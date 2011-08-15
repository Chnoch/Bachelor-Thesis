package ch.chnoch.thesis.renderer;

import javax.vecmath.*;

/**
 * Stores the properties of a light source.
 */
public class Light {

	private Vector3f mHalfPlane, mDirection, mPosition, mDiffuse, mSpecular, mAmbient, mSpotDirection;
	private float mSpotExponent;
	private float mSpotCutoff;
	private Type mType;
	
	private Camera mCamera;

	public Light(Camera camera) {
		mDirection = new Vector3f(0.f, 0.f, 1.f);
		mPosition = new Vector3f(0.f, 0.f, 1.f);
		mType = Type.DIRECTIONAL;
		mDiffuse = new Vector3f(1.f, 1.f, 1.f);
		mAmbient = new Vector3f(0.f, 0.f, 0.f);
		mSpecular = new Vector3f(1.f, 1.f, 1.f);
		mSpotDirection = new Vector3f(0.f, 0.f, 1.f);
		mSpotExponent = 0.f;
		mSpotCutoff = 180.f;
		mCamera = camera;
		
		mHalfPlane = camera.createHalfwayVector(this);
	}


	public float[] createDirectionArray(Matrix3f viewMatrix) {
		Vector3f tempDir = new Vector3f(mDirection);
		viewMatrix.transform(tempDir);
		float[] dir = new float[3];
		dir[0] = tempDir.x;
		dir[1] = tempDir.y;
		dir[2] = tempDir.z;
		return dir;
	}
	
	public float[] createDirectionArray() {
		float[] dir = new float[3];
		dir[0] = mDirection.x;
		dir[1] = mDirection.y;
		dir[2] = mDirection.z;
		return dir;
	}

	public float[] createPositionArray() {
		float[] pos = new float[4];
		pos[0] = mPosition.x;
		pos[1] = mPosition.y;
		pos[2] = mPosition.z;
		pos[3] = 1.f;
		return pos;
	}

	public float[] createHalfplaneArray(Matrix3f viewMatrix) {
		Vector3f tempDir = new Vector3f(mHalfPlane);
		viewMatrix.transform(tempDir);
		float[] halfVec = new float[3];
		halfVec[0] = tempDir.x;
		halfVec[1] = tempDir.y;
		halfVec[2] = tempDir.z;
		return halfVec;
	}
	
	public float[] createSpotDirectionArray() {
		float[] spotDir = new float[3];
		spotDir[0] = mSpotDirection.x;
		spotDir[1] = mSpotDirection.y;
		spotDir[2] = mSpotDirection.z;
		return spotDir;
	}

	public float[] createDiffuseArray() {
		float[] diff = new float[4];
		diff[0] = mDiffuse.x;
		diff[1] = mDiffuse.y;
		diff[2] = mDiffuse.z;
		diff[3] = 1.f;
		return diff;
	}

	public float[] createAmbientArray() {
		float[] amb = new float[4];
		amb[0] = mAmbient.x;
		amb[1] = mAmbient.y;
		amb[2] = mAmbient.z;
		amb[3] = 0;
		return amb;
	}

	public float[] createSpecularArray() {
		float[] spec = new float[4];
		spec[0] = mSpecular.x;
		spec[1] = mSpecular.y;
		spec[2] = mSpecular.z;
		spec[3] = 0;
		return spec;
	}

	/**
	 * Enumeration to declare the type of the light.
	 * @author Chnoch
	 *
	 */
	public enum Type {
		DIRECTIONAL, POINT, SPOT
	}
	
	public Vector3f getHalfPlane() {
		return mHalfPlane;
	}
	
	
	public void setHalfPlane(Vector3f mHalfPlane) {
		this.mHalfPlane = mHalfPlane;
	}
	
	
	public Vector3f getDirection() {
		return mDirection;
	}
	
	
	public void setDirection(Vector3f mDirection) {
		this.mDirection = mDirection;
		mHalfPlane = mCamera.createHalfwayVector(this);
	}
	
	
	public Vector3f getPosition() {
		return mPosition;
	}
	
	
	public void setPosition(Vector3f mPosition) {
		this.mPosition = mPosition;
		mHalfPlane = mCamera.createHalfwayVector(this);
	}
	
	
	public Vector3f getDiffuse() {
		return mDiffuse;
	}
	
	
	public void setDiffuse(Vector3f mDiffuse) {
		this.mDiffuse = mDiffuse;
	}
	
	
	public Vector3f getSpecular() {
		return mSpecular;
	}
	
	
	public void setSpecular(Vector3f mSpecular) {
		this.mSpecular = mSpecular;
	}
	
	
	public Vector3f getAmbient() {
		return mAmbient;
	}
	
	
	public void setAmbient(Vector3f mAmbient) {
		this.mAmbient = mAmbient;
	}
	
	
	public Vector3f getSpotDirection() {
		return mSpotDirection;
	}
	
	
	public void setSpotDirection(Vector3f mSpotDirection) {
		this.mSpotDirection = mSpotDirection;
	}
	
	
	public float getSpotExponent() {
		return mSpotExponent;
	}
	
	
	public void setSpotExponent(float mSpotExponent) {
		this.mSpotExponent = mSpotExponent;
	}
	
	
	public float getSpotCutoff() {
		return mSpotCutoff;
	}
	
	
	public void setSpotCutoff(float mSpotCutoff) {
		this.mSpotCutoff = mSpotCutoff;
	}
	
	
	public Type getType() {
		return mType;
	}
	
	
	public void setType(Type mType) {
		this.mType = mType;
	}
}
