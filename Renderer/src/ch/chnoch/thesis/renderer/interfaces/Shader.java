package ch.chnoch.thesis.renderer.interfaces;

/**
 * Declares the functionality to manage shaders in Open GL ES 2.0.
 */
public interface Shader {

	/**
	 * Loads a shader into OpenGL. uses the vertex and fragment shader source
	 * code to pass to OpenGL where the actual shader is created. An OpenGL
	 * shader is represented by an Integer id.
	 * 
	 * @param vertexFileName
	 *            the vertex shader file name
	 * @param fragmentFileName
	 *            the fragment shader file name
	 * @return the id of the shader in OpenGL
	 * @throws Exception
	 *             the exception
	 */
	public int load(String vertexFileName, String fragmentFileName) throws Exception;

	/**
	 * Gets the OpenGL representation of a shader.
	 * 
	 * @return the id of the shader
	 */
	public int getProgram();

	/**
	 * Uses the shader in OpenGL that is associated with this object.
	 */
	public void use();

	/**
	 * Disables the shader in OpenGL that is associated with this object.
	 */
	public void disable();
}
