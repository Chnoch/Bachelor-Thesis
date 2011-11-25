package ch.chnoch.thesis.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.opengl.*;
import android.util.Log;
import static android.opengl.GLES20.*;

import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.util.GLUtil;
import ch.chnoch.thesis.renderer.util.Util;

public class BasicGLES20Renderer extends BasicRenderer {

	private int mProgram;
	private int mTextureID;
	private int muMVPMatrixHandle;
	private int muNormalMatrixHandle;
	private int muModelViewMatrixHandle;
	private int maVertexHandle;
	private int maTextureHandle;
	private int maHasTextureHandle;
	private int maNormalHandle;
	private int maEyeVectorHandle;
	private int maLightHandle;
	private int maMaterialHandle;

	private Matrix4f mModelMatrix = Util.getIdentityMatrix();

	private Shader mShader;

	private GLLight mGLLight;

	private final String TAG = "BasicGLES20Renderer";
	private String mVertexShaderFileName;
	private String mFragmentShaderFileName;

	public void onDrawFrame(GL10 gl) {
		for (int i = 0; i < 72; i++) {
			mModelMatrix.setIdentity();
			mModelMatrix.setTranslation(new Vector3f(0, 0, -25));
			mModelMatrix.setRotation(new AxisAngle4f(0f, 1f, 0f, 5f * i));
			draw();
		}
	}

	private void draw() {
		try {
			drawMaterial();
			glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
					GLUtil.matrix4fToFloat16(mModelMatrix), 0);
			glVertexAttribPointer(maVertexHandle, 3, GL_FLOAT, false, 0,
					mVertexBuffer);
			glEnableVertexAttribArray(maVertexHandle);
			
			glVertexAttribPointer(maNormalHandle, 3, GL_FLOAT, true, 0,
					mNormalBuffer);
			glEnableVertexAttribArray(maNormalHandle);
			Matrix4f t = new Matrix4f(mModelMatrix);
			t.transpose();
			t.invert();
			glUniformMatrix4fv(muNormalMatrixHandle, 1, false,
					GLUtil.matrix4fToFloat16(t), 0);
			
			glUniformMatrix4fv(muModelViewMatrixHandle, 1, 
					false, GLUtil.matrix4fToFloat16(mModelMatrix), 0);
			
			mGLLight.draw(null);
			glDrawElements(GL_TRIANGLES, mIndexBuffer.capacity(),
					GL_UNSIGNED_SHORT, mIndexBuffer);
			
			
			GLUtil.checkGlError("glDraw", TAG);

		} catch (Exception exc) {

		}
	}

	private void drawMaterial() throws Exception {
		GLMaterial mat = (GLMaterial) mMaterial;
		mat.getHandles(mProgram);
		mat.draw();
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		glViewport(0, 0, w, h);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glDisable(GL_DITHER);
		try {

			makeShader();
			mShader.use();

			mProgram = mShader.getProgram();
			if (mProgram == 0) {
				return;
			}

			// Log.d(TAG, "Vertex Handle");
			maVertexHandle = glGetAttribLocation(mProgram, "aPosition");
			GLUtil.checkGlError("glUseProgram", TAG);

			// Log.d(TAG, "Texture Handle");
			maTextureHandle = glGetAttribLocation(mProgram, "aTextureCoord");
			GLUtil.checkGlError("glUseProgram", TAG);

			maHasTextureHandle = glGetAttribLocation(mProgram, "aHasTexture");
			GLUtil.checkGlError("glUseProgram", TAG);

			// Log.d(TAG, "Normal Handle");
			maNormalHandle = glGetAttribLocation(mProgram, "aNormals");
			GLUtil.checkGlError("glUseProgram", TAG);

			maEyeVectorHandle = glGetAttribLocation(mProgram, "aEyeVector");
			GLUtil.checkGlError("glUseProgram", TAG);

			// Log.d(TAG, "MVP Handle");
			muMVPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix");
			if (muMVPMatrixHandle == -1) {
				throw new RuntimeException("No MVPMatrix Handle");
			}

			muNormalMatrixHandle = glGetUniformLocation(mProgram,
					"uNormalMatrix");

			muModelViewMatrixHandle = glGetUniformLocation(mProgram,
					"uMVMatrix");

			mGLLight = new GLLight(mLight);

			mGLLight.getHandles(mProgram);

			GLUtil.checkGlError("glUseProgram", TAG);
		} catch (Exception exc) {

		}
	}

	public void createShader(Shader shader, String vertexShader,
			String fragmentShader) throws Exception {
		mShader = shader;
		mVertexShaderFileName = vertexShader;
		mFragmentShaderFileName = fragmentShader;
	}

	private void makeShader() {
		mShader = new GLShader();
		try {
			mShader.load(mVertexShaderFileName, mFragmentShaderFileName);
		} catch (Exception exc) {
			Log.e(TAG, "Could not load Shaders. Check sources of shaders. "
					+ exc.getMessage());
		}
	}

	public Texture makeTexture() {
		return null;
	}

	private void beginFrame() throws Exception {
		// setLights();

		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
		glUseProgram(mProgram);
	}

	/**
	 * This method is called at the end of each frame, i.e., after scene drawing
	 * is complete.
	 */
	private void endFrame() {
		glFlush();
	}

}
