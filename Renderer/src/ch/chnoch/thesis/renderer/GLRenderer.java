package ch.chnoch.thesis.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.util.GLUtil;
import ch.chnoch.thesis.renderer.util.Util;

import android.content.Context;
import android.opengl.*;
import android.os.SystemClock;
import android.util.Log;

public class GLRenderer implements GLSurfaceView.Renderer {

	private Context mContext;
	private SceneManagerInterface mSceneManager;
	private Shader mShader;

	private float[] mMVPMatrix = new float[16];
	private float[] mProjMatrix = new float[16];
	private float[] mMMatrix = new float[16];
	private float[] mVMatrix = new float[16];

	private int mProgram;
	private int mTextureID;
	private int muMVPMatrixHandle;
	private int maPositionHandle;
	private int maTextureHandle;

	private IntBuffer mIndexBuffer;
	private FloatBuffer mVertexBuffer;
	// private IntBuffer mTextureBuffer;
	private FloatBuffer mColorBuffer;
	// private IntBuffer mNormalBuffer;

	private List<Float> mVertexArray;
	private List<Float> mColorArray;

	private final String TAG = "GLRenderer";

	/**
	 * This constructor is called by {@link GLRenderPanel}.
	 * 
	 * @param drawable
	 *            the OpenGL rendering context. All OpenGL calls are directed to
	 *            this object.
	 */
	public GLRenderer(Context context) {
		mContext = context;

		mVertexArray = new ArrayList<Float>();
		mColorArray = new ArrayList<Float>();
	}

	public void setSceneManager(SceneManagerInterface sceneManager) {
		mSceneManager = sceneManager;
	}

	/**
	 * The main rendering method.
	 * 
	 * @param renderItem
	 *            the object that needs to be drawn
	 */
	private void draw(RenderItem renderItem) {
		VertexData vertexData = renderItem.getShape().getVertexData();
		LinkedList<VertexData.VertexElement> vertexElements = vertexData
				.getElements();
		int indices[] = vertexData.getIndices();

		// Don't draw if there are no indices
		if (indices == null)
			return;

		// Set the material
		// setMaterial(renderItem.getShape().getMaterial());

		// Set the modelview matrix by multiplying the camera matrix and the
		// transformation matrix of the object
		GLES20.glMatrixMode(GLES20.GL_MODELVIEW);
		Matrix4f t = new Matrix4f();
		t.set(mSceneManager.getCamera().getCameraMatrix());
		t.mul(renderItem.getT());
		GLES20.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);

		// Read geometry from the vertex element into array lists.
		// These lists will be used to create the buffers.
		for (int j = 0; j < indices.length; j++) {
			int i = indices[j];

			ListIterator<VertexData.VertexElement> itr = vertexElements
					.listIterator(0);
			while (itr.hasNext()) {
				VertexData.VertexElement e = itr.next();
				if (e.getSemantic() == VertexData.Semantic.POSITION) {
					if (e.getNumberOfComponents() == 2) {
						mVertexArray.add(e.getData()[i * 2]);
						mVertexArray.add(e.getData()[i * 2 + 1]);
					} else if (e.getNumberOfComponents() == 3) {
						mVertexArray.add(e.getData()[i * 3]);
						mVertexArray.add(e.getData()[i * 3 + 1]);
						mVertexArray.add(e.getData()[i * 3 + 2]);
					} else if (e.getNumberOfComponents() == 4) {
						mVertexArray.add(e.getData()[i * 4]);
						mVertexArray.add(e.getData()[i * 4 + 1]);
						mVertexArray.add(e.getData()[i * 4 + 2]);
						mVertexArray.add(e.getData()[i * 4 + 3]);
					}
					// } else if (e.getSemantic() == VertexData.Semantic.NORMAL)
					// {
					// if (e.getNumberOfComponents() == 3) {
					// gl.glNormal3f(e.getData()[i * 3],
					// e.getData()[i * 3 + 1], e.getData()[i * 3 + 2]);
					// } else if (e.getNumberOfComponents() == 4) {
					// gl.glVertex4f(e.getData()[i * 4],
					// e.getData()[i * 4 + 1], e.getData()[i * 4 + 2],
					// e.getData()[i * 4 + 3]);
					// }
					// } else if (e.getSemantic() ==
					// VertexData.Semantic.TEXCOORD) {
					// if (e.getNumberOfComponents() == 2) {
					// gl.glTexCoord2f(e.getData()[i * 2],
					// e.getData()[i * 2 + 1]);
					// } else if (e.getNumberOfComponents() == 3) {
					// gl.glTexCoord3f(e.getData()[i * 3],
					// e.getData()[i * 3 + 1], e.getData()[i * 3 + 2]);
					// } else if (e.getNumberOfComponents() == 4) {
					// gl.glTexCoord4f(e.getData()[i * 4],
					// e.getData()[i * 4 + 1], e.getData()[i * 4 + 2],
					// e.getData()[i * 4 + 3]);
					// }
				} else if (e.getSemantic() == VertexData.Semantic.COLOR) {
					if (e.getNumberOfComponents() == 3) {
						mColorArray.add(e.getData()[i * 3]);
						mColorArray.add(e.getData()[i * 3 + 1]); 
						mColorArray.add(e.getData()[i * 3 + 2]);
					} else if (e.getNumberOfComponents() == 4) {
						mColorArray.add(e.getData()[i * 4]);
						mColorArray.add(e.getData()[i * 4 + 1]);
						mColorArray.add(e.getData()[i * 4 + 2]);
						mColorArray.add(e.getData()[i * 4 + 3]);
					}
				}

			}

		}
		
