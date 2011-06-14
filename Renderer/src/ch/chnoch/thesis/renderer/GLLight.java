package ch.chnoch.thesis.renderer;

import static android.opengl.GLES20.*;

public class GLLight {
	private Light mLight;

	private int muDirectionHandle, muHalfplaneHandle, muAmbientColorHandle,
			muDiffuseColorHandle, muSpecularColorHandle;

	public GLLight(Light light) {
		mLight = light;
	}
	
	public void getHandles(int program) {
		muDirectionHandle = glGetUniformLocation(program, "directional_light.direction");
		muHalfplaneHandle = glGetUniformLocation(program, "directional_light.halfplane");
		muAmbientColorHandle = glGetUniformLocation(program, "directional_light.ambient_color");
		muDiffuseColorHandle = glGetUniformLocation(program, "directional_light.diffuse_color");
		muSpecularColorHandle = glGetUniformLocation(program, "directional_light.specular_color");
	}
	
	public void draw() throws Exception {
		glUniform3fv(muDirectionHandle, 3, mLight.createDirectionArray(), 0);
		glUniform3fv(muHalfplaneHandle, 3, mLight.createHalfplaneArray(), 0);
		glUniform4fv(muAmbientColorHandle, 4, mLight.createAmbientArray(), 0);
		glUniform4fv(muDiffuseColorHandle, 4, mLight.createDiffuseArray(), 0);
		glUniform4fv(muSpecularColorHandle, 4, mLight.createSpecularArray(), 0);
	}
}
