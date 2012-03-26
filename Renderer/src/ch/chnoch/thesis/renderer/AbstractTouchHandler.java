package ch.chnoch.thesis.renderer;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

/**
 * The AbstractTouchHandler contains many fields and operations that an
 * OnTouchListener can use. Every implementation of a class that implements
 * OnTouchListener should extend this class to benefit from these operations.
 */
public abstract class AbstractTouchHandler implements OnTouchListener {

	private static final String TAG = "AbstractTouchHandler";

	/** The threshold for a zoom operation. */
	protected static final float ZOOM_THRESHOLD = 10;

	/** The threshold for a rotation operation. */
	protected static final float ROTATION_THRESHOLD = 0.15f;

	/** The constant zoom factor that is 1/10th. */
	protected static final float ZOOM_FACTOR = 0.1f;

	/** A boolean indicating whether a touch event is on a node. */
	protected boolean mOnNode = false;

	/** A boolean indicating whether a node has been temporarily upscaled. */
	protected boolean mUpScaled = false;

	/** A boolean indicating whether a multitouch operation is ongoing. */
	protected boolean mMultitouch = false;

	/**
	 * A boolean indicating whether we are currently choosing an object for the
	 * camera options.
	 */
	protected boolean mSetObjectForCameraFlag = false;

	/** The camera mode. */
	protected CameraMode mCameraMode;

	/**
	 * The previous x and y values and the previous degree from the multitouch
	 * gesture.
	 */
	protected float mPreviousX, mPreviousY, mPreviousDegree = Float.MIN_VALUE;

	/** The time of the event start and event end. */
	protected float mEventStart, mEventEnd;

	/** The render context. */
	protected RenderContext mRenderer;

	/** The scene manager. */
	protected SceneManagerInterface mSceneManager;

	/** A trackball that is used to rotate a node. */
	protected Trackball mTrackball;

	/** A trackball that is used to rotate the world. */
	protected WorldTrackball mWorldTrackball;

	/** A plane that is used to translate a node. */
	protected Plane mPlane;

	/**
	 * A intersection of a ray with a shape which is used for picking the right
	 * nodes.
	 */
	protected RayShapeIntersection mIntersection;

	/** The viewer. */
	protected GLViewer mViewer;

	/** The distance between two fingers. */
	protected float mTwoFingerDistance;

	/** The scale factor of a node. */
	protected float mScaleFactor;

	/** The multitouch mode. */
	protected MultitouchMode mMultitouchMode = MultitouchMode.NONE;

	/** The event count that is used to aggregate several events. */
	protected int mEventCount;

	/** A boolean indicating whether to update the location. */
	protected boolean mUpdateLocation = true;

