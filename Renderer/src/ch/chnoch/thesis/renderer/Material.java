package ch.chnoch.thesis.renderer;

import javax.vecmath.*;

import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.interfaces.Texture;

import ch.chnoch.thesis.renderer.interfaces.Shader;

import ch.chnoch.thesis.renderer.interfaces.Shader;

/**
 * Stores the properties of a material.
 */
public class Material {

	public Vector3f diffuse;
	public Vector3f specular;
	public Vector3f ambient;
	public float shininess;
	private Texture texture;
	private Shader shader;
	
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

	public Texture getTexture() {
		return texture;
	}
	
	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public Shader getShader() {
		return shader;
	}
}