		float[] vertices = Util.listToArray(mVertexArray);
		float[] colors = Util.listToArray(mColorArray);
		
		mVertexBuffer = FloatBuffer.wrap(vertices);
		mColorBuffer = FloatBuffer.wrap(colors);
//		cleanMaterial(renderItem.getShape().getMaterial());
		// Ignore the passed-in GL10 interface, and use the GLES20
		// class's static methods instead.
		GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glUseProgram(mProgram);
		GLUtil.checkGlError("glUseProgram", TAG);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

		mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
		GLUtil.checkGlError("glVertexAttribPointer maPosition", TAG);
		mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
		GLES20.glEnableVertexAttribArray(maPositionHandle);
		GLUtil.checkGlError("glEnableVertexAttribArray maPositionHandle", TAG);
		GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT,
				false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
		GLUtil.checkGlError("glVertexAttribPointer maTextureHandle", TAG);
		GLES20.glEnableVertexAttribArray(maTextureHandle);
		GLUtil.checkGlError("glEnableVertexAttribArray maTextureHandle", TAG);

		long time = SystemClock.uptimeMillis() % 4000L;
		float angle = 0.090f * ((int) time);
		Matrix.setRotateM(mMMatrix, 0, angle, 0, 0, 1.0f);
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
		GLUtil.checkGlError("glDrawArrays", TAG);

	}

	/**
	 * This method is called at the beginning of each frame, i.e., before scene
	 * drawing starts.
	 * 
	 * @throws Exception
	 */
	private void beginFrame() throws Exception {
		// setLights();

		GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glUseProgram(mProgram);
		GLUtil.checkGlError("glUseProgram", TAG);
	}

	/**
	 * This method is called at the end of each frame, i.e., after scene drawing
	 * is complete.
	 */
	private void endFrame() {
		GLES20.glFlush();
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

	public Shader makeShader() {
		mShader = new GLShader();
		return mShader;
	}

	public Texture makeTexture() {
		return new GLTexture(mContext);
	}

	@Override
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

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		try {
			mProgram = mShader.getProgram();
			if (mProgram == 0) {
				return;
			}

			maPositionHandle = GLES20
					.glGetAttribLocation(mProgram, "aPosition");
			GLUtil.checkGlError("glGetAttribLocation aPosition", TAG);
			if (maPositionHandle == -1) {
				throw new RuntimeException(
						"Could not get attrib location for aPosition");
			}

			maTextureHandle = GLES20.glGetAttribLocation(mProgram,
					"aTextureCoord");
			GLUtil.checkGlError("glGetAttribLocation aTextureCoord", TAG);
			if (maTextureHandle == -1) {
				throw new RuntimeException(
						"Could not get attrib location for aTextureCoord");
			}

			muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,
					"uMVPMatrix");
			GLUtil.checkGlError("glGetUniformLocation uMVPMatrix", TAG);
			if (muMVPMatrixHandle == -1) {
				throw new RuntimeException(
						"Could not get attrib location for uMVPMatrix");
			}
		} catch (Exception exc) {
			Log.e(TAG, "Error creating surface", exc);
		}
	}
}
