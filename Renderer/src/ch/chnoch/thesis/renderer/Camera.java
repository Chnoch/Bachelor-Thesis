package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3f;

import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

/**
 * Stores the specification of a virtual camera. You will extend this class to
 * construct a 4x4 camera matrix, i.e., the world-to- camera transform from
 * intuitive parameters.
 * 
 * A scene manager (see {@link SceneManagerInterface},
 * {@link SimpleSceneManager}) stores a camera.
 */
public class Camera {

	private Matrix4f mCameraMatrix;
	private Vector3f mCenterOfProjection, mLookAtPoint, mUpVector;

	/**
	 * Construct a camera with a default camera matrix. The camera matrix
	 * corresponds to the world-to-camera transform. This default matrix places
	 * the camera at (0,0,10) in world space, facing towards the origin (0,0,0)
	 * of world space, i.e., towards the negative z-axis.
	 */
	public Camera() {
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

	public Vector3f getCenterOfProjection() {
		return mCenterOfProjection;
	}

	public void setCenterOfProjection(Vector3f centerOfProjection) {
		this.mCenterOfProjection = centerOfProjection;
		this.update();
	}

	public Vector3f getLookAtPoint() {
		return mLookAtPoint;
	}

	public void setLookAtPoint(Vector3f lookAtPoint) {
		this.mLookAtPoint = lookAtPoint;
		this.update();
	}

	public Vector3f getUpVector() {
		return mUpVector;
	}

	public void setUpVector(Vector3f upVector) {
		this.mUpVector = upVector;
		this.update();
	}

	public void update() {
		updateCamera();
	}

	public Vector3f createHalfwayVector(Light light) {
		Vector3f halfway;
		Vector3f eyeVec = new Vector3f(mCenterOfProjection);
		// eyeVec.sub(mLookAtPoint);
		Vector3f lightSource = light.getDirection();
		// lightSource.negate();
		// lightSource.sub(light.getDirection());
		halfway = new Vector3f(eyeVec);
		halfway.sub(lightSource);
		// halfway.normalize();
		return halfway;
	}

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

	private void updateCamera2() {
		float forwardx, forwardy, forwardz, invMag;
		float upx, upy, upz;
		float sidex, sidey, sidez;

		forwardx = mCenterOfProjection.x - mLookAtPoint.x;
		forwardy = mCenterOfProjection.y - mLookAtPoint.y;
		forwardz = mCenterOfProjection.z - mLookAtPoint.z;

		invMag = (float) (1.0 / Math.sqrt(forwardx * forwardx + forwardy
				* forwardy + forwardz * forwardz));
		forwardx = forwardx * invMag;
		forwardy = forwardy * invMag;
		forwardz = forwardz * invMag;

		invMag = (float) (1.0 / Math.sqrt(mUpVector.x * mUpVector.x
				+ mUpVector.y * mUpVector.y + mUpVector.z * mUpVector.z));
		upx = mUpVector.x * invMag;
		upy = mUpVector.y * invMag;
		upz = mUpVector.z * invMag;

		// side = up cross forward
		sidex = upy * forwardz - forwardy * upz;
		sidey = upz * forwardx - upx * forwardz;
		sidez = upx * forwardy - upy * forwardx;

		invMag = (float) (1.0 / Math.sqrt(sidex * sidex + sidey * sidey + sidez
				* sidez));
		sidex *= invMag;
		sidey *= invMag;
		sidez *= invMag;

		// recompute up = forward cross side

		upx = forwardy * sidez - sidey * forwardz;
		upy = forwardz * sidex - forwardx * sidez;
		upz = forwardx * sidey - forwardy * sidex;

		float[] mat = new float[16];
		mat[0] = sidex;
		mat[1] = sidey;
		mat[2] = sidez;

		mat[4] = upx;
		mat[5] = upy;
		mat[6] = upz;

		mat[8] = forwardx;
		mat[9] = forwardy;
		mat[10] = forwardz;

		mat[3] = -mCenterOfProjection.x * mat[0] + -mCenterOfProjection.y
				* mat[1] + -mCenterOfProjection.z * mat[2];
		mat[7] = -mCenterOfProjection.x * mat[4] + -mCenterOfProjection.y
				* mat[5] + -mCenterOfProjection.z * mat[6];
		mat[11] = -mCenterOfProjection.x * mat[8] + -mCenterOfProjection.y
				* mat[9] + -mCenterOfProjection.z * mat[10];

		mat[12] = mat[13] = mat[14] = 0;
		mat[15] = 1;

		mCameraMatrix.set(mat);
	}
}
