package ch.chnoch.thesis.renderer;

import java.nio.ByteBuffer;
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
	private GLViewer mViewer;
	private GL10 mGl;

	private float mAngle;

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
	}

	public void setSceneManager(SceneManagerInterface sceneManager) {
		mSceneManager = sceneManager;
		mCamera = mSceneManager.getCamera();
		mFrustum = mSceneManager.getFrustum();
	}

	public void setViewer(GLViewer viewer) {
		mViewer = viewer;
	}

	/**
	 * The main rendering method.
	 * 
	 * @param renderItem
	 *            the object that needs to be drawn
	 */
	private void draw(RenderItem renderItem, GL10 gl) {
		/*
		 * VertexData vertexData = renderItem.getShape().getVertexData();
		 * LinkedList<VertexData.VertexElement> vertexElements = vertexData
		 * .getElements(); int indices[] = vertexData.getIndices();
		 * 
		 * // Don't draw if there are no indices if (indices == null) return;
		 * 
		 * // Set the material //
		 * setMaterial(renderItem.getShape().getMaterial());
		 * 
		 * // Set the modelview matrix by multiplying the camera matrix and the
		 * // transformation matrix of the object /*
		 * GLES20.glMatrixMode(GLES20.GL_MODELVIEW); Matrix4f t = new
		 * Matrix4f(); t.set(mSceneManager.getCamera().getCameraMatrix());
		 * t.mul(renderItem.getT());
		 * GLES20.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);
		 */

		// Read geometry from the vertex element into array lists.
		// These lists will be used to create the buffers.
		/*
		 * for (int j = 0; j < indices.length; j++) { int i = indices[j];
		 * 
		 * ListIterator<VertexData.VertexElement> itr = vertexElements
		 * .listIterator(0); while (itr.hasNext()) { VertexData.VertexElement e
		 * = itr.next(); if (e.getSemantic() == VertexData.Semantic.POSITION) {
		 * if (e.getNumberOfComponents() == 2) { mVertexArray.add(e.getData()[i
		 * * 2]); mVertexArray.add(e.getData()[i * 2 + 1]); } else if
		 * (e.getNumberOfComponents() == 3) { mVertexArray.add(e.getData()[i *
		 * 3]); mVertexArray.add(e.getData()[i * 3 + 1]);
		 * mVertexArray.add(e.getData()[i * 3 + 2]); } else if
		 * (e.getNumberOfComponents() == 4) { mVertexArray.add(e.getData()[i *
		 * 4]); mVertexArray.add(e.getData()[i * 4 + 1]);
		 * mVertexArray.add(e.getData()[i * 4 + 2]);
		 * mVertexArray.add(e.getData()[i * 4 + 3]); } // } else if
		 * (e.getSemantic() == VertexData.Semantic.NORMAL) // { // if
		 * (e.getNumberOfComponents() == 3) { // gl.glNormal3f(e.getData()[i *
		 * 3], // e.getData()[i * 3 + 1], e.getData()[i * 3 + 2]); // } else if
		 * (e.getNumberOfComponents() == 4) { // gl.glVertex4f(e.getData()[i *
		 * 4], // e.getData()[i * 4 + 1], e.getData()[i * 4 + 2], //
		 * e.getData()[i * 4 + 3]); // } // } else if (e.getSemantic() == //
		 * VertexData.Semantic.TEXCOORD) { // if (e.getNumberOfComponents() ==
		 * 2) { // gl.glTexCoord2f(e.getData()[i * 2], // e.getData()[i * 2 +
		 * 1]); // } else if (e.getNumberOfComponents() == 3) { //
		 * gl.glTexCoord3f(e.getData()[i * 3], // e.getData()[i * 3 + 1],
		 * e.getData()[i * 3 + 2]); // } else if (e.getNumberOfComponents() ==
		 * 4) { // gl.glTexCoord4f(e.getData()[i * 4], // e.getData()[i * 4 +
		 * 1], e.getData()[i * 4 + 2], // e.getData()[i * 4 + 3]); // } } else
		 * if (e.getSemantic() == VertexData.Semantic.COLOR) { if
		 * (e.getNumberOfComponents() == 3) { mColorArray.add((int)
		 * e.getData()[i * 3]); mColorArray.add((int) e.getData()[i * 3 + 1]);
		 * mColorArray.add((int) e.getData()[i * 3 + 2]); } else if
		 * (e.getNumberOfComponents() == 4) { mColorArray.add((int)
		 * e.getData()[i * 4]); mColorArray.add((int) e.getData()[i * 4 + 1]);
		 * mColorArray.add((int) e.getData()[i * 4 + 2]); mColorArray.add((int)
		 * e.getData()[i * 4 + 3]); } }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * float[] vertices = Util.floatListToArray(mVertexArray); int[] colors
		 * = Util.intListToArray(mColorArray);
		 * 
		 * mVertexBuffer = FloatBuffer.wrap(vertices);
		 * mVertexBuffer.position(0); mColorBuffer = IntBuffer.wrap(colors);
		 * mColorBuffer.position(0); mIndexBuffer = IntBuffer.wrap(indices);
		 * mIndexBuffer.position(0);
		 */
		// cleanMaterial(renderItem.getShape().getMaterial());
		mGl = gl;
		Shape shape = renderItem.getShape();
		VertexBuffers buffers = shape.getVertexBuffers();
		mVertexBuffer = buffers.getVertexBuffer();
		mColorBuffer = buffers.getColorBuffer();
		mIndexBuffer = buffers.getIndexBuffer();

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// gl.glLoadIdentity();
		// gl.glTranslatef(0, 0, -3.0f);
		// gl.glRotatef(mAngleX, 0, 1, 0);
		// gl.glRotatef(mAngleY, 1, 0, 0);

		t.set(mCamera.getCameraMatrix());
		t.mul(renderItem.getT());
		gl.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);

		try {
			gl.glFrontFace(GL10.GL_CW);
			gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
			gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
			// gl.glColor4f(0.5f, 0.5f, 0f, 0.5f);
			gl.glDrawElements(GL10.GL_TRIANGLES, mIndexBuffer.capacity(),
					GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		} catch (Exception exc) {
			Log.e(TAG, "Exception drawing item", exc);
		}
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

	public SceneManagerInterface getSceneManager() {
		return mSceneManager;
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
			draw(it.next(), gl);
		}

		// mCube.draw(gl);

		// gl.glRotatef(mAngle * 2.0f, 0, 1, 1);
		// gl.glTranslatef(0.5f, 0.5f, 0.5f);

		// mAngle += 1.2f;
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "onsurfacechanged method called");
		gl.glViewport(0, 0, width, height);
		mViewer.surfaceHasChanged(width, height);
		// mFrustum.setAspectRatio(height/width);
		// reset the viewport matrix
		Matrix4f matVP = new Matrix4f();
		matVP.setM00(width / 2);
		matVP.setM03((width - 1) / 2);
		matVP.setM11(height / 2);
		matVP.setM13((height - 1) / 2);
		matVP.setM22(1);
		matVP.setM33(1);
		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadMatrixf(
				GLUtil.matrix4fToFloat16(mFrustum.getProjectionMatrix()), 0);
