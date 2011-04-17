package ch.chnoch.thesis.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.util.GLUtil;
import ch.chnoch.thesis.renderer.util.Util;

import android.content.Context;
import android.opengl.*;
import android.util.Log;

public class GLRenderer10 implements RenderContext {

	private Context mContext;
	private SceneManagerInterface mSceneManager;
	private Frustum mFrustum;
	private Camera mCamera;
	private Shader mShader;
	private Matrix4f mViewportMatrix;
	private GLViewer mViewer;

	private int mProgram;

	Matrix4f t = new Matrix4f();

	private ShortBuffer mIndexBuffer;
	private IntBuffer mVertexBuffer;
	// private IntBuffer mTextureBuffer;
	private IntBuffer mColorBuffer;
	// private IntBuffer mNormalBuffer;

	private List<Float> mVertexArray;
	private List<Integer> mColorArray;

	public float mAngleX = 0;
	public float mAngleY = 0;

	private final String TAG = "GLRenderer10";

	/**
	 * This constructor is called by {@link GLRenderPanel}.
	 * 
	 * @param drawable
	 *            the OpenGL rendering context. All OpenGL calls are directed to
	 *            this object.
	 */
	public GLRenderer10(Context context) {
		mContext = context;

		mVertexArray = new ArrayList<Float>();
		mColorArray = new ArrayList<Integer>();
		mViewportMatrix = new Matrix4f();
	}

	public void setSceneManager(SceneManagerInterface sceneManager) {
		mSceneManager = sceneManager;
		mCamera = mSceneManager.getCamera();
		mFrustum = mSceneManager.getFrustum();
	}

	public void setViewer(GLViewer viewer) {
		mViewer = viewer;
	}

	public Matrix4f getViewportMatrix() {
		return mViewportMatrix;
	}

	/**
	 * The main rendering method.
	 * 
	 * @param renderItem
	 *            the object that needs to be drawn
	 */
	private void draw(RenderItem renderItem, GL10 gl) {
		
		Shape shape = renderItem.getNode().getShape();
		VertexBuffers buffers = shape.getVertexBuffers();
		mVertexBuffer = buffers.getVertexBuffer();
		mColorBuffer = buffers.getColorBuffer();
		mIndexBuffer = buffers.getIndexBuffer();

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		t.set(mCamera.getCameraMatrix());
		t.mul(renderItem.getT());

		gl.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);

		gl.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		gl.glFrontFace(GL10.GL_CW);
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
		gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, mIndexBuffer.capacity(),
				GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		gl.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
	}

	public void onDrawFrame(GL10 gl) {
		calculateFPS();
		long oldTime = System.currentTimeMillis();

		SceneManagerIterator it = mSceneManager.iterator();

		/*
		 * Usually, the first thing one might want to do is to clear the screen.
		 * The most efficient way of doing this is to use glClear().
		 */
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		/*
		 * Now we're ready to draw some 3D objects
		 */
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		while (it.hasNext()) {
			draw(it.next(), gl);
		}
		long newTime = System.currentTimeMillis();

		long diff = newTime - oldTime;
		Log.d("RendererFrame", "Rendering single frame: " + diff + " ms");

	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "onsurfacechanged method called");
		mViewer.surfaceHasChanged(width, height);
		setViewportMatrix(width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadMatrixf(
				GLUtil.matrix4fToFloat16(mFrustum.getProjectionMatrix()), 0);
		gl.glViewport(0, 0, width, height);
	}

	public void setViewportMatrix(int width, int height) {
		// reset the viewport matrix
		mViewportMatrix.setM00(width / 2.f);
		mViewportMatrix.setM03((width - 1) / 2.f);

		mViewportMatrix.setM11(height / 2.f);
		mViewportMatrix.setM13((height - 1) / 2.f);

		mViewportMatrix.setM22(1);
		mViewportMatrix.setM33(1);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.d(TAG, "onsurfacecreated method called");

		/*
		 * By default, OpenGL enables features that improve quality but reduce
		 * performance. One might want to tweak that especially on software
		 * renderer.
		 */
		gl.glDisable(GL10.GL_DITHER);

		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(1, 1, 1, 1);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glMatrixMode(GLES10.GL_PROJECTION);
		gl.glLoadMatrixf(
				GLUtil.matrix4fToFloat16(mFrustum.getProjectionMatrix()), 0);
	}

	public SceneManagerInterface getSceneManager() {
		return mSceneManager;
	}

	public Shader makeShader() {
		// TODO Auto-generated method stub
		return null;
	}

	public Texture makeTexture() {
		// TODO Auto-generated method stub
		return null;
	}

	public Matrix4f createMatrices() {
		SceneManagerInterface sceneManager = getSceneManager();
		Camera camera = sceneManager.getCamera();
		Frustum frustum = sceneManager.getFrustum();

		Matrix4f staticMatrix = new Matrix4f(getViewportMatrix());
		staticMatrix.mul(frustum.getProjectionMatrix());
		staticMatrix.mul(camera.getCameraMatrix());

		return staticMatrix;
	}

	private int frameCount;
	private long currentTime, previousTime = 0;
	private float fps;

	// -------------------------------------------------------------------------
	// Calculates the frames per second
	// -------------------------------------------------------------------------
	void calculateFPS() {
		// Increase frame count
		frameCount++;

		// Get the number of milliseconds since glutInit called
		// (or first call to glutGet(GLUT ELAPSED TIME)).
		currentTime = System.currentTimeMillis();

		// Calculate time passed
		long timeInterval = currentTime - previousTime;

		if (timeInterval > 1000) {
			// calculate the number of frames per second
			fps = timeInterval / frameCount;

			// Set time
			previousTime = currentTime;

			// Reset frame count
			frameCount = 0;

			Log.d("Renderer", "Rendertime per Frame: " + fps + " ms");
		}
	}
}
