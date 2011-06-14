package ch.chnoch.thesis.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ch.chnoch.thesis.renderer.interfaces.*;
import ch.chnoch.thesis.renderer.util.GLUtil;

import android.opengl.*;
import static android.opengl.GLES20.*;
import android.util.Log;

public class GLES20Renderer extends AbstractRenderer {

	private Shader mShader;
	private String mVertexShaderFileName, mFragmentShaderFileName;
	
	private GLLight mLight;

	private int mProgram;
	private int mTextureID;
	private int muMVPMatrixHandle;
	private int maVertexHandle;
	private int maTextureHandle;
	private int maNormalHandle;
	private int maLightHandle;
	private int maMaterialHandle;

	private ShortBuffer mIndexBuffer;
	private IntBuffer mVertexBuffer;
	private FloatBuffer mTexCoordsBuffer;
	private IntBuffer mColorBuffer;
	private IntBuffer mNormalBuffer;

	private boolean mEnableShader;

	private final String TAG = "GLES20Renderer";

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;

	/**
	 * This constructor is called by {@link GLRenderPanel}.
	 * 
	 * @param drawable
	 *            the OpenGL rendering context. All OpenGL calls are directed to
	 *            this object.
	 */
	public GLES20Renderer() {
		super();
	}

	public GLES20Renderer(SceneManagerInterface sceneManager) {
		super(sceneManager);
	}

	/**
	 * Pass the material properties to OpenGL, including textures and shaders.
	 */
	/*
	 * private void setMaterial(Material m) { if (m != null) { float diffuse[] =
	 * new float[4]; diffuse[0] = m.diffuse.x; diffuse[1] = m.diffuse.y;
	 * diffuse[2] = m.diffuse.z; diffuse[3] = 1.f;
	 * GLES20.glMaterialfv(GLES20.GL_FRONT_AND_BACK, GLES11.GL_DIFFUSE, diffuse,
	 * 0);
	 * 
	 * float ambient[] = new float[4]; ambient[0] = m.ambient.x; ambient[1] =
	 * m.ambient.y; ambient[2] = m.ambient.z; ambient[3] = 1.f;
	 * GLES20.glMaterialfv(GLES20.GL_FRONT_AND_BACK, GLES11.GL_AMBIENT, ambient,
	 * 0);
	 * 
	 * float specular[] = new float[4]; specular[0] = m.specular.x; specular[1]
	 * = m.specular.y; specular[2] = m.specular.z; specular[3] = 1.f;
	 * GLES20.glMaterialfv(GLES20.GL_FRONT_AND_BACK, GLES11.GL_SPECULAR,
	 * specular, 0);
	 * 
	 * GLES20.glMaterialf(GLES20.GL_FRONT_AND_BACK, GLES20.GL_SHININESS,
	 * m.shininess);
	 * 
	 * GLTexture tex = (GLTexture) (m.getTexture()); if (tex != null) {
	 * GLES20.glEnable(GLES20.GL_TEXTURE_2D);
	 * GLES20.glTexEnvf(GLES20.GL_TEXTURE_ENV, GLES20.GL_TEXTURE_ENV_MODE,
	 * GLES20.GL_MODULATE); GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
	 * tex.getId()); GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
	 * GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	 * GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
	 * GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR); } if (m.getShader() !=
	 * null) { m.getShader().use(); } } }
	 */

