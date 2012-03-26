package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3f;

import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

/**
 * Stores the specification of a virtual camera. Holds a 4x4 camera matrix,
 * i.e., the world-to-camera transformation from intuitive parameters. The
 * transformation is specified by the center of projection (i.e. the where the
 * camera is located), a look at point (i.e. the direction where the camera is
 * looking at and an up vector (i.e. which direction of the 3D space points
 * upwards).
 * 
 * A scene manager (see {@link SceneManagerInterface}) usually stores a camera.
 */
public class Camera {

	private Matrix4f mCameraMatrix;

	private Vector3f mCenterOfProjection, mLookAtPoint, mUpVector;

	/**
	 * Construct a camera with a default camera matrix. The camera matrix
	 * corresponds to the world-to-camera transform. This default matrix places
	 * the camera at (0,0,15) in world space, facing towards the origin (0,0,0)
	 * of world space, i.e., towards the negative z-axis.
	 */
	public Camera() {
		reset();
	}

	/**
	 * Resets the camera properties.
	 */
	public void reset() {
		mCenterOfProjection = new Vector3f(0, 0, 15);
		mLookAtPoint = new Vector3f(0, 0, 0);
		mUpVector = new Vector3f(0, 1, 0);
		mCameraMatrix = new Matrix4f();
		this.update();
	}

	/**
	 * Return the camera matrix, i.e., the world-to-camera transform. For
	 * example, this is used by the renderer.
	 * 
	 * @return the 4x4 world-to-camera transform matrix
	 */
	public Matrix4f getCameraMatrix() {
		return mCameraMatrix;
	}

	/**
	 * Gets the center of projection.
	 * 
	 * @return the center of projection
	 */
	public Vector3f getCenterOfProjection() {
		return mCenterOfProjection;
	}

	/**
	 * Sets the center of projection.
	 * 
	 * @param centerOfProjection
	 *            the new center of projection
	 */
	public void setCenterOfProjection(Vector3f centerOfProjection) {
		this.mCenterOfProjection = centerOfProjection;
		this.update();
	}

	/**
	 * Gets the look at point.
	 * 
	 * @return the look at point
	 */
	public Vector3f getLookAtPoint() {
		return mLookAtPoint;
	}

	/**
	 * Sets the look at point.
	 * 
	 * @param lookAtPoint
	 *            the new look at point
	 */
	public void setLookAtPoint(Vector3f lookAtPoint) {
		this.mLookAtPoint = lookAtPoint;
		this.update();
	}

	/**
	 * Gets the up vector.
	 * 
	 * @return the up vector
	 */
	public Vector3f getUpVector() {
		return mUpVector;
	}

	/**
	 * Sets the up vector.
	 * 
	 * @param upVector
	 *            the new up vector
	 */
	public void setUpVector(Vector3f upVector) {
		this.mUpVector = upVector;
		this.update();
	}

	/**
	 * Updates the camera to reflect any changes in the parameters.
	 */
	public void update() {
		updateCamera();
	}


	/*
	 * PRIVATE METHODS
	 */

	/**
	 * A helper method to update the camera.
	 */
	private void updateCamera() {
		Vector3f x = new Vector3f();
		Vector3f y = new Vector3f();
		Vector3f z = new Vector3f();
		Vector3f temp = new Vector3f(this.mCenterOfProjection);

		temp.sub(this.mLookAtPoint);
		temp.normalize();
		z.set(temp);

		temp.set(this.mUpVector);
		temp.cross(temp, z);
		temp.normalize();
		x.set(temp);

		y.cross(z, x);

		Matrix4f newMatrix = new Matrix4f();
		newMatrix.setColumn(0, x.getX(), x.getY(), x.getZ(), 0);
		newMatrix.setColumn(1, y.getX(), y.getY(), y.getZ(), 0);
		newMatrix.setColumn(2, z.getX(), z.getY(), z.getZ(), 0);
		newMatrix.setColumn(3, mCenterOfProjection.getX(),
				mCenterOfProjection.getY(), mCenterOfProjection.getZ(), 1);
		try {
			newMatrix.invert();
			this.mCameraMatrix.set(newMatrix);
		} catch (SingularMatrixException exc) {
			Log.d("Camera",
					"SingularMatrixException on Matrix: "
							+ newMatrix.toString());
		}
	}
}
