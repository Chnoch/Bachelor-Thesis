package ch.chnoch.thesis.renderer;

/**
 * Declares the functionality to manage shaders.
 */
public interface Shader {

	public int load(String vertexFileName, String fragmentFileName) throws Exception;
	public void use();
	public void disable();
}
