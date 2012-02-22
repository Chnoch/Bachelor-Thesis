package ch.chnoch.thesis.renderer.interfaces;

// TODO: Auto-generated Javadoc
/**
 * Declares the functionality to manage shaders.
 */
public interface Shader {

	/**
	 * Load.
	 *
	 * @param vertexFileName the vertex file name
	 * @param fragmentFileName the fragment file name
	 * @return the int
	 * @throws Exception the exception
	 */
	public int load(String vertexFileName, String fragmentFileName) throws Exception;
	
	/**
	 * Gets the program.
	 *
	 * @return the program
	 */
	public int getProgram();
	
	/**
	 * Use.
	 */
	public void use();
	
	/**
	 * Disable.
	 */
	public void disable();
}
