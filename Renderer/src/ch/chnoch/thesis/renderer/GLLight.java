package ch.chnoch.thesis.renderer;

import static android.opengl.GLES20.*;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.util.GLUtil;

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

	public void draw(Matrix4f viewMatrix) throws Exception {

		Matrix3f rotMatrix = new Matrix3f();
		if (viewMatrix != null) {
			viewMatrix.getRotationScale(rotMatrix);
		}

		if (muDirectionHandle != -1) {
			float[] dir = new float[3];
			dir = mLight.createDirectionArray(rotMatrix);
			glUniform3f(muDirectionHandle, dir[0], dir[1], dir[2]);
			GLUtil.checkGlError("glUniform3f muDirectionHandle", TAG);
		}

		if (muPositionHandle != -1) {
			float[] pos = new float[3];
			pos = mLight.createPositionArray(rotMatrix);
			glUniform3f(muPositionHandle, pos[0], pos[1], pos[2]);
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
