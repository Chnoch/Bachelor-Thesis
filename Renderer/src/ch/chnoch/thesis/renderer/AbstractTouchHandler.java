package ch.chnoch.thesis.renderer;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

public abstract class AbstractTouchHandler implements OnTouchListener {

	private static final String TAG = "AbstractTouchHandler";
	protected static final float ZOOM_THRESHOLD = 10;
	protected static final float ROTATION_THRESHOLD = 0.2f;
	protected static final float WORLD_ROTATE_FACTOR = 2f;
	protected static final float CAMERA_ROTATION_FACTOR = 0.01f;
	protected static final float ZOOM_FACTOR = 0.3f;

	protected boolean mOnNode = false;
	protected boolean mUpScaled = false;
	protected boolean mMultitouch = false;

	protected float mPreviousX, mPreviousY, mPreviousDegree = Float.MIN_VALUE;
	protected float mEventStart, mEventEnd;

	protected RenderContext mRenderer;
	protected SceneManagerInterface mSceneManager;

	protected Trackball mTrackball;
	protected WorldTrackball mWorldTrackball;
	protected Plane mPlane;
	protected RayShapeIntersection mIntersection;

	protected GLViewer mViewer;

	protected ScaleGestureDetector mScaleDetector;

	protected float mTwoFingerDistance;
	protected float mScaleFactor;
	protected MultitouchMode mMultitouchMode = MultitouchMode.NONE;
	protected int mEventCount;
	protected boolean mUpdateLocation = true;

	protected final float TOUCH_SCALE_FACTOR = 1;

	public AbstractTouchHandler(SceneManagerInterface sceneManager,
			RenderContext renderer, GLViewer viewer) {
		mSceneManager = sceneManager;
		mRenderer = renderer;
		mTrackball = new Trackball();
		mWorldTrackball = new WorldTrackball();
		mPlane = new Plane(mSceneManager.getCamera());

		mViewer = viewer;
	}

	@Override
	public abstract boolean onTouch(View view, MotionEvent e);

	protected void unproject(float x, float y) {
		Ray ray = mViewer.unproject(x, y);

		RayShapeIntersection intersect = mRenderer.getSceneManager()
				.intersectRayNode(ray);

		if (intersect.hit) {
			mIntersection = intersect;
			mOnNode = true;
		} else {
			mOnNode = false;
		}
	}

	protected void multitouchMove(MotionEvent e, float x, float y) {
		if (mMultitouchMode.equals(MultitouchMode.NONE)) {
			mMultitouchMode = testMultitouch(e);
		}
		switch (mMultitouchMode) {
		case ROTATE:
			// Log.d(TAG, "Rotating World, x: " + x + " y: " + y);
			this.rotateWorld(x, y);
			break;
		case ZOOM:
			zoom(e);
			break;
		case ROTATE_CAMERA:
			rotateCamera(e);
			break;
		case NONE:
			break;
		}
	}

	protected void actionPointerDown(MotionEvent e) {
		mOnNode = false;
		mEventCount = 0;
		mTwoFingerDistance = calculateTwoFingerDistance(e);
		mPreviousDegree = calculateAngle(e);
		mMultitouch = true;
	}

	protected void actionPointerUp() {
		mMultitouchMode = MultitouchMode.NONE;
		mPreviousDegree = Float.MIN_VALUE;
	}

	protected void actionUp() {
		mOnNode = false;
		mMultitouch = false;
		mEventCount = 0;

		if (mUpScaled) {
			mIntersection.node.setScale(mIntersection.node.getScale() - 0.1f);
			mUpScaled = false;
		}
	}

	protected void finalizeOnTouch(float x, float y) {
		if (mUpdateLocation) {
			mPreviousX = x;
			mPreviousY = y;
		} else {
			mUpdateLocation = true;
		}
		mViewer.requestRender();
	}

