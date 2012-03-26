package ch.chnoch.thesis.renderer;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.Node;

/**
 * A WorldTrackball is used to rotate the camera. The world is simulated as a
 * trackball and therefore a rotation can be applied to the camera. The way the
 * rotation works depends on the camera mode, that is whether the user
 * interaction is camera centric or object, resp. origin centric.
 */
public class WorldTrackball extends Trackball {

	private static final String TAG = "WorldTrackball";

	private boolean mCameraCentric;

	/**
	 * Sets the trackball to reflect the the properties of the world correctly.
	 * 
	 * @param root
	 *            the root of the scene manager
	 * @param camera
	 *            the camera
	 * @param cameraCentric
	 *            a boolean indicating whether the rotation is camera centric or
	 *            world centric
	 */
	public void setNode(Node root, Camera camera, boolean cameraCentric) {
		mNode = root;
		mCamera = camera;
		mCameraCentric = cameraCentric;
		initNode();
	}

	private void initNode() {
		if (mCameraCentric) {
			mCenter = new Vector3f(mCamera.getCenterOfProjection());
			mRadius = mCamera.getCenterOfProjection().length();
		} else {
			Vector3f rad = new Vector3f(mCamera.getLookAtPoint());
			rad.sub(mCamera.getCenterOfProjection());
			mCenter = new Vector3f(new Vector3f(0, 0, 0));
			mRadius = rad.length() + 1;
			Log.d(TAG, "Center: " + mCenter.toString() + " radius: " + mRadius);
		}
	}

	/**
	 * Updates the camera to reflect the changes from the trackball.
	 * 
	 * @param target
	 *            the target position of the camera
	 * @param current
	 *            the current position of the camera
	 * @return true, if the rotation is successful
	 */
	public boolean update(Vector3f target, Vector3f current) {
		if (target.equals(current)) {
			Log.d(TAG, "Cur und Prev too similar; no update");
			return false;
		}
		float factor = 1;
		// factor *= -1;
		if (mCameraCentric)
			factor *= -1;

		AxisAngle4f axisAngle = getAxisAngle(target, current, factor);
		Matrix4f rot = new Matrix4f();
		rot.set(axisAngle);
		if (Float.isNaN(rot.m00)) {
			Log.d(TAG, "Illegal arguments: Cur: " + target.toString()
					+ " prev: " + current.toString() + " factor: " + factor);
			Log.d(TAG, "Rotation Matrix: " + rot.toString());
			return false;
		}

		if (mCameraCentric) {
			Log.d(TAG, "Camera before: " + mCamera.getLookAtPoint().toString());
			Vector3f camera = new Vector3f(mCamera.getLookAtPoint());
			camera.sub(mCamera.getCenterOfProjection());
			rot.transform(camera);
			camera.add(mCamera.getCenterOfProjection());
			mCamera.setLookAtPoint(camera);
			Log.d(TAG, "Camera after: " + mCamera.getLookAtPoint().toString());
		} else {
			// Log.d(TAG, "Camera before: "
			// + mCamera.getCenterOfProjection().toString());
			Log.d(TAG, "Rot: " + rot.toString());
			Vector3f camera = mCamera.getCenterOfProjection();
			rot.transform(camera);
			rot.transform(mCamera.getUpVector());
			mCamera.setCenterOfProjection(camera);
			// Log.d(TAG, "Camera after: "
			// + mCamera.getCenterOfProjection().toString());
		}
		return true;
	}
}
