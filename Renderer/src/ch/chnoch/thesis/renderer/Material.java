package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;

/**
 * This class stores the general properties of a material. The properties stored
 * in this class will be used by an OpenGL renderer or a software renderer to
 * create the material.
 */
public abstract class Material {

	public Vector3f mDiffuse;
	public Vector3f mSpecular;
	public Vector3f mAmbient;

	public float shininess;

	private Texture texture;

	private boolean mTextureChanged;

	private Shader shader;

	/**
	 * Instantiates a new material with standard properties. <br>
	 * The diffuse, specular and ambient component are all set to (1,1,1), and
	 * the shininess parameter is set to 40.
	 */
	public Material() {
		mDiffuse = new Vector3f(1.f, 1.f, 1.f);
		mSpecular = new Vector3f(1.f, 1.f, 1.f);
		mAmbient = new Vector3f(1.f, 1.f, 1.f);
		shininess = 40f;
	}

	public void setTexture(Texture tex) {
		texture = tex;
		setTextureChanged(true);
	}

	public Texture getTexture() {
		return texture;
	}
	
	public boolean hasTextureChanged() {
		return mTextureChanged;
	}
	
	public void setTextureChanged(boolean value) {
		mTextureChanged = value;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public Shader getShader() {
		return shader;
	}

	/**
	 * Creates an array for the diffuse material component.
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
	 * Creates an array for the specular material component.
	 * 
	 * @return the specular component
	 */
	public float[] createSpecularArray() {
		float[] spec = new float[4];
		spec[0] = mSpecular.x;
		spec[1] = mSpecular.y;
		spec[2] = mSpecular.z;
		spec[3] = 1.f;
		return spec;
	}

	/**
	 * Creates an array for the ambient material component.
	 * 
	 * @return the ambient component
	 */
	public float[] createAmbientArray() {
		float[] amb = new float[4];
		amb[0] = mAmbient.x;
		amb[1] = mAmbient.y;
		amb[2] = mAmbient.z;
		amb[3] = 1.f;
		return amb;
	}
}
