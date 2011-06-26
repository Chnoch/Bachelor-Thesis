package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.util.Util;

/**
 * Stores the specification of a viewing frustum, or a viewing volume. The
 * viewing frustum is represented by a 4x4 projection matrix. You will extend
 * this class to construct the projection matrix from intuitive parameters.
 * <p>
 * A scene manager (see {@link SceneManagerInterface},
 * {@link SimpleSceneManager}) stores a frustum.
 */
public class Frustum {

	private Matrix4f mProjectionMatrix;
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
		this.mNearPlane = 1;
		this.mFarPlane = 50;

		this.updateFrustum();
	}

	/**
	 * Return the 4x4 projection matrix, which is used for example by the
	 * renderer.
	 * 
	 * @return the 4x4 projection matrix
	 */
	public Matrix4f getProjectionMatrix(boolean picking) {
		return mProjectionMatrix;
	}

	public float getNearPlane() {
		return mNearPlane;
	}

	public void setNearPlane(float nearPlane) {
		this.mNearPlane = nearPlane;
		this.updateFrustum();
	}

	public float getFarPlane() {
		return mFarPlane;
	}

	public void setFarPlane(float farPlane) {
		this.mFarPlane = farPlane;
		this.updateFrustum();
	}

	public float getAspectRatio() {
		return mAspectRatio;
	}

	public void setAspectRatio(float aspectRatio) {
		this.mAspectRatio = aspectRatio;
		updateFrustum();
	}

	public float getVertFOV() {
		return mVertFOV;
	}

	public void setVertFOV(float vertFOV) {
		this.mVertFOV = vertFOV;
		updateFrustum();
	}

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