	/**
	 * Pass the light properties to OpenGL. This assumes the list of lights in
	 * the scene manager is accessible via a method Iterator<Light>
	 * lightIterator().
	 */
	// TODO: Figure out lights
	/*
	 * void setLights() { int lightIndex[] = { GLES20.GL_LIGHT0, GL2.GL_LIGHT1,
	 * GL2.GL_LIGHT2, GL2.GL_LIGHT3, GL2.GL_LIGHT4, GL2.GL_LIGHT5,
	 * GL2.GL_LIGHT6, GL2.GL_LIGHT7 };
	 * 
	 * gl.glMatrixMode(GL2.GL_MODELVIEW); gl.glLoadIdentity();
	 * 
	 * Iterator<Light> iter = mSceneManager.lightIterator();
	 * 
	 * if (iter.hasNext()) { // Lighting gl.glEnable(GL2.GL_LIGHTING); }
	 * 
	 * int i = 0; Light l; while (iter.hasNext() && i < 8) { l = iter.next();
	 * 
	 * gl.glEnable(lightIndex[i]);
	 * 
	 * if (l.type == Light.Type.DIRECTIONAL) { float[] direction = new float[4];
	 * direction[0] = l.direction.x; direction[1] = l.direction.y; direction[2]
	 * = l.direction.z; direction[3] = 0.f; gl.glLightfv(lightIndex[i],
	 * GL2.GL_POSITION, direction, 0); } if (l.type == Light.Type.POINT ||
	 * l.type == Light.Type.SPOT) { float[] position = new float[4]; position[0]
	 * = l.position.x; position[1] = l.position.y; position[2] = l.position.z;
	 * position[3] = 1.f; gl.glLightfv(lightIndex[i], GL2.GL_POSITION, position,
	 * 0); } if (l.type == Light.Type.SPOT) { float[] spotDirection = new
	 * float[3]; spotDirection[0] = l.spotDirection.x; spotDirection[1] =
	 * l.spotDirection.y; spotDirection[2] = l.spotDirection.z;
	 * gl.glLightfv(lightIndex[i], GL2.GL_SPOT_DIRECTION, spotDirection, 0);
	 * gl.glLightf(lightIndex[i], GL2.GL_SPOT_EXPONENT, l.spotExponent);
	 * gl.glLightf(lightIndex[i], GL2.GL_SPOT_CUTOFF, l.spotCutoff); }
	 * 
	 * float[] diffuse = new float[4]; diffuse[0] = l.diffuse.x; diffuse[1] =
	 * l.diffuse.y; diffuse[2] = l.diffuse.z; diffuse[3] = 1.f;
	 * gl.glLightfv(lightIndex[i], GL2.GL_DIFFUSE, diffuse, 0);
	 * 
	 * float[] ambient = new float[4]; ambient[0] = l.ambient.x; ambient[1] =
	 * l.ambient.y; ambient[2] = l.ambient.z; ambient[3] = 0;
	 * gl.glLightfv(lightIndex[i], GL2.GL_AMBIENT, ambient, 0);
	 * 
	 * float[] specular = new float[4]; specular[0] = l.specular.x; specular[1]
	 * = l.specular.y; specular[2] = l.specular.z; specular[3] = 0;
	 * gl.glLightfv(lightIndex[i], GL2.GL_SPECULAR, specular, 0);
	 * 
	 * i++; } }
	 */

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
		return new GLTexture();
	}

	public void onDrawFrame(GL10 gl) {
		try {
			beginFrame();

			SceneManagerIterator it = mSceneManager.iterator();

			while (it.hasNext()) {
				draw(it.next());
			}

			endFrame();
		} catch (Exception exc) {
			Log.e(TAG, "Error drawing Frame", exc);
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

		glClearColor(1, 1, 1, 1);
		glClearDepthf(1);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		try {
			mProgram = mShader.getProgram();
			if (mProgram == 0) {
				return;
			}

			Log.d(TAG, "Vertex Handle");
			maVertexHandle = glGetAttribLocation(mProgram, "aPosition");

			Log.d(TAG, "Texture Handle");
			maTextureHandle = glGetAttribLocation(mProgram, "aTextureCoord");

			Log.d(TAG, "Normal Handle");
			maNormalHandle = glGetAttribLocation(mProgram, "aNormal");
			
			Log.d(TAG, "MVP Handle");
			muMVPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix");
			
			Iterator<Light> lights =mSceneManager.lightIterator(); 
			if (lights.hasNext()) {
				mLight = new GLLight(lights.next());
				mLight.getHandles(mProgram);
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
		mNormalBuffer = buffers.getNormalBuffer();



		// cleanMaterial(renderItem.getShape().getMaterial());

		try {
			// Set the modelview matrix by multiplying the camera matrix and the
			// transformation matrix of the object
			if (muMVPMatrixHandle != -1) {
				t.set(mSceneManager.getCamera().getCameraMatrix());
				t.mul(renderItem.getT());
				glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
						GLUtil.matrix4fToFloat16(t), 0);
				GLUtil.checkGlError("glUniformMatrix4fv muMVPMatrixHandle", TAG);
			}
			
			// Ignore the passed-in GL10 interface, and use the GLES20
			// class's static methods instead.
			glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
			glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

			// glActiveTexture(GL_TEXTURE0);
			// glBindTexture(GL_TEXTURE_2D, mTextureID);

			if (maVertexHandle != -1) {
				Log.d(TAG, "Vertex Pointers");
				glVertexAttribPointer(maVertexHandle, 3, GL_UNSIGNED_SHORT,
						false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES,
						mVertexBuffer);
				GLUtil.checkGlError("glVertexAttribPointer maPosition", TAG);
				glEnableVertexAttribArray(maVertexHandle);
			}

			if (maTextureHandle != -1) {
				Log.d(TAG, "Texture Pointers");
				glVertexAttribPointer(maTextureHandle, 2, GL_FLOAT, false,
						TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mColorBuffer);
				GLUtil.checkGlError("glVertexAttribPointer maTextureHandle",
						TAG);
				glEnableVertexAttribArray(maTextureHandle);
				GLUtil.checkGlError(
						"glEnableVertexAttribArray maTextureHandle", TAG);
			}

			if (maNormalHandle != -1) {
				Log.d(TAG, "Normal Pointers");
				glVertexAttribPointer(maNormalHandle, 3, GL_UNSIGNED_SHORT,
						true, TRIANGLE_VERTICES_DATA_STRIDE_BYTES,
						mNormalBuffer);
				GLUtil.checkGlError("glVertexAttribPointer maNormalHandle", TAG);
				glEnableVertexAttribArray(maNormalHandle);
				GLUtil.checkGlError("glEnableVertexAttribArray maNormalHandle",
						TAG);
			}
			
			//Light
			if (mLight != null) {
				mLight.draw();
			}
			
			//Material
			GLMaterial mat = new GLMaterial(renderItem.getNode().getMaterial());
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
		GLUtil.checkGlError("glUseProgram", TAG);
	}

	/**
	 * This method is called at the end of each frame, i.e., after scene drawing
	 * is complete.
	 */
	private void endFrame() {
		glFlush();
	}
}
