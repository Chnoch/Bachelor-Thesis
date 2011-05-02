package ch.chnoch.thesis.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
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
	private FloatBuffer mTexCoordsBuffer;
	private IntBuffer mColorBuffer;
	private IntBuffer mNormalBuffer;

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

		mViewportMatrix = new Matrix4f();
	}

	/*
	 * Public Methods
	 */

	public void setSceneManager(SceneManagerInterface sceneManager) {
		mSceneManager = sceneManager;
		mCamera = mSceneManager.getCamera();
		mFrustum = mSceneManager.getFrustum();
	}

	public SceneManagerInterface getSceneManager() {
		return mSceneManager;
	}

	public void setViewer(GLViewer viewer) {
		mViewer = viewer;
	}

	public Matrix4f getViewportMatrix() {
		return mViewportMatrix;
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

	/*
	 * Framework Callback Methods
	 */

	public void onDrawFrame(GL10 gl) {
		calculateFPS();
		long oldTime = System.currentTimeMillis();

		SceneManagerIterator shapeIterator = mSceneManager.iterator();

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

		while (shapeIterator.hasNext()) {
			draw(shapeIterator.next(), gl);
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
		
		setLights(gl);
	}

	/*
	 * Private Methods
	 */

	/**
	 * The main rendering method for one item.
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
		mTexCoordsBuffer = buffers.getTexCoordsBuffer();
		mNormalBuffer = buffers.getNormalBuffer();

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		t.set(mCamera.getCameraMatrix());
		t.mul(renderItem.getT());

		gl.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);
		
		setMaterial(renderItem.getNode().getMaterial(), gl);

		gl.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		gl.glFrontFace(GL10.GL_CW);
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
//		gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glNormalPointer(GL10.GL_FIXED, 0, mNormalBuffer);
//		gl.glEnable(GL10.GL_TEXTURE_2D);
//		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexCoordsBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, mIndexBuffer.capacity(),
				GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		gl.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
	}

	private void setLights(GL10 gl) {
		int lightIndex[] = { GL10.GL_LIGHT0, GL10.GL_LIGHT1, GL10.GL_LIGHT2,
				GL10.GL_LIGHT3, GL10.GL_LIGHT4, GL10.GL_LIGHT5, GL10.GL_LIGHT6,
				GL10.GL_LIGHT7 };

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glLoadIdentity();
		
		Iterator<Light> iter = mSceneManager.lightIterator();

		int i = 0;
		Light l;
		while (iter.hasNext() && i < 8) {
			l = iter.next();
			gl.glEnable(lightIndex[i]);

			if (l.type == Light.Type.DIRECTIONAL) {
				gl.glLightfv(lightIndex[i], GL10.GL_POSITION, l.createDirectionArray(), 0);
			}
			if (l.type == Light.Type.POINT || l.type == Light.Type.SPOT) {
				gl.glLightfv(lightIndex[i], GL10.GL_POSITION, l.createPositionArray(), 0);
			}
			if (l.type == Light.Type.SPOT) {
				gl.glLightfv(lightIndex[i], GL10.GL_SPOT_DIRECTION,
						l.createSpotDirectionArray(), 0);
				gl.glLightf(lightIndex[i], GL10.GL_SPOT_EXPONENT,
						l.spotExponent);
				gl.glLightf(lightIndex[i], GL10.GL_SPOT_CUTOFF, l.spotCutoff);
			}

			gl.glLightfv(lightIndex[i], GL10.GL_DIFFUSE, l.createDiffuseArray(), 0);
			gl.glLightfv(lightIndex[i], GL10.GL_AMBIENT, l.createAmbientArray(), 0);
			gl.glLightfv(lightIndex[i], GL10.GL_SPECULAR, l.createSpecularArray(), 0);

			i++;
		}
		
		gl.glEnable(GL10.GL_CCW);
	}
	
	/**
	 * Pass the material properties to OpenGL, including textures and shaders.
	 */
	private void setMaterial(Material m, GL10 gl)
	{
		if(m!=null)
		{
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, m.createDiffuseArray(), 0);
			
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, m.createAmbientArray(), 0);

			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, m.createSpecularArray(), 0);

			gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, m.shininess);
		}
	}

	// only used to calculate rendering time per frame
	private int frameCount;
	private long currentTime, previousTime = 0;
	private float fps;

	private void calculateFPS() {
		// Increase frame count
		frameCount++;

		currentTime = System.currentTimeMillis();

		// Calculate time passed
		long timeInterval = currentTime - previousTime;

		if (timeInterval > 1000) {
			fps = timeInterval / frameCount;

			previousTime = currentTime;

			frameCount = 0;

			Log.d("Renderer", "Rendertime per Frame: " + fps + " ms");
		}
	}
}
