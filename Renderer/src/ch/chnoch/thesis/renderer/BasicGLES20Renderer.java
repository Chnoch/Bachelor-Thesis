package ch.chnoch.thesis.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.opengl.*;
import android.os.SystemClock;
import android.util.Log;
import static android.opengl.GLES20.*;

import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.util.GLUtil;
import ch.chnoch.thesis.renderer.util.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class BasicGLES20Renderer.
 */
public class BasicGLES20Renderer extends BasicRenderer {

	/** The m program. */
	private int mProgram;
	
	/** The mu mvp matrix handle. */
	private int muMVPMatrixHandle;
	
	/** The mu normal matrix handle. */
	private int muNormalMatrixHandle;
	
	/** The mu model view matrix handle. */
	private int muModelViewMatrixHandle;
	
	/** The ma vertex handle. */
	private int maVertexHandle;
	
	/** The ma normal handle. */
	private int maNormalHandle;

	/** The m model matrix. */
	private Matrix4f mModelMatrix = Util.getIdentityMatrix();

	/** The m shader. */
	private Shader mShader;

	/** The m gl light. */
	private GLLight mGLLight;

	/** The TAG. */
	private final String TAG = "BasicGLES20Renderer";
	
	/** The m vertex shader file name. */
	private String mVertexShaderFileName;
	
	/** The m fragment shader file name. */
	private String mFragmentShaderFileName;
	
	/** The m mvp matrix. */
	private float[] mMVPMatrix = new float[16];
	
	/** The m proj matrix. */
	private float[] mProjMatrix = new float[16];
	
	/** The m m matrix. */
	private float[] mMMatrix = new float[16];
	
	/** The m v matrix. */
	private float[] mVMatrix = new float[16];
	
	/** The m mv matrix. */
	private float[] mMVMatrix = new float[16];
	
	/** The m normal matrix. */
	private float[] mNormalMatrix = new float[16];

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.BasicRenderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	public void onDrawFrame(GL10 gl) {
//		GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
//		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glUseProgram(mProgram);
		for (int i = 0; i < mMVPMatrix.length; i++) {
			Log.d(TAG, "MVPMatrix Android: " + mMVPMatrix[i]);
		}
		
		float radius = 20;
		for (int i = 0; i < 72; i++) {
			float angleDegree = 5f*i;
			float angle = (float) (5f*i * Math.PI / 180);
			float x = (float) Math.sin(angle) * radius;
			float z = (float) Math.cos(angle) * radius;
			Log.d(TAG, "angle: " + angleDegree + "x: " + x + "z: " + z);
			Matrix.setIdentityM(mMMatrix, 0);
			Matrix.rotateM(mMMatrix, 0, angleDegree, 0, 1, 0);
			Matrix.translateM(mMMatrix, 0, x, 0, z);

			Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
			Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
			Matrix.multiplyMM(mMVMatrix,0, mVMatrix, 0, mMMatrix, 0);
//			mModelMatrix.setIdentity();
			// mModelMatrix.setTranslation(new Vector3f(0, 0, 0));
			// mModelMatrix.set(new AxisAngle4f(0f, 0f, 25f, 5f * i));
			draw();
		}
	}

	/**
	 * Draw.
	 */
	private void draw() {
		try {
			drawMaterial();
			glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
					mMVPMatrix, 0);

			glVertexAttribPointer(maVertexHandle, 3, GL_FLOAT, false, 0,
					mVertexBuffer);
			glEnableVertexAttribArray(maVertexHandle);

			glVertexAttribPointer(maNormalHandle, 3, GL_FLOAT, true, 0,
					mNormalBuffer);
			glEnableVertexAttribArray(maNormalHandle);
			GLUtil.checkGlError("maNormal", TAG);

			Matrix.setIdentityM(mNormalMatrix, 0);
			Matrix.multiplyMM(mNormalMatrix, 0, mNormalMatrix, 0, mMVMatrix, 0);
			Matrix.invertM(mNormalMatrix, 0, mNormalMatrix, 0);
			Matrix.transposeM(mNormalMatrix, 0, mNormalMatrix, 0);
//			Log.d(TAG, "Handle: " + muNormalMatrixHandle);
			glUniformMatrix4fv(muNormalMatrixHandle, 1, false,
					mNormalMatrix, 0);
			GLUtil.checkGlError("muNormalMatrix", TAG);

			glUniformMatrix4fv(muModelViewMatrixHandle, 1, false,
					mMVMatrix, 0);

			GLUtil.checkGlError("mModelMatrix", TAG);
			mGLLight.draw(Util.getIdentityMatrix());
			glDrawElements(GL_TRIANGLES, mIndexBuffer.capacity(),
					GL_UNSIGNED_SHORT, mIndexBuffer);

			GLUtil.checkGlError("glDraw", TAG);

		} catch (Exception exc) {

		}
	}

	/**
	 * Draw material.
	 *
	 * @throws Exception the exception
	 */
	private void drawMaterial() throws Exception {
		GLMaterial mat = (GLMaterial) mMaterial;
		mat.getHandles(mProgram);
		mat.draw();
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.BasicRenderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		glViewport(0, 0, w, h);
		float ratio = (float) w / h;
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.BasicRenderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glDisable(GL_DITHER);
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, 0, 0f, 0f, 1f, 0f, 1.0f, 0.0f);
		try {

			makeShader();
			mShader.use();

			mProgram = mShader.getProgram();
			if (mProgram == 0) {
				return;
			}

			// Log.d(TAG, "Vertex Handle");
			maVertexHandle = glGetAttribLocation(mProgram, "aPosition");
			GLUtil.checkGlError("maVertexHandle", TAG);

			// Log.d(TAG, "Normal Handle");
			maNormalHandle = glGetAttribLocation(mProgram, "aNormals");
			GLUtil.checkGlError("maNormalHandle", TAG);

			// Log.d(TAG, "MVP Handle");
			muMVPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix");
			if (muMVPMatrixHandle == -1) {
				throw new RuntimeException("No MVPMatrix Handle");
			}

			muNormalMatrixHandle = glGetUniformLocation(mProgram,
					"uNormalMatrix");
			if (muNormalMatrixHandle == -1) {
				throw new RuntimeException("No NormalMatrix Handle");
			}

			muModelViewMatrixHandle = glGetUniformLocation(mProgram,
					"uMVMatrix");

			mGLLight = new GLLight(mLight);

			mGLLight.getHandles(mProgram);

			GLUtil.checkGlError("glUseProgram", TAG);
		} catch (Exception exc) {
			Log.e(TAG, "Error creating surface", exc);
		}
	}

	/**
	 * Creates the shader.
	 *
	 * @param shader the shader
	 * @param vertexShader the vertex shader
	 * @param fragmentShader the fragment shader
	 * @throws Exception the exception
	 */
	public void createShader(Shader shader, String vertexShader,
			String fragmentShader) throws Exception {
		mShader = shader;
		mVertexShaderFileName = vertexShader;
		mFragmentShaderFileName = fragmentShader;
	}

	/**
	 * Make shader.
	 */
	private void makeShader() {
		mShader = new GLShader();
		try {
			mShader.load(mVertexShaderFileName, mFragmentShaderFileName);
		} catch (Exception exc) {
			Log.e(TAG, "Could not load Shaders. Check sources of shaders. "
					+ exc.getMessage());
		}
	}

	/**
	 * Make texture.
	 *
	 * @return the texture
	 */
	public Texture makeTexture() {
		return null;
	}

	/**
	 * Check gl error.
	 *
	 * @param op the op
	 */
	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}

}
