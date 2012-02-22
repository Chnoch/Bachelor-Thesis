package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

/**
 * Stores the specification of a viewing frustum, or a viewing volume. The
 * viewing frustum is represented by a 4x4 projection matrix. You will extend
 * this class to construct the projection matrix from intuitive parameters.
 * <p>
 * A scene manager (see {@link SceneManagerInterface},
 * {@link SimpleSceneManager}) stores a frustum.
 */
public class Frustum {

	/** The projection matrix. */
	private Matrix4f mProjectionMatrix;

	/** The different parameters of the frustum. */
	private float mNearPlane, mFarPlane, mAspectRatio, mVertFOV;

	/**
	 * Construct a default viewing frustum. The frustum is given by a default
	 * 4x4 projection matrix.
	 */
	public Frustum() {
		mProjectionMatrix = new Matrix4f();
		// Aspect Ratio is 1 on init
		this.mAspectRatio = 1;
		this.mVertFOV = 60;
		this.mNearPlane = 2;
		this.mFarPlane = 100;

		this.updateFrustum();
	}

	/**
	 * Return the 4x4 projection matrix, which is used for example by the
	 * renderer.
	 * 
	 * @return the 4x4 projection matrix
	 */
	public Matrix4f getProjectionMatrix() {
		return mProjectionMatrix;
	}

	/**
	 * Gets the near plane.
	 * 
	 * @return the near plane
	 */
	public float getNearPlane() {
		return mNearPlane;
	}

	/**
	 * Sets the near plane.
	 * 
	 * @param nearPlane
	 *            the new near plane
	 */
	public void setNearPlane(float nearPlane) {
		this.mNearPlane = nearPlane;
		this.updateFrustum();
	}

	/**
	 * Gets the far plane.
	 * 
	 * @return the far plane
	 */
	public float getFarPlane() {
		return mFarPlane;
	}

	/**
	 * Sets the far plane.
	 * 
	 * @param farPlane
	 *            the new far plane
	 */
	public void setFarPlane(float farPlane) {
		this.mFarPlane = farPlane;
		this.updateFrustum();
	}

	/**
	 * Gets the aspect ratio.
	 * 
	 * @return the aspect ratio
	 */
	public float getAspectRatio() {
		return mAspectRatio;
	}

	/**
	 * Sets the aspect ratio.
	 * 
	 * @param aspectRatio
	 *            the new aspect ratio
	 */
	public void setAspectRatio(float aspectRatio) {
		this.mAspectRatio = aspectRatio;
		updateFrustum();
	}

	/**
	 * Gets the vertical Field of View.
	 * 
	 * @return the vertical Field of View
	 */
	public float getVertFOV() {
		return mVertFOV;
	}

	/**
	 * vertical Field of View.
	 * 
	 * @param vertFOV
	 *            the new vertical Field of View
	 */
	public void setVertFOV(float vertFOV) {
		this.mVertFOV = vertFOV;
		updateFrustum();
	}

	/**
	 * Updates the frustum.
	 */
	private void updateFrustum() {
		final float DEG2RAD = 3.14159265f / 180;

		float halfFov = mVertFOV * 0.5f * DEG2RAD;
		// float deltaZ = mFarPlane - mNearPlane;
		float deltaZ = 0;
		deltaZ = mNearPlane - mFarPlane;
		float sine = (float) Math.sin(halfFov);
		float cotangent = (float) Math.cos(halfFov) / sine;

		mProjectionMatrix.setM00(cotangent);
		mProjectionMatrix.setM11(cotangent * mAspectRatio);
		mProjectionMatrix.setM22((mFarPlane + mNearPlane) / deltaZ);
		mProjectionMatrix.setM23(2 * mNearPlane * mFarPlane / deltaZ);
		mProjectionMatrix.setM32(-1);
	}
}
