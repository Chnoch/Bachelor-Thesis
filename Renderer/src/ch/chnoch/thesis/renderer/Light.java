package ch.chnoch.thesis.renderer;

import javax.vecmath.*;

/**
 * Stores the properties of a light source.
 */
public class Light {

	public Vector3f direction, position, diffuse, specular, ambient, spotDirection;
	public float spotExponent;
	public float spotCutoff;
	public Type type;

	public Light() {
		direction = new Vector3f(0.f, 0.f, 1.f);
		position = new Vector3f(0.f, 0.f, 1.f);
		type = Type.DIRECTIONAL;
		diffuse = new Vector3f(1.f, 1.f, 1.f);
		ambient = new Vector3f(0.f, 0.f, 0.f);
		specular = new Vector3f(1.f, 1.f, 1.f);
		spotDirection = new Vector3f(0.f, 0.f, 1.f);
		spotExponent = 0.f;
		spotCutoff = 180.f;
	}


	public float[] createDirectionArray() {
		float[] dir = new float[4];
		dir[0] = direction.x;
		dir[1] = direction.y;
		dir[2] = direction.z;
		dir[3] = 0.f;
		return dir;
	}

	public float[] createPositionArray() {
		float[] pos = new float[4];
		pos[0] = position.x;
		pos[1] = position.y;
		pos[2] = position.z;
		pos[3] = 1.f;
		return pos;
	}

	public float[] createSpotDirectionArray() {

		float[] spotDir = new float[3];
		spotDir[0] = spotDirection.x;
		spotDir[1] = spotDirection.y;
		spotDir[2] = spotDirection.z;
		return spotDir;
	}

	public float[] createDiffuseArray() {
		float[] diff = new float[4];
		diff[0] = diffuse.x;
		diff[1] = diffuse.y;
		diff[2] = diffuse.z;
		diff[3] = 1.f;
		return diff;
	}

	public float[] createAmbientArray() {
		float[] amb = new float[4];
		amb[0] = ambient.x;
		amb[1] = ambient.y;
		amb[2] = ambient.z;
		amb[3] = 0;
		return amb;
	}

	public float[] createSpecularArray() {
		float[] spec = new float[4];
		spec[0] = specular.x;
		spec[1] = specular.y;
		spec[2] = specular.z;
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
