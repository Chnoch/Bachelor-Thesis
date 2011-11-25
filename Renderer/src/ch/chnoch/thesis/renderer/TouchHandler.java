package ch.chnoch.thesis.renderer;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.util.Util;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchHandler implements OnTouchListener {

	private boolean mOnNode = false;
	private boolean mIsTranslation = false;
	private boolean mRotate = false;
	private boolean mUpScaled = false;

	private float mPreviousX, mPreviousY;
	private long mEventStart, mEventEnd;

	private RenderContext mRenderer;
	private SceneManagerInterface mSceneManager;

	private Trackball mTrackball;
	private Plane mPlane;
	private RayShapeIntersection mIntersection;

	private GLViewer mViewer;

	private ScaleGestureDetector mScaleDetector;

	private boolean mRotationDisabled;

	private final float TOUCH_SCALE_FACTOR = 1;
	private final float TRANSLATION_SCALE_FACTOR = 1;

	private final String TAG = "TouchHandler";

	public TouchHandler(SceneManagerInterface sceneManager, RenderContext renderer, GLSurfaceView viewer,
			boolean disableRotation) {
		mSceneManager = sceneManager;
		mRenderer = renderer;
		mTrackball = new Trackball();
		mPlane = new Plane();
		mPlane.setNormal(new Vector3f(0, 0, 1));

		mRotationDisabled = disableRotation;

		mScaleDetector = new ScaleGestureDetector(viewer.getContext(),
				new ScaleListener());
		// runSimulation();
	}

	public boolean onTouch(View view, MotionEvent e) {
		// Let the gesture detector analyze the input first
		// mScaleDetector.onTouchEvent(e);
		Log.d(TAG, "onTouch");
		if (view instanceof GLViewer) {
			mViewer = (GLViewer) view;
		}

		float x = e.getX();
		float y = e.getY();

		y = view.getHeight() - y;

		switch (e.getAction()) {

		case MotionEvent.ACTION_DOWN:
			try {
				mEventStart = e.getEventTime();
				unproject(x, y);
				translatePhysics(x, y, 1);

				break;
			} catch (Exception exc) {
				// Log.e(TAG, exc.getMessage());
			}
			mViewer.requestRender();

		case MotionEvent.ACTION_MOVE:
			mEventEnd = e.getEventTime();
			if (!mOnNode) {
				unproject(x,y);
				translatePhysics(x, y, 1);
			}

			if (mOnNode && !mScaleDetector.isInProgress()) {
				float distance = (float) Math.sqrt(Math.pow(mPreviousX - x, 2)
						+ Math.pow(mPreviousY - y, 2));
				// Log.d("TouchHandler", "Distance: " + distance);

				// if (mEventEnd - mEventStart > 300
				// || (mIsTranslation && !mRotate) || mRotationDisabled) {
				if (mRotationDisabled) {
					Log.d("TouchHandler", "Moving Object");
					// translate(x, y);
					translatePhysics(x, y, 0);

				} else if (distance > 0.1f) {
					// Short press: Rotate object
					// Log.d("TouchHandler", "Rotating Object");
					try {
						rotate(x, y);
					} catch (Exception exc) {
						// Log.e(TAG, exc.getMessage());
					}

				}

				mViewer.requestRender();
			}
			mPreviousX = x;
			mPreviousY = y;
			mEventStart = e.getEventTime();
			break;
		case MotionEvent.ACTION_UP:
			// reset all flags
			mIsTranslation = false;
			mOnNode = false;
			mRotate = false;

			translatePhysics(x, y, 2);

			if (mUpScaled) {
				mIntersection.node
						.setScale(mIntersection.node.getScale() - 0.1f);
				mUpScaled = false;
				mViewer.requestRender();
			}
			break;

		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}

	public void setTrackball(Trackball trackball) {
		mTrackball = trackball;
	}

	private void unproject(float x, float y) {
		Ray ray = mViewer.unproject(x, y);
		// Log.d("TouchHandler", "Ray: " + ray.toString());

		RayShapeIntersection intersect = mRenderer.getSceneManager()
				.intersectRayNode(ray);

		if (intersect.hit) {
			mIntersection = intersect;
			mOnNode = true;
		} else {
			mOnNode = false;
		}
	}

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

			Log.d("TouchHandler",
					"Moving from " + startIntersection.hitPoint.toString()
							+ " to " + endIntersection.hitPoint.toString());

			mPlane.update(endIntersection.hitPoint, startIntersection.hitPoint);

			mIsTranslation = true;

			// if (!mUpScaled) {
			if (false) {
				mIntersection.node
						.setScale(mIntersection.node.getScale() + 0.1f);
				mUpScaled = true;
			}
		} catch (Exception exc) {
			// Log.e(TAG, exc.getMessage());
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param mode
	 *            1: start, 2: end, 0: common
	 */
	private void translatePhysics(float x, float y, int mode) {
		Ray curRay = mViewer.unproject(x, y);
		switch (mode) {
		case 0:
			// common action

			Ray prevRay = mViewer.unproject(mPreviousX, mPreviousY);
			RayShapeIntersection startIntersection = mPlane.intersect(prevRay);
			RayShapeIntersection endIntersection = mPlane.intersect(curRay);
			mPlane.update(endIntersection.hitPoint, startIntersection.hitPoint);
			Log.d("TouchHandler",
					"Moving from " + startIntersection.hitPoint.toString()
							+ " to " + endIntersection.hitPoint.toString());
			break;
		case 1:
			// start of translation
			RayShapeIntersection hitPointInter = mIntersection.node
					.intersect(curRay);

			mPlane.setPointOnPlane(hitPointInter.hitPoint);
			mPlane.setNode(mIntersection.node);
		case 2:
			// end of translation
			mSceneManager.destroyJoints();
		}
	}

	private void rotate(float x, float y) {
		mTrackball.setNode(mIntersection.node);

		Ray startRay = mViewer.unproject(mPreviousX, mPreviousY);
		Ray endRay = mViewer.unproject(x, y);

		// Log.d("TouchHandler",
		// "startRay: " + startRay.toString());
		// Log.d("TouchHandler", "endRay: " + endRay.toString());
		RayShapeIntersection startIntersection = mTrackball.intersect(startRay);
		RayShapeIntersection endIntersection = mTrackball.intersect(endRay);

		mTrackball.update(startIntersection.hitPoint, endIntersection.hitPoint,
				TOUCH_SCALE_FACTOR);
		mRotate = true;
	}

	/*
	 * Private Methods
	 */
	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		float mScaleFactor = 1;

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (mIntersection != null) {
				mScaleFactor *= detector.getScaleFactor();
				mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
				// Log.d("TouchHandler", "ScaleFactor: " + mScaleFactor);

				mIntersection.node.setScale(mScaleFactor);
				mViewer.requestRender();
			}
			return true;
		}

	}

}
