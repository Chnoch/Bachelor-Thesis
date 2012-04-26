package ch.chnoch.thesis.renderer;

import java.util.Timer;
import java.util.TimerTask;

import javax.vecmath.Vector3f;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import ch.chnoch.thesis.renderer.interfaces.RendererInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

/**
 * This is a basic touch handler that is used for interacting with scenes that
 * don't have support for a physics engine. It supports several different UI
 * interactions. It uses one-finger touch to interact with objects (Short touch
 * and moving for rotation and long touch and moving for translation.)
 * Multitouch is used for camera movement. There are several camera modes that
 * are supported (see {@link MultitouchMode} for a detailed list). <br>
 * The most important method of this class is onTouch, which is a callback
 * method from the Android framework that is called every time a touch event
 * occurs. All the logic and differentiation between the touch events occur
 * within this method. <br>
 * TouchHandler can be used directly in in applications, can be extended or only
 * used as a guideline. See {@link AbstractTouchHandler} for implementations of
 * general helper methods that a touch handler can use.
 */
public class TouchHandler extends AbstractTouchHandler {

	private boolean mIsTranslation = false;

	private boolean mRotate = false;

	private static final float ROTATION_THRESHOLD = 0.15f;

	private static final long TRANSLATION_DELAY = 300;

	private static final String TAG = "TouchHandler";

	/**
	 * Instantiates a new touch handler and sets up all the necessary objects.
	 * 
	 * @param sceneManager
	 *            the scene manager
	 * @param renderer
	 *            the renderer that is used for drawing
	 * @param viewer
	 *            the object representing the surface
	 * @param cameraMode
	 *            the camera mode that is used for camera interaction
	 */
	public TouchHandler(SceneManagerInterface sceneManager,
			RendererInterface renderer, GLViewer viewer, CameraMode cameraMode) {
		super(sceneManager, renderer, viewer, cameraMode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.AbstractTouchHandler#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	public boolean onTouch(View view, MotionEvent e) {
		int action = e.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		Log.d(TAG, "ActionCode: " + actionCode);
		float x = e.getX();
		float y = e.getY();

		y = view.getHeight() - y;

		if (setCameraToObject(x, y)) {
			return true;
		}

		switch (actionCode) {

		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "ACTION_DOWN");
			try {
				mEventStart = e.getEventTime();
				unproject(x, y);
				startAsyncScaler();
				break;
			} catch (Exception exc) {
				break;
			}
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.d(TAG, "ACTION_POINTER_DOWN");
			actionPointerDown(e);
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "ACTION_MOVE");
			mEventEnd = e.getEventTime();

			if (mMultitouch) {
				// Multitouch Action
				multitouchMove(e, x, y);
			} else if (mOnNode) {
				Log.d(TAG, "Singletouch");
				float distance = (float) Math.sqrt(Math.pow(mPreviousX - x, 2)
						+ Math.pow(mPreviousY - y, 2));

				if (mEventEnd - mEventStart > TRANSLATION_DELAY
						|| (mIsTranslation && !mRotate)) {
					// Long Press: Moving object
					translate(x, y);

				} else if (distance > ROTATION_THRESHOLD) {
					// Short press: Rotate object
					rotate(x, y);
				}
			} else {
				Log.d(TAG, "No movement");
			}
			mEventStart = e.getEventTime();
			break;
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "ACTION_UP");
			// reset all flags
			mIsTranslation = false;
			mRotate = false;
			actionUp();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.d(TAG, "ACTION_POINTER_UP");
			actionPointerUp();
			break;
		}
		finalizeOnTouch(x, y);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.AbstractTouchHandler#makeRotation(android.view
	 * .MotionEvent, float, float)
	 */
	protected void makeRotation(MotionEvent e, float x, float y) {
		rotateCamera(e);
		zoom(e);
		moveCamera(x, y);

	}

	/**
	 * Translates an object by the difference between the two last used
	 * coordinates using a {@link Plane}.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	private void translate(float x, float y) {
		try {
			// Long Press occured: Manipulate object by moving it
			Ray prevRay = mViewer.unproject(mPreviousX, mPreviousY);
			Ray curRay = mViewer.unproject(x, y);

			// mPlane = findClosestPlane(prevRay);
			if (mIntersection.node != mPlane.getNode()) {
				RayShapeIntersection hitPointInter = mIntersection.node
						.intersect(prevRay);

				mPlane.setPointOnPlane(hitPointInter.hitPoint);
				mPlane.setNode(mIntersection.node);
			}

			RayShapeIntersection startIntersection = mPlane.intersect(prevRay);
			RayShapeIntersection endIntersection = mPlane.intersect(curRay);

			mPlane.update(endIntersection.hitPoint, startIntersection.hitPoint);

			mIsTranslation = true;
		} catch (Exception exc) {
			// Log.e(TAG, exc.getMessage());
		}
	}

	/**
	 * Rotates an object by the difference between the two last used coordinates
	 * using a {@link Trackball}.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	private void rotate(float x, float y) {
		mTrackball.setNode(mIntersection.node);

		Ray startRay = mViewer.unproject(mPreviousX, mPreviousY);
		Ray endRay = mViewer.unproject(x, y);

		RayShapeIntersection startIntersection = mTrackball.intersect(startRay);
		RayShapeIntersection endIntersection = mTrackball.intersect(endRay);

		mTrackball.update(startIntersection.hitPoint, endIntersection.hitPoint);
		mRotate = true;
	}

	/**
	 * Sets the look at point of the camera to an object. The object is
	 * determined by the hitpoint of the coordinates. Only used if the camera
	 * mode is OBJECT_CENTRIC.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 * @return true, if succesful
	 */
	private boolean setCameraToObject(float x, float y) {
		if (mSetObjectForCameraFlag) {
			unproject(x, y);
			if (mOnNode) {
				mSceneManager.getCamera().setLookAtPoint(
						new Vector3f(mIntersection.node.getCenter()));
			} else {
				mSceneManager.getCamera().setLookAtPoint(new Vector3f(0, 0, 0));
			}
			mSetObjectForCameraFlag = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Starts the asynchronous scaler that tests whether a node needs to be
	 * scaled for translation.
	 */
	private void startAsyncScaler() {
		Log.d(TAG, "startAsyncScaler");
		Timer timer = new Timer();
		TimerTask task = new ScaleTask();
		timer.schedule(task, TRANSLATION_DELAY);
	}

	/**
	 * The asynchronous class that tests whether a rotation or translation of an
	 * object occurs and scales the node a little bit as soon as the
	 * TRANSLATION_DELAY is passed.
	 */
	private class ScaleTask extends TimerTask {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		public void run() {
			Log.d(TAG, "Running ScaleTask");
			if (!mMultitouch && !mRotate && mOnNode) {
				if (!mUpScaled) {
					Log.d(TAG, "Upscaling Node");
					mIntersection.node
							.setScale(mIntersection.node.getScale() + 0.1f);
					mUpScaled = true;
					mViewer.requestRender();
					mIsTranslation = true;
					mRotate = false;
				}
			}
		}
	}
}
