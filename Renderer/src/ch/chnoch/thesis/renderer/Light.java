package ch.chnoch.thesis.renderer;

import javax.vecmath.*;

/**
 * Stores the properties of a light source.
 */
public class Light {

	public Vector3f mHalfPlane, mDirection, mPosition, mDiffuse, mSpecular, mAmbient, mSpotDirection;
	public float mSpotExponent;
	public float mSpotCutoff;
	public Type mType;

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
		
		mHalfPlane = camera.createHalfwayVector(this);
	}


	public float[] createDirectionArray() {
		float[] dir = new float[4];
		dir[0] = mDirection.x;
		dir[1] = mDirection.y;
		dir[2] = mDirection.z;
		dir[3] = 0.f;
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

	public float[] createHalfplaneArray() {
		
		float[] halfplane = new float[3];
		halfplane[0] = mHalfPlane.x;
		halfplane[1] = mHalfPlane.y;
		halfplane[2] = mHalfPlane.z;
		return halfplane;
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
}
