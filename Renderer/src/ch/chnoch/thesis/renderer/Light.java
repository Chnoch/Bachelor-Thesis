package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

// TODO: Auto-generated Javadoc
/**
 * Stores the properties of a light source.
 */
public class Light {

	/** The m spot direction. */
	private Vector3f mDirection, mPosition, mDiffuse, mSpecular,
			mAmbient, mSpotDirection;

	/** The m spot exponent. */
	private float mSpotExponent;

	/** The m spot cutoff. */
	private float mSpotCutoff;

	/** The m type. */
	private Type mType;

	/** The m camera. */
	private Camera mCamera;

	/**
	 * Instantiates a new light.
	 * 
	 * @param camera
	 *            the camera
	 */
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
//
//		mHalfPlane = camera.createHalfwayVector(this);
	}

	/**
	 * Creates the direction array.
	 * 
	 * @return the float[]
	 */
	public float[] createDirectionArray() {
		float[] dir = new float[4];
		dir[0] = mDirection.x;
		dir[1] = mDirection.y;
		dir[2] = mDirection.z;
		dir[3] = 0;
		return dir;
	}

	/**
	 * Creates the position array.
	 * 
	 * @return the float[]
	 */
	public float[] createPositionArray() {
		float[] pos = new float[4];
		pos[0] = mPosition.x;
		pos[1] = mPosition.y;
		pos[2] = mPosition.z;
		pos[3] = 1.f;
		return pos;
	}

	/**
	 * Creates the spot direction array.
	 * 
	 * @return the float[]
	 */
	public float[] createSpotDirectionArray() {
		float[] spotDir = new float[3];
		spotDir[0] = mSpotDirection.x;
		spotDir[1] = mSpotDirection.y;
		spotDir[2] = mSpotDirection.z;
		return spotDir;
	}

	/**
	 * Creates the diffuse array.
	 * 
	 * @return the float[]
	 */
	public float[] createDiffuseArray() {
		float[] diff = new float[4];
		diff[0] = mDiffuse.x;
		diff[1] = mDiffuse.y;
		diff[2] = mDiffuse.z;
		diff[3] = 1.f;
		return diff;
	}

	/**
	 * Creates the ambient array.
	 * 
	 * @return the float[]
	 */
	public float[] createAmbientArray() {
		float[] amb = new float[4];
		amb[0] = mAmbient.x;
		amb[1] = mAmbient.y;
		amb[2] = mAmbient.z;
		amb[3] = 1;
		return amb;
	}

	/**
	 * Creates the specular array.
	 * 
	 * @return the float[]
	 */
	public float[] createSpecularArray() {
		float[] spec = new float[4];
		spec[0] = mSpecular.x;
		spec[1] = mSpecular.y;
		spec[2] = mSpecular.z;
		spec[3] = 1;
		return spec;
	}

	/**
	 * Enumeration to declare the type of the light.
	 * 
	 * @author Chnoch
	 * 
	 */
	public enum Type {

		/** The DIRECTIONAL. */
		DIRECTIONAL,
		/** The POINT. */
		POINT,
		/** The SPOT. */
		SPOT
	}

	/**
	 * Gets the direction.
	 * 
	 * @return the direction
	 */
	public Vector3f getDirection() {
		return mDirection;
	}

	/**
	 * Sets the direction.
	 * 
	 * @param mDirection
	 *            the new direction
	 */
	public void setDirection(Vector3f mDirection) {
		this.mDirection = mDirection;
	}
	
	/**
	 * Sets the direction.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void setDirection(float x, float y, float z) {
		this.mDirection.set(x,y,z);
	}

	/**
	 * Gets the position.
	 * 
	 * @return the position
	 */
	public Vector3f getPosition() {
		return mPosition;
	}

	/**
	 * Sets the position.
	 * 
	 * @param mPosition
	 *            the new position
	 */
	public void setPosition(Vector3f mPosition) {
		this.mPosition = mPosition;
	}

	/**
	 * Sets the position.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void setPosition(float x, float y, float z) {
		this.mPosition.set(x,y,z);
	}
	
	/**
	 * Gets the diffuse.
	 * 
	 * @return the diffuse
	 */
	public Vector3f getDiffuse() {
		return mDiffuse;
	}

	/**
	 * Sets the diffuse.
	 * 
	 * @param mDiffuse
	 *            the new diffuse
	 */
	public void setDiffuse(Vector3f mDiffuse) {
		this.mDiffuse = mDiffuse;
	}

	/**
	 * Sets the diffuse.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void setDiffuse(float x, float y, float z) {
		this.mDiffuse.set(x,y,z);
	}

	/**
	 * Gets the specular.
	 * 
	 * @return the specular
	 */
	public Vector3f getSpecular() {
		return mSpecular;
	}

	/**
	 * Sets the specular.
	 * 
	 * @param mSpecular
	 *            the new specular
	 */
	public void setSpecular(Vector3f mSpecular) {
		this.mSpecular = mSpecular;
	}
	
	/**
	 * Sets the specular.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void setSpecular(float x, float y, float z) {
		this.mSpecular.set(x,y,z);
	}

	/**
	 * Gets the ambient.
	 * 
	 * @return the ambient
	 */
	public Vector3f getAmbient() {
		return mAmbient;
	}

	/**
	 * Sets the ambient.
	 * 
	 * @param mAmbient
	 *            the new ambient
	 */
	public void setAmbient(Vector3f mAmbient) {
		this.mAmbient = mAmbient;
	}

	/**
	 * Sets the ambient.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void setAmbient(float x, float y, float z) {
		this.mAmbient.set(x,y,z);
	}

	/**
	 * Gets the spot direction.
	 * 
	 * @return the spot direction
	 */
	public Vector3f getSpotDirection() {
		return mSpotDirection;
	}

	/**
	 * Sets the spot direction.
	 * 
	 * @param mSpotDirection
	 *            the new spot direction
	 */
	public void setSpotDirection(Vector3f mSpotDirection) {
		this.mSpotDirection = mSpotDirection;
	}

	/**
	 * Sets the spot direction.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void setSpotDirection(float x, float y, float z) {
		this.mSpotDirection.set(x,y,z);
	}

	/**
	 * Gets the spot exponent.
	 * 
	 * @return the spot exponent
	 */
	public float getSpotExponent() {
		return mSpotExponent;
	}

	/**
	 * Sets the spot exponent.
	 * 
	 * @param mSpotExponent
	 *            the new spot exponent
	 */
	public void setSpotExponent(float mSpotExponent) {
		this.mSpotExponent = mSpotExponent;
	}

	/**
	 * Gets the spot cutoff.
	 * 
	 * @return the spot cutoff
	 */
	public float getSpotCutoff() {
		return mSpotCutoff;
	}

	/**
	 * Sets the spot cutoff.
	 * 
	 * @param mSpotCutoff
	 *            the new spot cutoff
	 */
	public void setSpotCutoff(float mSpotCutoff) {
		this.mSpotCutoff = mSpotCutoff;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public Type getType() {
		return mType;
	}

	/**
	 * Sets the type.
	 * 
	 * @param mType
	 *            the new type
	 */
	public void setType(Type mType) {
		this.mType = mType;
	}
}
