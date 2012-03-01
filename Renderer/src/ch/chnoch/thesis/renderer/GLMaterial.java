package ch.chnoch.thesis.renderer;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform4f;
import ch.chnoch.thesis.renderer.util.GLUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class GLMaterial.
 */
public class GLMaterial extends Material {

	/** The Constant TAG. */
	private static final String TAG = "GLMaterial";
	
	/** The mu specular exponent handle. */
	private int muAmbientColorHandle, muDiffuseColorHandle, muSpecularColorHandle, muSpecularExponentHandle;
	
	/**
	 * Instantiates a new gL material.
	 */
	public GLMaterial() {
	}
	
	/**
	 * Gets the handles.
	 * 
	 * @param program
	 *            the program
	 * @return the handles
	 */
	public void getHandles(int program) {
		muAmbientColorHandle = glGetUniformLocation(program, "material.ambient");
		muDiffuseColorHandle= glGetUniformLocation(program, "material.diffuse");
		muSpecularColorHandle = glGetUniformLocation(program, "material.specular");
		muSpecularExponentHandle = glGetUniformLocation(program, "material.specular_exponent");
	}
	
	/**
	 * Draw.
	 * 
	 * @throws Exception
	 *             the exception
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
