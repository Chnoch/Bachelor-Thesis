package ch.chnoch.thesis.renderer;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_DITHER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glClearDepthf;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glFlush;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttrib1f;
import static android.opengl.GLES20.glVertexAttrib3f;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.util.GLUtil;

public class GLES20Renderer extends AbstractRenderer {

	private Shader mShader;
	private String mVertexShaderFileName, mFragmentShaderFileName;

	private List<Texture> mTextures;

	private GLLight mLight;

	private Context mContext;

	private int mProgram;
	private int muMVPMatrixHandle;
	private int muNormalMatrixHandle;
	private int muModelViewMatrixHandle;
	private int maVertexHandle;
	private int maTextureHandle;
	private int maHasTextureHandle;
	private int maNormalHandle;
	private int maEyeVectorHandle;

	private ShortBuffer mIndexBuffer;
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTexCoordsBuffer;
	private FloatBuffer mColorBuffer;
	private FloatBuffer mNormalBuffer;

	private boolean mEnableShader;

	private boolean mTextureChanged = false;
	private boolean mTextureBound = false;

	private final String TAG = "GLES20Renderer";

	/**
	 * This constructor is called by {@link GLRenderPanel}.
	 * 
	 * @param drawable
	 *            the OpenGL rendering context. All OpenGL calls are directed to
	 *            this object.
	 */
	public GLES20Renderer(Context context) {
		super();
		mContext = context;
	}

	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		Log.d(TAG, "onsurfacecreated method called");

		if (mEnableShader) {
			Log.d(TAG, "Enabling Shader");
			loadShader();
			mShader.use();
		}

		glDisable(GL_DITHER);

		glClearColor(0.5f, 0.5f, 0.5f, 1);

		glClearDepthf(1);

		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

		glDepthFunc(GL_LEQUAL);