//		gl.glMatrixMode(GL11.GL_VIEWPORT);
//		gl.glLoadMatrixf(GLUtil.matrix4fToFloat16(matVP), 0);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.d(TAG, "onsurfactecreated method called");

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

	/*public void pick(float mouseX, float mouseY) {
		final int[] vp = new int[4];
		final int[] mv = new int[16];
		final int[] p = new int[16];
		final double[] result = new double[3];
		float mouseZ;

		mGl.glGetIntegerv(GLES11.GL_VIEWPORT, vp, 0);
		mGl.glGetIntegerv(GLES11.GL_MODELVIEW_MATRIX, mv, 0);
		mGl.glGetIntegerv(GLES11.GL_PROJECTION_MATRIX, p, 0);

		mGl.glReadPixels(mouseX, mouseY, 1, 1, GLES11.GL_DEPTH_COMPONENT,
				GLES11.GL_FLOAT, mouseZ);

		glu.gluUnProject(mouseX, mouseY, mouseZ.get(0), mv, 0, p, 0, vp, 0,
				result, 0);

		System.out.println("mouse: " + mouseX + ", " + mouseY + ", "
				+ mouseZ.get(0));
		System.out.println("world: " + result[0] + ", " + result[1] + ", "
				+ result[2]);
	}
	*/
}
