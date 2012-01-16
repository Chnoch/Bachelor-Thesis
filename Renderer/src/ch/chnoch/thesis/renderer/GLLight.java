package ch.chnoch.thesis.renderer;

import static android.opengl.GLES20.*;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.util.Log;

import ch.chnoch.thesis.renderer.util.GLUtil;
import ch.chnoch.thesis.renderer.util.Util;

public class GLLight {
	private Light mLight;
	private static final String TAG = "GLLight";

	private int muDirectionHandle, muPositionHandle, muAmbientColorHandle,
			muDiffuseColorHandle, muSpecularColorHandle;

	public GLLight(Light light) {
		mLight = light;
	}

	public void getHandles(int program) {
		muDirectionHandle = glGetUniformLocation(program, "light.direction");
		muPositionHandle = glGetUniformLocation(program, "light.position");
		muAmbientColorHandle = glGetUniformLocation(program, "light.ambient");
		muDiffuseColorHandle = glGetUniformLocation(program, "light.diffuse");
		muSpecularColorHandle = glGetUniformLocation(program, "light.specular");
	}

	public void draw(Matrix4f transformation) throws Exception {

		if (muDirectionHandle != -1) {
			float[] dir = new float[3];
			dir = mLight.createDirectionArray();
			glUniform3f(muDirectionHandle, dir[0], dir[1], dir[2]);
			GLUtil.checkGlError("glUniform3f muDirectionHandle", TAG);
		}

		if (muPositionHandle != -1) {
			Vector3f pos = new Vector3f(mLight.getPosition());
			Util.transform(transformation, pos);
//			transformation.transform(pos);
			
			glUniform3f(muPositionHandle, pos.x, pos.y, pos.z);
			GLUtil.checkGlError("glUniform3f muPositionHandle", TAG);
		}

		float[] ambCol = new float[4];
		ambCol = mLight.createAmbientArray();
		glUniform4f(muAmbientColorHandle, ambCol[0], ambCol[1], ambCol[2],
				ambCol[3]);
		GLUtil.checkGlError("glUniform4f muAmbientColorHandle", TAG);

		float[] diffCol = new float[4];
		diffCol = mLight.createDiffuseArray();
		glUniform4f(muDiffuseColorHandle, diffCol[0], diffCol[1], diffCol[2],
				diffCol[3]);
		GLUtil.checkGlError("glUniform4f muDiffuseColorHandle", TAG);

		float[] specCol = new float[4];
		specCol = mLight.createSpecularArray();
		glUniform4f(muSpecularColorHandle, specCol[0], specCol[1], specCol[2],
				specCol[3]);
		GLUtil.checkGlError("glUniform4f muSpecularColorHandle", TAG);
	}
}
