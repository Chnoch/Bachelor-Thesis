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
		// Aspect Ratio is 1 on init
		this.mAspectRatio = 1;
		this.mVertFOV = 30;
		this.mNearPlane = 2;
		this.mFarPlane = 30;
		mTop = 1;
		mBottom = -1;
		mLeft = -100;
		mRight = 100;
		
		this.updateFrustum();
		
//		projectionMatrix = Util.getIdentityMatrix();
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
		/*
		final float DEG2RAD = 3.14159265f / 180;

		float halfFov = mVertFOV * DEG2RAD;
		float deltaZ = mFarPlane - mNearPlane;
		float sine = (float)Math.sin(halfFov);
		float cotangent = (float) Math.cos(halfFov) / sine;
		
//		float temp = (float) (1 / (aspectRatio * Math.tan(vertFOV * DEG2RAD / 2)));
		this.mProjectionMatrix.setM00(cotangent);

//		temp = (float) (1 / Math.tan(vertFOV * DEG2RAD / 2));
		this.mProjectionMatrix.setM11(cotangent * mAspectRatio);

//		temp = (nearPlane + farPlane) / (nearPlane - farPlane);
		this.mProjectionMatrix.setM22(-(mFarPlane + mNearPlane)/deltaZ);

//		temp = (2 * nearPlane * farPlane) / (nearPlane - farPlane);
		this.mProjectionMatrix.setM32(-2 * mNearPlane * mFarPlane / deltaZ);
		
		this.mProjectionMatrix.setM23(-1);
		*/
		
		// The new version, that should correspond with how opengl is handling
		// the projection matrix
		
		this.mProjectionMatrix.setM00(2*mNearPlane/(mRight- mLeft));
		this.mProjectionMatrix.setM02((mRight+mLeft)/(mRight- mLeft));
		this.mProjectionMatrix.setM11(2*mNearPlane/(mTop - mBottom));
		this.mProjectionMatrix.setM12((mTop+mBottom)/(mTop-mBottom));
		this.mProjectionMatrix.setM22(- ((mFarPlane+mNearPlane) / (mFarPlane - mNearPlane)));
		this.mProjectionMatrix.setM23(- ((2*mFarPlane*mNearPlane )/(mFarPlane-mNearPlane)));
		this.mProjectionMatrix.setM32(-1);
		
	}

}
