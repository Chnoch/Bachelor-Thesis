package ch.chnoch.thesis.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.*;
import ch.chnoch.thesis.renderer.util.GLUtil;

import android.content.Context;
import android.opengl.*;
import static android.opengl.GLES10.GL_FASTEST;
import static android.opengl.GLES10.GL_PERSPECTIVE_CORRECTION_HINT;
import static android.opengl.GLES10.GL_PROJECTION;
import static android.opengl.GLES10.GL_SMOOTH;
import static android.opengl.GLES20.*;
import android.util.Log;

public class GLES20Renderer extends AbstractRenderer {

	private Shader mShader;
	private String mVertexShaderFileName, mFragmentShaderFileName;

	private List<Texture> mTextures;

	private GLLight mLight;

	private Context mContext;

	private int mProgram;
	private int mTextureID;
	private int muMVPMatrixHandle;
	private int maVertexHandle;
	private int maTextureHandle;
	private int maNormalHandle;
	private int maLightHandle;
	private int maMaterialHandle;

	private ShortBuffer mIndexBuffer;
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTexCoordsBuffer;
	private FloatBuffer mColorBuffer;
	private FloatBuffer mNormalBuffer;

	private boolean mEnableShader;
	
	private boolean mTextureChanged = false;

	private final String TAG = "GLES20Renderer";

	private static final int FLOAT_SIZE_BYTES = 4;

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

	public GLES20Renderer(Context context, SceneManagerInterface sceneManager) {
		super(sceneManager);
		mContext = context;
	}

	private void cleanMaterial(Material m) {
		if (m != null && m.getTexture() != null) {
			GLES20.glDisable(GLES20.GL_TEXTURE_2D);
		}
		if (m != null && m.getShader() != null) {
			m.getShader().disable();
		}
	}

	public void createShader(Shader shader, String vertexFileName,
			String fragmentFileName) throws Exception {
		mEnableShader = true;
		mShader = shader;
		mVertexShaderFileName = vertexFileName;
		mFragmentShaderFileName = fragmentFileName;
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
		Texture tex = new GLTexture(mContext);

		if (mTextures == null) {
			mTextures = new ArrayList<Texture>();
		}
		mTextures.add(tex);
		return tex;
	}

	private void loadTextures() {
		if (mTextures != null) {
			for (Texture tex : mTextures) {
				tex.load();
			}
		}
	}

	public void onDrawFrame(GL10 gl) {
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

	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		Log.d(TAG, "onsurfacechanged method called");
		mViewer.surfaceHasChanged(width, height);
		setViewportMatrix(width, height);
		glViewport(0, 0, width, height);
	}

	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		Log.d(TAG, "onsurfacecreated method called");
		int[] depthbits = new int[1];
		glGetIntegerv(GL_DEPTH_BITS, depthbits, 0);
		Log.d(TAG, "Depth Bits: " + depthbits[0]);

		if (mEnableShader) {
			Log.d(TAG, "Enabling Shader");
			makeShader();
			mShader.use();
		}

		glDisable(GL_DITHER);

		// glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

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

			Log.d(TAG, "Vertex Handle");
			maVertexHandle = glGetAttribLocation(mProgram, "aPosition");
			GLUtil.checkGlError("glUseProgram", TAG);

			Log.d(TAG, "Texture Handle");
			maTextureHandle = glGetAttribLocation(mProgram, "aTextureCoord");
			GLUtil.checkGlError("glUseProgram", TAG);

			Log.d(TAG, "Normal Handle");
			maNormalHandle = glGetAttribLocation(mProgram, "aNormals");
			GLUtil.checkGlError("glUseProgram", TAG);

