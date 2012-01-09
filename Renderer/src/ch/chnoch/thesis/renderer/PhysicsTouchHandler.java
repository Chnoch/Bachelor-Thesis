package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class PhysicsTouchHandler extends AbstractTouchHandler {

	private static float SCALE_FACTOR = 1f;
	private float mTwoFingerDistance;
	private float mScaleFactor;
	private MultitouchMode mMultitouchMode = MultitouchMode.NONE;
	private int mEventCount;
	private boolean mUpdateLocation = true;

	private static final String TAG = "PhysicsTouchHandler";
	private static final float ZOOM_THRESHOLD = 10;

	public PhysicsTouchHandler(SceneManagerInterface sceneManager,
			RenderContext renderer, GLSurfaceView viewer) {
		super(sceneManager, renderer, viewer);
		mPlane.set2DMode(true);
	}

	@Override
	public boolean onTouch(View view, MotionEvent e) {
		int action = e.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		// Let the gesture detector analyze the input first
		// Log.d(TAG, "onTouch");

		float x = e.getX();
		float y = e.getY();
		y = view.getHeight() - y;

		switch (actionCode) {

		case MotionEvent.ACTION_DOWN:
			mEventStart = e.getEventTime();
			unproject(x, y);
			findNode(x, y);

			break;
		case MotionEvent.ACTION_MOVE:
			mEventEnd = e.getEventTime();
			if (mMultitouch) {
				// Log.d(TAG, "Multitouch");
				if (mMultitouchMode.equals(MultitouchMode.NONE)) {
					mMultitouchMode = testMultitouch(e);
				}
				switch (mMultitouchMode) {
				case ROTATE:
					Log.d(TAG, "Rotating World, x: " + x + " y: " + y);
					rotateWorld(x, y);
					break;
				case ZOOM:
					zoom(e);
					break;
				case NONE:
					break;
				}
			} else {
				if (!mOnNode) {
					unproject(x, y);
					findNode(x, y);
				}

				if (mOnNode) {
					moveNode(x, y);
				}
			}
			mEventStart = e.getEventTime();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.d(TAG, "ACTION_POINTER_DOWN");
			mOnNode = false;
			mEventCount = 0;
			mTwoFingerDistance = calculateTwoFingerDistance(e);
			endTranslation();
			mMultitouch = true;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.d(TAG, "ACTION_POINTER_UP");
			mMultitouchMode = MultitouchMode.NONE;
		case MotionEvent.ACTION_UP:
			// reset all flags
			mOnNode = false;
			mMultitouch = false;
			mEventCount = 0;

			endTranslation();
			break;

		}
		if (mUpdateLocation) {
			mPreviousX = x;
			mPreviousY = y;
		} else {
			mUpdateLocation = true;
		}
		mViewer.requestRender();
		return true;
	}

	private MultitouchMode testMultitouch(MotionEvent e) {
		// Wait several events until the multitouch decision is made.
		if (mEventCount < 5) {
			Log.d(TAG, "Event count: " + mEventCount);
			mEventCount++;
			return MultitouchMode.NONE;
		} else {
			float newDist = calculateTwoFingerDistance(e);
			Log.d(TAG, "Original dist: " + mTwoFingerDistance);
			Log.d(TAG, "New dist: " + newDist);
			float dist = Math.abs(newDist - mTwoFingerDistance);
			Log.d(TAG, "TwoFingerDistance: " + dist);
			if (dist < ZOOM_THRESHOLD) {
				Log.d(TAG, "Rotate");
				return MultitouchMode.ROTATE;
			} else {
				Log.d(TAG, "Zoom");
				return MultitouchMode.ZOOM;
			}
		}
	}

	private float calculateTwoFingerDistance(MotionEvent e) {
		float x = e.getX(0) - e.getX(1);
		float y = e.getY(0) - e.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param mode
	 *            1: start, 2: end, 0: common
	 */
	private void moveNode(float x, float y) {
		Ray curRay = mViewer.unproject(x, y);
		Ray prevRay = mViewer.unproject(mPreviousX, mPreviousY);

		RayShapeIntersection startIntersection = mPlane.intersect(prevRay);
		RayShapeIntersection endIntersection = mPlane.intersect(curRay);

		mPlane.update(endIntersection.hitPoint, startIntersection.hitPoint);
	}

	private void findNode(float x, float y) {
		if (mOnNode) {
			mPlane.setPointOnPlane(mIntersection.hitPoint);
			mPlane.setNode(mIntersection.node);
		}
	}

	private void rotateWorld(float x, float y) {
		mTrackball.setNodeToRoot(mSceneManager.getRoot(),
				mSceneManager.getCamera());

		Ray startRay = mViewer.unproject(mPreviousX, mPreviousY);
		Ray endRay = mViewer.unproject(x, y);

		Log.d(TAG, "Previous x: " + mPreviousX + " y: " + mPreviousY);
		
		RayShapeIntersection startIntersection = mTrackball.intersect(startRay);
		RayShapeIntersection endIntersection = mTrackball.intersect(endRay);

		Log.d(TAG, "startIntersection: " + startIntersection.toString());
		Log.d(TAG, "endIntersection: " + endIntersection.toString());
		
		
		mUpdateLocation = mTrackball.update(startIntersection.hitPoint, endIntersection.hitPoint,
				TOUCH_SCALE_FACTOR);
	}

	private void zoom(MotionEvent e) {
		float dist = calculateTwoFingerDistance(e);
		mScaleFactor = dist / mTwoFingerDistance;
		mTwoFingerDistance = dist;
		mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
		Log.d(TAG, "Scale Factor: " + mScaleFactor);
		Camera camera = mSceneManager.getCamera();
		Vector3f centerOfProjection = camera.getCenterOfProjection();

		centerOfProjection.scale(1f/mScaleFactor);
		camera.setCenterOfProjection(centerOfProjection);
	}
	
	private void endTranslation() {
		mSceneManager.destroyJoints();
	}

	private enum MultitouchMode {
		ZOOM, ROTATE, NONE
	}
}
