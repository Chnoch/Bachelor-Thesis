package ch.chnoch.thesis.renderer;

import javax.vecmath.*;

import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;

/**
 * Stores the properties of a material.
 */
public class Material {

	public Vector3f mDiffuse;
	public Vector3f mSpecular;
	public Vector3f mAmbient;
	public float shininess;
	private Texture texture;
	private Shader shader;

	public Material() {
		mDiffuse = new Vector3f(1.f, 1.f, 1.f);
		mSpecular = new Vector3f(1.f, 1.f, 1.f);
		mAmbient = new Vector3f(1.f, 1.f, 1.f);
		shininess = 1.f;
	}

	public void setTexture(Texture tex) {
		texture = tex;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public Shader getShader() {
		return shader;
	}

	public float[] createDiffuseArray() {
		float[] diff = new float[4];
		diff[0] = mDiffuse.x;
		diff[1] = mDiffuse.y;
		diff[2] = mDiffuse.z;
		diff[3] = 1.f;
		return diff;
	}

	public float[] createSpecularArray() {
		float[] spec = new float[4];
		spec[0] = mSpecular.x;
		spec[1] = mSpecular.y;
		spec[2] = mSpecular.z;
		spec[3] = 1.f;
		return spec;
	}

	public float[] createAmbientArray() {
		float[] amb = new float[4];
		amb[0] = mAmbient.x;
		amb[1] = mAmbient.y;
		amb[2] = mAmbient.z;
		amb[3] = 1.f;
		return amb;
	}
}