			Log.d(TAG, "MVP Handle");
			muMVPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix");
			if (muMVPMatrixHandle == -1) {
				throw new RuntimeException("No MVPMatrix Handle");
			}

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
	 * The main rendering method.
	 * 
	 * @param renderItem
	 *            the object that needs to be drawn
	 */
	private void draw(RenderItem renderItem) {
		// Set the material
		// setMaterial(renderItem.getShape().getMaterial());
		// Read geometry from the vertex element into array lists.
		// These lists will be used to create the buffers.
		VertexBuffers buffers = renderItem.getNode().getShape()
				.getVertexBuffers();
		mVertexBuffer = buffers.getVertexBuffer();
		mColorBuffer = buffers.getColorBuffer();
		mIndexBuffer = buffers.getIndexBuffer();
		mTexCoordsBuffer = buffers.getTexCoordsBuffer();
		if (mTexCoordsBuffer != null) {
			for (int i = 0; i < mTexCoordsBuffer.capacity(); i++) {
				Log.d(TAG, "Value: " + mTexCoordsBuffer.get());
			}
			mTexCoordsBuffer.position(0);
		}
		mNormalBuffer = buffers.getNormalBuffer();

		// cleanMaterial(renderItem.getShape().getMaterial());

		try {
			// Set the modelview matrix by multiplying the camera matrix and the
			// transformation matrix of the object
			// if (muMVPMatrixHandle != -1) {
			t.set(mSceneManager.getFrustum().getProjectionMatrix());
			t.mul(mSceneManager.getCamera().getCameraMatrix());
			t.mul(renderItem.getT());
			glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
					GLUtil.matrix4fToFloat16(t), 0);
			GLUtil.checkGlError("glUniformMatrix4fv muMVPMatrixHandle", TAG);
			// }

			// Ignore the passed-in GL10 interface, and use the GLES20
			// class's static methods instead.
			glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

			// Test for changed texture
			Material material = renderItem.getNode().getMaterial();
			if (material.hasTextureChanged()) {
				loadTextures();
				material.setTextureChanged(false);
			}
			Texture texture = material.getTexture();
			if (texture != null) {
				glActiveTexture(GL_TEXTURE0);
				glBindTexture(GL_TEXTURE_2D, texture.getID());
			}

			if (maVertexHandle != -1) {
				Log.d(TAG, "Vertex Pointers");
				glVertexAttribPointer(maVertexHandle, 3, GL_FLOAT, false, 0,
						mVertexBuffer);
				GLUtil.checkGlError("glVertexAttribPointer maPosition", TAG);
				glEnableVertexAttribArray(maVertexHandle);
			}

			if (maTextureHandle != -1 && mTexCoordsBuffer!= null) {
				Log.d(TAG, "Texture Pointers");
				glVertexAttribPointer(maTextureHandle, 2, GL_FLOAT, false, 0,
						mTexCoordsBuffer);
				GLUtil.checkGlError("glVertexAttribPointer maTextureHandle",
						TAG);
				glEnableVertexAttribArray(maTextureHandle);
				GLUtil.checkGlError(
						"glEnableVertexAttribArray maTextureHandle", TAG);
			}

			if (maNormalHandle != -1 && mNormalBuffer != null) {
				// rotate normals
				FloatBuffer normals = mNormalBuffer.duplicate();
				Matrix4f matrix = renderItem.getT();
				Matrix3f rotation = new Matrix3f();
				matrix.getRotationScale(rotation);
				for (int i = 0; i < mNormalBuffer.capacity(); i += 3) {
					float x = mNormalBuffer.get(i);
					float y = mNormalBuffer.get(i + 1);
					float z = mNormalBuffer.get(i + 2);

					Vector3f vec = new Vector3f(x, y, z);
					rotation.transform(vec);
					// vec.normalize();
					normals.put(i, vec.x);
					normals.put(i + 1, vec.y);
					normals.put(i + 2, vec.z);
				}

				Log.d(TAG, "Normal Pointers");
				glVertexAttribPointer(maNormalHandle, 3, GL_FLOAT, true, 0,
						normals);
				GLUtil.checkGlError("glVertexAttribPointer maNormalHandle", TAG);
				glEnableVertexAttribArray(maNormalHandle);
				GLUtil.checkGlError("glEnableVertexAttribArray maNormalHandle",
						TAG);
			}

			// Light
			if (mLight != null) {
				mLight.draw();
			}

			// Material
			GLMaterial mat = (GLMaterial) renderItem.getNode().getMaterial();
			mat.getHandles(mProgram);
			mat.draw();

			Log.d(TAG, "Draw");
			glDrawElements(GL_TRIANGLES, mIndexBuffer.capacity(),
					GL_UNSIGNED_SHORT, mIndexBuffer);
			GLUtil.checkGlError("glDrawArrays", TAG);
		} catch (Exception exc) {
			Log.e(TAG, "Exception drawing item", exc);
		}
	}

	/**
	 * This method is called at the beginning of each frame, i.e., before scene
	 * drawing starts.
	 * 
	 * @throws Exception
	 */
	private void beginFrame() throws Exception {
		// setLights();

		glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
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

}