	/**
	 * Instantiates a new abstract touch handler.
	 * 
	 * @param sceneManager
	 *            the scene manager
	 * @param renderer
	 *            the renderer
	 * @param viewer
	 *            the viewer
	 * @param cameraMode
	 *            the camera mode
	 */
	public AbstractTouchHandler(SceneManagerInterface sceneManager,
			RenderContext renderer, GLViewer viewer, CameraMode cameraMode) {
		mSceneManager = sceneManager;
		mRenderer = renderer;
		mTrackball = new Trackball();
		mWorldTrackball = new WorldTrackball();
		mPlane = new Plane(mSceneManager.getCamera());

		mCameraMode = cameraMode;

		mViewer = viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@Override
	public abstract boolean onTouch(View view, MotionEvent e);

	/**
	 * Selects an object for camera movement.
	 */
	public void selectObjectForCameraMovement() {
		if (mCameraMode == CameraMode.OBJECT_CENTRIC) {
			mSetObjectForCameraFlag = true;
		}
	}

	/**
	 * Sets the camera mode.
	 * 
	 * @param mode
	 *            the new camera mode
	 */
	public void setCameraMode(CameraMode mode) {
		mCameraMode = mode;
		mMultitouchMode = MultitouchMode.NONE;
	}

	/*
	 * ----------------------------------------------------------------
	 * PROTECTED METHODS
	 * ----------------------------------------------------------------
	 */
	/**
	 * Make a rotation.
	 * 
	 * @param e
	 *            the Motion event
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	protected abstract void makeRotation(MotionEvent e, float x, float y);

	/**
	 * Unproject an x,y value from the screen into a ray in the 3D scene.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
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

	/**
	 * Move the world resp. the camera according to the MultitouchMode.
	 * 
	 * @param e
	 *            the Motion event
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	protected void multitouchMove(MotionEvent e, float x, float y) {
		Log.d("WorldTrackball",
				"Eventvalues x: " + e.getX() + " y: " + e.getY()
						+ " passed values: x: " + x + " y: " + y);
		if (mMultitouchMode.equals(MultitouchMode.NONE)) {
			mMultitouchMode = testMultitouch(e);
		}
		switch (mMultitouchMode) {
		case ROTATE:
			this.rotateWorldOriginCentric(e, x, y);
			break;
		case ROTATE_CAMERA_CENTRIC:
			this.rotateWorldCameraCentric(e, x, y);
			break;
		case ZOOM_ORIGIN:
			zoomOrigin(e);
			break;
		case NONE:
			break;
		}
	}

	/**
	 * A helper method that should always be called whenever a new pointer (i.e.
	 * finger) on the screen is detected.
	 * 
	 * @param e
	 *            the Motion event
	 */
	protected void actionPointerDown(MotionEvent e) {
		mOnNode = false;
		mEventCount = 0;
		mTwoFingerDistance = calculateTwoFingerDistance(e);
		mPreviousDegree = calculateAngle(e);
		mMultitouch = true;
	}

	/**
	 * A helper method that should always be called whenever a pointer (i.e.
	 * finger) on the screen is removed.
	 */
	protected void actionPointerUp() {
		mMultitouchMode = MultitouchMode.NONE;
		mPreviousDegree = Float.MIN_VALUE;
	}

	/**
	 * A helper method that should always be called whenever all pointers (i.e.
	 * finger) on the screen have been removed.
	 */
	protected void actionUp() {
		mOnNode = false;
		mMultitouch = false;
		mEventCount = 0;

		if (mUpScaled) {
			mIntersection.node.setScale(mIntersection.node.getScale() - 0.1f);
			mUpScaled = false;
		}
	}

	/**
	 * Finalizes the touch method when no finger is touching the screen anymore.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	protected void finalizeOnTouch(float x, float y) {
		if (mUpdateLocation) {
			mPreviousX = x;
			mPreviousY = y;
		} else {
			mUpdateLocation = true;
		}

		mViewer.requestRender();
	}


	/**
	 * Zoom the camera in the direction where it's looking at currently.
	 * 
	 * @param e
	 *            the corresponding Motion event
	 */
	protected void zoom(MotionEvent e) {
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
			moveVector.scale(-mScaleFactor * ZOOM_FACTOR);

			Log.d(TAG, "MoveVector: " + moveVector.toString());
			Vector3f centerOfProjection = camera.getCenterOfProjection();
			Vector3f lookAtPoint = camera.getLookAtPoint();
			centerOfProjection.add(moveVector);
			lookAtPoint.add(moveVector);
			camera.setCenterOfProjection(centerOfProjection);
			camera.setLookAtPoint(lookAtPoint);
		}
	}

	/**
	 * Zoom the camera towards the origin.
	 * 
	 * @param e
	 *            the corresponding Motion event
	 */
	protected void zoomOrigin(MotionEvent e) {
		float dist = calculateTwoFingerDistance(e);
		if (dist > -1) {
			mScaleFactor = dist / mTwoFingerDistance;
			mTwoFingerDistance = dist;
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
			Log.d(TAG, "Scale Factor: " + mScaleFactor);
			Camera camera = mSceneManager.getCamera();
			Vector3f centerOfProjection = camera.getCenterOfProjection();

			centerOfProjection.scale(1f / mScaleFactor);
			camera.setCenterOfProjection(centerOfProjection);
		}
	}

