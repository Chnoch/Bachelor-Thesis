package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;

// TODO: Auto-generated Javadoc
/**
 * Stores the properties of a material.
 */
public abstract class Material {

	/** The m diffuse. */
	public Vector3f mDiffuse;

	/** The m specular. */
	public Vector3f mSpecular;

	/** The m ambient. */
	public Vector3f mAmbient;

	/** The shininess. */
	public float shininess;

	/** The texture. */
	private Texture texture;

	/** The m texture changed. */
	private boolean mTextureChanged;

	/** The shader. */
	private Shader shader;

	/**
	 * Instantiates a new material.
	 */
	public Material() {
		mDiffuse = new Vector3f(1.f, 1.f, 1.f);
		mSpecular = new Vector3f(1.f, 1.f, 1.f);
		mAmbient = new Vector3f(1.f, 1.f, 1.f);
		shininess = 1.f;
	}

	/**
	 * Sets the texture.
	 * 
	 * @param tex
	 *            the new texture
	 */
	public void setTexture(Texture tex) {
		texture = tex;
		setTextureChanged(true);
	}

	/**
	 * Gets the texture.
	 * 
	 * @return the texture
	 */
	public Texture getTexture() {
		return texture;
	}
	
	/**
	 * Checks for texture changed.
	 * 
	 * @return true, if successful
	 */
	public boolean hasTextureChanged() {
		return mTextureChanged;
	}
	
	/**
	 * Sets the texture changed.
	 * 
	 * @param value
	 *            the new texture changed
	 */
	public void setTextureChanged(boolean value) {
		mTextureChanged = value;
	}

	/**
	 * Sets the shader.
	 * 
	 * @param shader
	 *            the new shader
	 */
	public void setShader(Shader shader) {
		this.shader = shader;
	}

	/**
	 * Gets the shader.
	 * 
	 * @return the shader
	 */
	public Shader getShader() {
		return shader;
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
	 * Creates the specular array.
	 * 
	 * @return the float[]
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
	 * Creates the ambient array.
	 * 
	 * @return the float[]
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