		try {

			mProgram = mShader.getProgram();
			if (mProgram == 0) {
				return;
			}

			loadTextures();

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
			
			muModelViewMatrixHandle = glGetUniformLocation(mProgram, "uMVMatrix");

			Iterator<Light> lights = mSceneManager.lightIterator();
			if (lights.hasNext()) {
				mLight = new GLLight(lights.next());
				mLight.getHandles(mProgram);
				GLUtil.checkGlError("glUseProgram", TAG);

			}

		} catch (Exception exc) {
			Log.e(TAG, "Error creating surface", exc);
		}
	}

	/**
	 * The callback method that is invoked by the Android framework whenever the
	 * surface changes. Happens mostly during rotation changes.
	 * 
	 * @param glUnused
	 *            The unused OpenGL ES 1.0 pointer
	 * @param width
	 *            The new width
	 * @param height
	 *            The new height
	 */
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		Log.d(TAG, "onsurfacechanged method called");
		mViewer.surfaceHasChanged(width, height);
		setViewportMatrix(width, height);
		glViewport(0, 0, width, height);
	}

	/**
	 * The callback method that is invoked by the Android framework every time a
	 * frame is drawn.
	 * 
	 * @param glUnused
	 *            A pointer to the GL10 object. This is no longer used in OpenGL
	 *            ES 2.0
	 */
	public void onDrawFrame(GL10 glUnused) {
		try {
			beginFrame();

			SceneManagerIterator it = mSceneManager.iterator();

			while (it.hasNext()) {
				draw(it.next());
			}

			endFrame();

		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}

	/**
	 * The main rendering method for each Node.
	 * 
	 * @param renderItem
	 *            the object that needs to be drawn
	 */
	private void draw(RenderItem renderItem) {
		// Read geometry from the vertex element into array lists.
		// These lists will be used to create the buffers.
		VertexBuffers buffers = renderItem.getNode().getShape()
				.getVertexBuffers();
		mVertexBuffer = buffers.getVertexBuffer();
		mColorBuffer = buffers.getColorBuffer();
		mIndexBuffer = buffers.getIndexBuffer();
		mTexCoordsBuffer = buffers.getTexCoordsBuffer();
		mNormalBuffer = buffers.getNormalBuffer();
		mTextureBound = false;

		try {
			GLUtil.checkGlError("First test", TAG);
			// Set the modelview matrix by multiplying the camera matrix and the
			// transformation matrix of the object
			if (muMVPMatrixHandle != -1) {
				t.set(mSceneManager.getFrustum().getProjectionMatrix());
				t.mul(mSceneManager.getCamera().getCameraMatrix());
				t.mul(renderItem.getT());
				glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
						GLUtil.matrix4fToFloat16(t), 0);
				GLUtil.checkGlError("glUniformMatrix4fv muMVPMatrixHandle", TAG);
			}

			// Test for changed textures
			Material material = renderItem.getNode().getMaterial();
			if (material != null) {
				if (material.hasTextureChanged()) {
					loadTextures();
					material.setTextureChanged(false);
				}
				Texture texture = material.getTexture();
				if (texture != null) {
					glActiveTexture(GL_TEXTURE0);
					glBindTexture(GL_TEXTURE_2D, texture.getID());
					mTextureBound = true;
				}
			}

			if (maVertexHandle != -1) {
				// Log.d(TAG, "Vertex Pointers");
				glVertexAttribPointer(maVertexHandle, 3, GL_FLOAT, false, 0,
						mVertexBuffer);
				GLUtil.checkGlError("glVertexAttribPointer maPosition", TAG);
				glEnableVertexAttribArray(maVertexHandle);
			}

			if (maEyeVectorHandle != -1) {
				Vector3f cop = mSceneManager.getCamera()
						.getCenterOfProjection();
				glVertexAttrib3f(maEyeVectorHandle, cop.x, cop.y, cop.z);
//				glEnableVertexAttribArray(maEyeVectorHandle);
			}

			if (mTextureBound) {
				if (maTextureHandle != -1 && mTexCoordsBuffer != null) {
					// Log.d(TAG, "Texture Pointers");
					glVertexAttribPointer(maTextureHandle, 2, GL_FLOAT, false,
							0, mTexCoordsBuffer);
					GLUtil.checkGlError(
							"glVertexAttribPointer maTextureHandle", TAG);
					glEnableVertexAttribArray(maTextureHandle);
					GLUtil.checkGlError(
							"glEnableVertexAttribArray maTextureHandle", TAG);
//					glVertexAttrib1f(maHasTextureHandle, 2);
				} else if (maHasTextureHandle != -1) {
					glVertexAttrib1f(maHasTextureHandle, 0);
				}
			} else if (maHasTextureHandle != -1) {
				glVertexAttrib1f(maHasTextureHandle, 0);
				GLUtil.checkGlError("glVertexAttribPointer maNormalHandle", TAG);
			}

			if (muNormalMatrixHandle != -1 && maNormalHandle != -1
					&& mNormalBuffer != null) {
				// rotate normals
				t.set(mSceneManager.getCamera().getCameraMatrix());
				t.mul(renderItem.getT());
				t.invert();
				t.transpose();

				// Log.d(TAG, "Normal Pointers");
				glVertexAttribPointer(maNormalHandle, 3, GL_FLOAT, true, 0,
						mNormalBuffer);
				GLUtil.checkGlError("glVertexAttribPointer maNormalHandle", TAG);
				glEnableVertexAttribArray(maNormalHandle);
				GLUtil.checkGlError("glEnableVertexAttribArray maNormalHandle",
						TAG);
				glUniformMatrix4fv(muNormalMatrixHandle, 1, false,
						GLUtil.matrix4fToFloat16(t), 0);
				GLUtil.checkGlError("glUniformMatrix4fv muNormalMatrixHandle",
						TAG);
			}
			if (muModelViewMatrixHandle != -1) {
				t.set(mSceneManager.getCamera().getCameraMatrix());
				t.mul(renderItem.getT());
				glUniformMatrix4fv(muModelViewMatrixHandle, 1, false, GLUtil.matrix4fToFloat16(t), 0);
				GLUtil.checkGlError("glUniformMatrix4fv muModelViewMatrixHandle",
						TAG);
			}

			// Light
			if (mLight != null) {
				t.set(mSceneManager.getCamera().getCameraMatrix());
//				t.invert();
				mLight.draw(t);
//				mLight.draw(Util.getIdentityMatrix());
			}

			// Material
			GLMaterial mat = (GLMaterial) renderItem.getNode().getMaterial();
			if (mat != null) {
				mat.getHandles(mProgram);
				mat.draw();
			}

//			Log.d(TAG, "Draw");
			glDrawElements(GL_TRIANGLES, mIndexBuffer.capacity(),
					GL_UNSIGNED_SHORT, mIndexBuffer);
			GLUtil.checkGlError("glDrawArrays", TAG);
		} catch (Exception exc) {
			Log.e(TAG, "Exception drawing item", exc);
		}
	}

	public void createShader(Shader shader, String vertexFileName,
			String fragmentFileName) throws Exception {
		mEnableShader = true;
		mShader = shader;
		mVertexShaderFileName = vertexFileName;
		mFragmentShaderFileName = fragmentFileName;
	}

	public Texture createTexture() {
		Texture tex = new GLTexture(mContext);

		if (mTextures == null) {
			mTextures = new ArrayList<Texture>();
		}
		mTextures.add(tex);
		return tex;
	}

	/**
	 * This method is called at the beginning of each frame, i.e., before scene
	 * drawing starts.
	 * 
	 * @throws Exception
	 */
	private void beginFrame() throws Exception {
		// setLights();

		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
		glUseProgram(mProgram);

		if (mTextureChanged) {
			loadTextures();
		}
	}

	/**
	 * This method is called at the end of each frame, i.e., after scene drawing
	 * is complete.
	 */
	private void endFrame() {
		glFlush();
	}

	private void loadTextures() {
		if (mTextures != null) {
			for (Texture tex : mTextures) {
				tex.load();
			}
		}
	}

	private void loadShader() {
		mShader = new GLShader();
		try {
			mShader.load(mVertexShaderFileName, mFragmentShaderFileName);
		} catch (Exception exc) {
			Log.e(TAG, "Could not load Shaders. Check sources of shaders. "
					+ exc.getMessage());
		}
	}

	private void cleanMaterial(Material m) {
		if (m != null && m.getTexture() != null) {
			GLES20.glDisable(GLES20.GL_TEXTURE_2D);
		}
		if (m != null && m.getShader() != null) {
			m.getShader().disable();
		}
	}
}