	/**
	 * Rotate the world around the origin.
	 * 
	 * @param e
	 *            the Motion event
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	protected void rotateWorldOriginCentric(MotionEvent e, float x, float y) {
		Log.d(TAG, "rotateWorldOriginCentric");
		mWorldTrackball.setNode(mSceneManager.getRoot(),
				mSceneManager.getCamera(), false);

		this.makeRotation(e, x, y);
	}

	/**
	 * Rotates the camera around itself.
	 * 
	 * @param e
	 *            the Motion event
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	protected void rotateWorldCameraCentric(MotionEvent e, float x, float y) {
		mWorldTrackball.setNode(mSceneManager.getRoot(),
				mSceneManager.getCamera(), true);

		this.makeRotation(e, x, y);
	}


	/**
	 * Rotate the camera around the z-axis.
	 * 
	 * @param e
	 *            the Motion event
	 */
	protected void rotateCamera(MotionEvent e) {
		if (e.getPointerCount() > 1) {
			float angle = calculateAngle(e);
			if (!(mPreviousDegree == Float.MIN_VALUE)) {
				Camera camera = mSceneManager.getCamera();
				Vector3f upVector = camera.getUpVector();
				Matrix4f rot = new Matrix4f();
				Vector3f cameraDirection = new Vector3f(camera.getLookAtPoint());
				cameraDirection.sub(camera.getCenterOfProjection());
				rot.set(new AxisAngle4f(cameraDirection, mPreviousDegree
						- angle));
				rot.transform(upVector);
				mSceneManager.getCamera().setUpVector(upVector);
			}
			mPreviousDegree = angle;
		}
	}

	/**
	 * Moves the camera around the origin.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	protected void moveCamera(float x, float y) {
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
				endIntersection.hitPoint);
	}

	/*
	 * -----------------------------------------------------------------------
	 * PRIVATE METHODS
	 * -----------------------------------------------------------------------
	 */

	/**
	 * Test for which multitouch mode is most appropriate given the gesture.
	 * 
	 * @param e
	 *            the corresponding Motion event
	 * @return the correct multitouch mode
	 */
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
				float dist = Math.abs(newDist - mTwoFingerDistance);
				float angle = Math.abs(newAngle - mPreviousDegree);

				switch (mCameraMode) {
				case ORIGIN_CENTRIC:
				case OBJECT_CENTRIC:
					if (angle > ROTATION_THRESHOLD || dist < ZOOM_THRESHOLD) {
						Log.d(TAG, "RotateCamera");
						return MultitouchMode.ROTATE;
					} else {
						Log.d(TAG, "ZoomOrigin");
						return MultitouchMode.ZOOM_ORIGIN;
					}
				case CAMERA_CENTRIC:
					return MultitouchMode.ROTATE_CAMERA_CENTRIC;
				default:
					return MultitouchMode.NONE;
				}

			} else {
				return MultitouchMode.NONE;
			}
		}

	}

	/**
	 * Calculates the distance between two finger.
	 * 
	 * @param e
	 *            the Motion event
	 * @return the distance
	 */
	private float calculateTwoFingerDistance(MotionEvent e) {
		if (e.getPointerCount() > 1) {
			float x = e.getX(0) - e.getX(1);
			float y = e.getY(0) - e.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		} else {
			return -1;
		}
	}

	/**
	 * Calculates the angle from the two fingers to a horizontal axis.
	 * 
	 * @param e
	 *            the Motion event
	 * @return the angle
	 */
	private float calculateAngle(MotionEvent e) {
		if (e.getPointerCount() > 1) {
			float distX = e.getX(0) - e.getX(1);
			float distY = e.getY(0) - e.getY(1);

			return (float) Math.atan2(distY, distX);
		} else {
			return Float.MIN_VALUE;
		}
	}

	/**
	 * The different multitouch modes.
	 */
	protected enum MultitouchMode {

		/** Zoom the camera towards the origin */
		ZOOM_ORIGIN,
		/** Rotate the camera around the origin */
		ROTATE,
		/** Rotate the camera camera-centric. */
		ROTATE_CAMERA_CENTRIC,
		/** No multitouch */
		NONE
	}

	/**
	 * The different camera modes.
	 */
	public enum CameraMode {

		/** Camera centric */
		CAMERA_CENTRIC,
		/** Object centric */
		OBJECT_CENTRIC,
		/** Origin centric */
		ORIGIN_CENTRIC
	}

}
