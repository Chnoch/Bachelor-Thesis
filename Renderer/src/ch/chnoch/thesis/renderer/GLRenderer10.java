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
	private void draw(RenderItem renderItem, GL11 gl) {
		Shape shape = renderItem.getNode().getShape();
		VertexBuffers buffers = shape.getVertexBuffers();
		mVertexBuffer = buffers.getVertexBuffer();
		mColorBuffer = buffers.getColorBuffer();
		mIndexBuffer = buffers.getIndexBuffer();

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		t.set(mCamera.getCameraMatrix());
		t.mul(renderItem.getT());
		Log.d("RenderItem Matrix", renderItem.getT().toString());
		gl.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);
		
		gl.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		gl.glFrontFace(GL10.GL_CW);
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
		gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, mIndexBuffer.capacity(),
				GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		gl.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
	}

	private void drawBox(RenderItem item, GL10 gl) {
		BoundingBox box = item.getNode().getBoundingBox();
		float[] vertices = getVertices(box);
		int[] intVert = new int[24];

		// fixed point conversion made
		for (int i = 0; i < intVert.length; i++) {
			intVert[i] = (int) vertices[i];
		}

		int[] indices = getIndices();

		ByteBuffer vbb = ByteBuffer.allocateDirect(intVert.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		IntBuffer vertexBuffer = vbb.asIntBuffer();
		vertexBuffer.put(intVert);
		vertexBuffer.position(0);

		vbb = ByteBuffer.allocateDirect(indices.length*4);
		vbb.order(ByteOrder.nativeOrder());
		IntBuffer indexBuffer = vbb.asIntBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
		gl.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

		gl.glVertexPointer(3, GLES10.GL_UNSIGNED_SHORT, 0, vertexBuffer);
		gl.glColor4f(1, 1,1, 1);
//		gl.glDrawElements(GLES11.GL_TRIANGLES, indexBuffer.capacity(), GLES10.GL_UNSIGNED_SHORT, indexBuffer);

		gl.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
	}

	private int[] getIndices() {
		int[] indices = new int[36];
		indices[0] = 0;
		indices[1] = 2;
		indices[2] = 0;

		indices[3] = 0;
		indices[4] = 4;
		indices[5] = 0;

		indices[6] = 0;
		indices[7] = 1;
		indices[8] = 0;

		indices[9] = 4;
		indices[10] = 6;
		indices[11] = 4;
		
		indices[12] = 2;
		indices[13] = 6;
		indices[14] = 2;
		
		indices[15] = 2;
		indices[16] = 3;
		indices[17] = 2;
		
		indices[18] = 1;
		indices[19] = 3;
		indices[20] = 1;
		
		indices[21] = 1;
		indices[22] = 5;
		indices[23] = 1;
		
		indices[24] = 4;
		indices[25] = 5;
		indices[26] = 4;
		
		indices[27] = 5;
		indices[28] = 7;
		indices[29] = 5;
		
		indices[30] = 6;
		indices[31] = 7;
		indices[32] = 6;
		
		indices[33] = 3;
		indices[34] = 7;
		indices[35] = 3;
		
		return indices;
	}

	private float[] getVertices(BoundingBox box) {
		Vector3f low = box.getLow();
		Vector3f high = box.getHigh();
		int one = Util.one;
		float[] vertices = new float[24];

		vertices[0] = one * low.x;
		vertices[1] = one * low.y;
		vertices[2] = one * low.z;

		vertices[3] = one * low.x;
		vertices[4] = one * low.y;
		vertices[5] = one * high.z;

		vertices[6] = one * low.x;
		vertices[7] = one * high.y;
		vertices[8] = one * low.z;

		vertices[9] = one * low.x;
		vertices[10] = one * high.y;
		vertices[11] = one * high.z;

		vertices[12] = one * high.x;
		vertices[13] = one * low.y;
		vertices[14] = one * low.z;

		vertices[15] = one * high.x;
		vertices[16] = one * low.y;
		vertices[17] = one * high.z;

		vertices[18] = one * high.x;
		vertices[19] = one * high.y;
		vertices[20] = one * low.z;

		vertices[21] = one * high.x;
		vertices[22] = one * high.y;
		vertices[23] = one * high.z;

		return vertices;
	}

	public void onDrawFrame(GL10 gl) {
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
			drawShapeAndBox(it.next(), (GL11) gl);
		}

	}

	private void drawShapeAndBox(RenderItem item, GL11 gl) {
		draw(item, gl);
		drawBox(item, gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "onsurfacechanged method called");
		mViewer.surfaceHasChanged(width, height);
		Log.d("Width & Height", width + ", " + height);
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
}
