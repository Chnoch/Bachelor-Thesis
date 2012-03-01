package ch.chnoch.thesis.renderer;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform4f;
import ch.chnoch.thesis.renderer.util.GLUtil;

/**
 * Represents a material that will be drawn with OpenGL ES 2.0. This class will
 * find the correct handles in the shader and put the appropriate attributes of
 * the material that is specified stored with a node into OpenGL. These values
 * will be used by the shader to draw the material.
 */
public class GLMaterial extends Material {

	/** The Constant TAG. */
	private static final String TAG = "GLMaterial";
	private int muAmbientColorHandle, muDiffuseColorHandle, muSpecularColorHandle, muSpecularExponentHandle;
	
	/**
	 * Instantiates a new OpenGL material.
	 */
	public GLMaterial() {
	}
	
	/**
	 * Gets the handles in the shader.
	 * 
	 * @param program
	 *            the program that represents the current shader
	 */
	public void getHandles(int program) {
		muAmbientColorHandle = glGetUniformLocation(program, "material.ambient");
		muDiffuseColorHandle= glGetUniformLocation(program, "material.diffuse");
		muSpecularColorHandle = glGetUniformLocation(program, "material.specular");
		muSpecularExponentHandle = glGetUniformLocation(program, "material.specular_exponent");
	}
	
	/**
	 * Draws the material to OpenGL.
	 * 
	 * @throws Exception
	 *             An exception that can occur with OpenGl
	 */
	public void draw() throws Exception {
		float[] amb = new float[4];
		amb= createAmbientArray();
		glUniform4f(muAmbientColorHandle, amb[0], amb[1], amb[2], amb[3]);
		GLUtil.checkGlError("glUniform4f muAmbientColorHandle",TAG);
		
		float[] diffCol = new float[4];
		diffCol = createDiffuseArray();
		glUniform4f(muDiffuseColorHandle, diffCol[0], diffCol[1], diffCol[2], diffCol[3]);
		GLUtil.checkGlError("glUniform4f muDiffuseColorHandle",TAG);
		
		float[] specCol = new float[4];
		specCol = createSpecularArray();
		glUniform4f(muSpecularColorHandle, specCol[0],specCol[1], specCol[2], specCol[3] );
		GLUtil.checkGlError("glUniform4f muSpecularColorHandle",TAG);
		
		glUniform1f(muSpecularExponentHandle, shininess);
		GLUtil.checkGlError("glUniform3f muDirectionHandle",TAG);
	}
}
