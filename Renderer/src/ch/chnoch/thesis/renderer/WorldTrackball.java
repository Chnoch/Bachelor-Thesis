package ch.chnoch.thesis.renderer;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.Node;

public class WorldTrackball extends Trackball {

	private static final String TAG = "WorldTrackball";

	private boolean mCameraCentric;

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

	public boolean update(Vector3f cur, Vector3f prev) {
		if (cur.equals(prev)) {
			Log.d(TAG, "Cur und Prev too similar; no update");
			return false;
		}
		float factor = 1;
		// factor *= -1;
		if (mCameraCentric)
			factor *= -1;

		AxisAngle4f axisAngle = getAxisAngle(cur, prev, factor);
		Matrix4f rot = new Matrix4f();
		rot.set(axisAngle);
		if (Float.isNaN(rot.m00)) {
			Log.d(TAG, "Illegal arguments: Cur: " + cur.toString()
					+ " prev: " + prev.toString() + " factor: " + factor);
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
