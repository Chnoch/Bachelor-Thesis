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
	private float mTop, mBottom, mLeft, mRight;

	/**
	 * Construct a default viewing frustum. The frustum is given by a default
	 * 4x4 projection matrix.
	 */
	public Frustum() {
		mProjectionMatrix = new Matrix4f();
		// mProjectionMatrix = Util.getIdentityMatrix();
		// Aspect Ratio is 1 on init
		this.mAspectRatio = 1;
		this.mVertFOV = 60;
		this.mNearPlane = 1;
		this.mFarPlane = 50;
		mTop = 1;
		mBottom = -1;
		mLeft = -10;
		mRight = 10;

		this.updateFrustum();

		// projectionMatrix = Util.getIdentityMatrix();
	}

	/**
	 * Return the 4x4 projection matrix, which is used for example by the
	 * renderer.
	 * 
	 * @return the 4x4 projection matrix
	 */
	public Matrix4f getProjectionMatrix(boolean picking) {

		// VERSION NUMBER ONE: Wrong depth, right picking
		final float DEG2RAD = 3.14159265f / 180;

		Matrix4f numberOne = new Matrix4f();
		float halfFov = mVertFOV * 0.5f * DEG2RAD;
		// float deltaZ = mFarPlane - mNearPlane;
		float deltaZ = 0;
		if (picking) {
			deltaZ = mFarPlane - mNearPlane;
		} else {
			deltaZ = mNearPlane - mFarPlane;
		}
		float sine = (float) Math.sin(halfFov);
		float cotangent = (float) Math.cos(halfFov) / sine;

		// float temp = (float) (1 / (mAspectRatio * Math.tan(mVertFOV *DEG2RAD
		// / 2)));
		numberOne.setM00(cotangent);
		// numberOne.setM00(temp);
		// numberOne.setM02(0);

		// temp = (float) (1 / Math.tan(mVertFOV * DEG2RAD / 2));
		numberOne.setM11(cotangent * mAspectRatio);
		// numberOne.setM11(temp);

		// temp = (mNearPlane + mFarPlane) / (mNearPlane - mFarPlane);
		numberOne.setM22((mFarPlane + mNearPlane) / deltaZ);
		// numberOne.setM22(temp);

		// temp = (2 * mNearPlane * mFarPlane) / (mNearPlane - mFarPlane);
		numberOne.setM23(2 * mNearPlane * mFarPlane / deltaZ);
		// numberOne.setM23(temp);

		numberOne.setM32(-1);

		/*
		 * Matrix4f numberTwo = new Matrix4f();
		 * 
		 * // VERSION NUMBER TWO numberTwo.setM00(2 * mNearPlane / (mRight -
		 * mLeft)); numberTwo.setM02((mRight + mLeft) / (mRight - mLeft));
		 * numberTwo.setM11(2 * mNearPlane / (mTop - mBottom));
		 * numberTwo.setM12((mTop + mBottom) / (mTop - mBottom)); numberTwo
		 * .setM22(((mFarPlane + mNearPlane) / (mFarPlane - mNearPlane)));
		 * numberTwo .setM23(((2 * mFarPlane * mNearPlane) / (mFarPlane -
		 * mNearPlane))); numberTwo.setM32(-1);
		 */
		return numberOne;
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

	public void setLeft(float left) {
		mLeft = left;
		updateFrustum();
	}

	public void setRight(float right) {
		mRight = right;
		updateFrustum();
	}

	private void updateFrustum() {
		// This is the old version used to calculate the frustum

		// The new version, that should correspond with how opengl is handling
		// the projection matrix

		this.mProjectionMatrix.setM00(2 * mNearPlane / (mRight - mLeft));
		this.mProjectionMatrix.setM02((mRight + mLeft) / (mRight - mLeft));
		this.mProjectionMatrix.setM11(2 * mNearPlane / (mTop - mBottom));
		this.mProjectionMatrix.setM12((mTop + mBottom) / (mTop - mBottom));
		this.mProjectionMatrix
				.setM22(((mFarPlane + mNearPlane) / (mNearPlane - mFarPlane)));
		this.mProjectionMatrix
				.setM23(((2 * mFarPlane * mNearPlane) / (mNearPlane - mFarPlane)));
		this.mProjectionMatrix.setM32(-1);

	}

}
