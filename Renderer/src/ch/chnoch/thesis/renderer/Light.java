package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

/**
 * This class stores the general properties of a light source. Several types of
 * different light sources can be specified. The properties stored in this class
 * will be used by either an OpenGL renderer to render the light or by a
 * software renderer that draws the lights.
 */
public class Light {

	private Vector3f mDirection, mPosition, mDiffuse, mSpecular,
			mAmbient, mSpotDirection;

	private float mSpotExponent;

	private float mSpotCutoff;

	/** The light type */
	private Type mType;

	/**
	 * Instantiates a new light with standard parameters. <br>
	 * It is a directional light with the direction (0,0,1), a diffuse light
	 * component with (0.7,0.7,0.7), an ambient light component with
	 * (0.3,0.3,0.3) and a specular light component with (1,1,1)
	 * 
	 */
	public Light() {
		mDirection = new Vector3f(0.f, 0.f, 1.f);
		mPosition = new Vector3f(0.f, 0.f, 1.f);
		mType = Type.DIRECTIONAL;
		mDiffuse = new Vector3f(0.7f, 0.7f, 0.7f);
		mAmbient = new Vector3f(0.3f, 0.3f, 0.3f);
		mSpecular = new Vector3f(1.f, 1.f, 1.f);
		mSpotDirection = new Vector3f(0.f, 0.f, 1.f);
		mSpotExponent = 0.f;
		mSpotCutoff = 180.f;
	}

	/**
	 * Creates an array for the direction.
	 * 
	 * @return the direction
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
	 * Creates an array for the position.
	 * 
	 * @return the position
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
	 * Creates an array for the spot direction.
	 * 
	 * @return the spot direction
	 */
	public float[] createSpotDirectionArray() {
		float[] spotDir = new float[3];
		spotDir[0] = mSpotDirection.x;
		spotDir[1] = mSpotDirection.y;
		spotDir[2] = mSpotDirection.z;
		return spotDir;
	}

	/**
	 * Creates an array the diffuse light component.
	 * 
	 * @return the diffuse component
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
	 * Creates an array for the ambient light component.
	 * 
	 * @return the ambient component
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
	 * Creates an array for the specular light component.
	 * 
	 * @return the specular component
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
	 * Enumeration of the different types of light sources.
	 * 
	 */
	public enum Type {

		/** A directional light source */
		DIRECTIONAL,
		/** A point light source */
		POINT,
		/** A spot light source */
		SPOT
	}

	public Vector3f getDirection() {
		return mDirection;
	}

	public void setDirection(Vector3f mDirection) {
		this.mDirection = mDirection;
	}
	
	public void setDirection(float x, float y, float z) {
		this.mDirection.set(x,y,z);
	}
	public Vector3f getPosition() {
		return mPosition;
	}

	public void setPosition(Vector3f mPosition) {
		this.mPosition = mPosition;
	}

	public void setPosition(float x, float y, float z) {
		this.mPosition.set(x,y,z);
	}
	
	public Vector3f getDiffuse() {
		return mDiffuse;
	}

	public void setDiffuse(Vector3f mDiffuse) {
		this.mDiffuse = mDiffuse;
	}

	public void setDiffuse(float x, float y, float z) {
		this.mDiffuse.set(x,y,z);
	}
	public Vector3f getSpecular() {
		return mSpecular;
	}

	public void setSpecular(Vector3f mSpecular) {
		this.mSpecular = mSpecular;
	}
	
	public void setSpecular(float x, float y, float z) {
		this.mSpecular.set(x,y,z);
	}

	public Vector3f getAmbient() {
		return mAmbient;
	}

	public void setAmbient(Vector3f mAmbient) {
		this.mAmbient = mAmbient;
	}

	public void setAmbient(float x, float y, float z) {
		this.mAmbient.set(x,y,z);
	}

	public Vector3f getSpotDirection() {
		return mSpotDirection;
	}

	public void setSpotDirection(Vector3f mSpotDirection) {
		this.mSpotDirection = mSpotDirection;
	}

	public void setSpotDirection(float x, float y, float z) {
		this.mSpotDirection.set(x,y,z);
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
