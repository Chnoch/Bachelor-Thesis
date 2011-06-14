package ch.chnoch.thesis.renderer;

import static android.opengl.GLES20.*;

public class GLMaterial {
	private Material mMaterial;
	
	private int muAmbientColorHandle, muDiffuseColorHandle, muSpecularColorHandle, muSpecularExponentHandle;
	
	public GLMaterial(Material material) {
		mMaterial = material;
	}
	
	public void getHandles(int program) {
		muAmbientColorHandle = glGetUniformLocation(program, "material_properties.ambient_color");
		muDiffuseColorHandle= glGetUniformLocation(program, "material_properties.diffuse_color");
		muSpecularColorHandle = glGetUniformLocation(program, "material_properties.specular_color");
		muSpecularExponentHandle = glGetUniformLocation(program, "material_properties.specular_exponent");
	}
	
	public void draw() throws Exception {
		glUniform4fv(muAmbientColorHandle, 4, mMaterial.createAmbientArray(),0);
		glUniform4fv(muDiffuseColorHandle, 4, mMaterial.createDiffuseArray(),0);
		glUniform4fv(muSpecularColorHandle, 4, mMaterial.createSpecularArray(),0);
		glUniform1f(muSpecularExponentHandle, mMaterial.shininess);
	}
}
