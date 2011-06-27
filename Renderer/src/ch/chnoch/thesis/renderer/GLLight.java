package ch.chnoch.thesis.renderer;

import static android.opengl.GLES20.*;
import ch.chnoch.thesis.renderer.util.GLUtil;

public class GLLight {
	private Light mLight;
	private static final String TAG = "GLLight";

	private int muDirectionHandle, muHalfplaneHandle, muAmbientColorHandle,
			muDiffuseColorHandle, muSpecularColorHandle;

	public GLLight(Light light) {
		mLight = light;
	}
	
	public void getHandles(int program) {
		muDirectionHandle = glGetUniformLocation(program, "light.direction");
		muHalfplaneHandle = glGetUniformLocation(program, "light.halfplane");
		muAmbientColorHandle = glGetUniformLocation(program, "light.ambient_color");
		muDiffuseColorHandle = glGetUniformLocation(program, "light.diffuse_color");
		muSpecularColorHandle = glGetUniformLocation(program, "light.specular_color");
	}
	
	public void draw() throws Exception {
		float[] dir = new float[3];
		dir = mLight.createDirectionArray();
		glUniform3f(muDirectionHandle, dir[0], dir[1], dir[2]);
		GLUtil.checkGlError("glUniform3f muDirectionHandle",TAG);
		
		float[] halfPlane = new float[3];
		halfPlane = mLight.createHalfplaneArray();
		glUniform3f(muHalfplaneHandle, halfPlane[0], halfPlane[1], halfPlane[2]);
		GLUtil.checkGlError("glUniform3f muHalfplaneHandle",TAG);
		
		float[] ambCol = new float[4];
		ambCol = mLight.createAmbientArray();
		glUniform4f(muAmbientColorHandle, ambCol[0], ambCol[1],ambCol[2],ambCol[3]);
		GLUtil.checkGlError("glUniform4f muAmbientColorHandle",TAG);
		
		float[] diffCol = new float[4];
		diffCol = mLight.createDiffuseArray();
		glUniform4f(muDiffuseColorHandle, diffCol[0], diffCol[1], diffCol[2], diffCol[3]);
		GLUtil.checkGlError("glUniform4f muDiffuseColorHandle",TAG);
		
		float[] specCol = new float[4];
		specCol = mLight.createSpecularArray();
		glUniform4f(muSpecularColorHandle, specCol[0], specCol[1], specCol[2], specCol[3]);
		GLUtil.checkGlError("glUniform4f muSpecularColorHandle",TAG);
	}
}
