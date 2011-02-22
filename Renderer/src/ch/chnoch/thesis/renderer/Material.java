package ch.chnoch.thesis.renderer;

import javax.vecmath.*;

/**
 * Stores the properties of a material.
 */
public class Material {

	public Vector3f diffuse;
	public Vector3f specular;
	public Vector3f ambient;
	public float shininess;
	public Texture texture;
	public Shader shader;
	
	public Material()
	{
		diffuse = new Vector3f(1.f, 1.f, 1.f);
		specular = new Vector3f(1.f, 1.f, 1.f);
		ambient = new Vector3f(1.f, 1.f, 1.f);
		shininess = 1.f;
	}
	
	public void setTexture(Texture tex) {
		texture = tex;
	}
}