	private MultitouchMode testMultitouch(MotionEvent e) {

		// Wait several events until the multitouch decision is made.
		if (mEventCount < 5) {
			Log.d(TAG, "Event count: " + mEventCount);
			mEventCount++;
			return MultitouchMode.NONE;
		} else {
			float newDist = calculateTwoFingerDistance(e);
			float newAngle = calculateAngle(e);
			if (newDist > -1) {
				Log.d(TAG, "Original dist: " + mTwoFingerDistance);
				Log.d(TAG, "New dist: " + newDist);
				float dist = Math.abs(newDist - mTwoFingerDistance);
				Log.d(TAG, "TwoFingerDistance: " + dist);
				float angle = Math.abs(newAngle - mPreviousDegree);
				Log.d(TAG, "Angle difference: " + (newAngle - mPreviousDegree));

				if (angle > ROTATION_THRESHOLD) {
					Log.d(TAG, "RotateCamera");
					return MultitouchMode.ROTATE_CAMERA;
				} else if (dist < ZOOM_THRESHOLD) {
					Log.d(TAG, "Rotate");
					return MultitouchMode.ROTATE;
				} else {
					Log.d(TAG, "Zoom");
					return MultitouchMode.ZOOM;
				}
			} else {
				return MultitouchMode.NONE;
			}
		}

	}

	private void zoom(MotionEvent e) {
		float dist = calculateTwoFingerDistance(e);
		if (dist > -1) {
			mScaleFactor = (dist - mTwoFingerDistance);
			mTwoFingerDistance = dist;
			// mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
			Log.d(TAG, "Scale Factor: " + mScaleFactor);
			Camera camera = mSceneManager.getCamera();
			Vector3f moveVector = new Vector3f(camera.getCenterOfProjection());
			moveVector.sub(camera.getLookAtPoint());

			moveVector.normalize();
			moveVector.scale(mScaleFactor);

			Log.d(TAG, "MoveVector: " + moveVector.toString());
			Vector3f centerOfProjection = camera.getCenterOfProjection();
			Vector3f lookAtPoint = camera.getLookAtPoint();
			centerOfProjection.add(moveVector);
			lookAtPoint.add(moveVector);
			camera.setCenterOfProjection(centerOfProjection);
			camera.setLookAtPoint(lookAtPoint);
		}
	}

	private float calculateTwoFingerDistance(MotionEvent e) {
		if (e.getPointerCount() > 1) {
			float x = e.getX(0) - e.getX(1);
			float y = e.getY(0) - e.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		} else {
			return -1;
		}
	}

	private float calculateAngle(MotionEvent e) {
		if (e.getPointerCount() > 1) {
			float distX = e.getX(0) - e.getX(1);
			float distY = e.getY(0) - e.getY(1);

			return (float) Math.atan2(distY, distX);
		} else {
			return Float.MIN_VALUE;
		}
	}

	protected void rotateWorld(float x, float y) {
		mWorldTrackball.setNode(mSceneManager.getRoot(),
				mSceneManager.getCamera(), false);

		Ray startRay = mViewer.unproject(mPreviousX, mPreviousY);
		Ray endRay = mViewer.unproject(x, y);

		Log.d(TAG, "Previous x: " + mPreviousX + " y: " + mPreviousY);

		RayShapeIntersection startIntersection = mWorldTrackball
				.intersect(startRay);
		RayShapeIntersection endIntersection = mWorldTrackball
				.intersect(endRay);

		Log.d(TAG, "startIntersection: " + startIntersection.toString());
		Log.d(TAG, "endIntersection: " + endIntersection.toString());

		mUpdateLocation = mWorldTrackball.update(startIntersection.hitPoint,
				endIntersection.hitPoint, WORLD_ROTATE_FACTOR);
	}

	protected void rotateCamera(MotionEvent e) {
		if (e.getPointerCount() > 1) {
			float angle = calculateAngle(e);
			if (!(mPreviousDegree == Float.MIN_VALUE)) {
				Vector3f upVector = mSceneManager.getCamera().getUpVector();
				Matrix4f rot = new Matrix4f();
				rot.set(new AxisAngle4f(new Vector3f(0, 0, 1), angle
						- mPreviousDegree));
				rot.transform(upVector);
				mSceneManager.getCamera().setUpVector(upVector);
			}
			mPreviousDegree = angle;
		}
	}

	protected enum MultitouchMode {
		ZOOM, ROTATE, ROTATE_CAMERA, NONE
	}

}
