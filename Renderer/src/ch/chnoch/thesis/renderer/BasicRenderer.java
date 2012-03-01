package ch.chnoch.thesis.renderer;

import static android.opengl.GLES10.GL_AMBIENT;
import static android.opengl.GLES10.GL_DEPTH_BITS;
import static android.opengl.GLES10.GL_DIFFUSE;
import static android.opengl.GLES10.GL_FRONT_AND_BACK;
import static android.opengl.GLES10.GL_POSITION;
import static android.opengl.GLES10.GL_SHININESS;
import static android.opengl.GLES10.GL_SPECULAR;
import static android.opengl.GLES10.GL_VERSION;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class BasicRenderer.
 */
public class BasicRenderer implements GLSurfaceView.Renderer {

	/** The m vertex buffer. */
	protected FloatBuffer mVertexBuffer;

	/** The m normal buffer. */
	protected FloatBuffer mNormalBuffer;

	/** The m index buffer. */
	protected ShortBuffer mIndexBuffer;

	/** The m light. */
	protected Light mLight;

	/** The m material. */
	protected Material mMaterial;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.
	 * khronos.opengles.GL10)
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glDisable(GL10.GL_DITHER);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
//		setCamera(gl);

		for (int i = 0; i < 72; i++) {
			gl.glPushMatrix();
			gl.glRotatef(5f * i, 0, 1, 0);
			gl.glTranslatef(0, 0, -25);
			draw(gl);
			gl.glPopMatrix();
		}
		/*
		for (int i=1;i<10;i++){
			gl.glPushMatrix();
			gl.glTranslatef(i*2.5f,0,-25);
//			gl.glRotatef(-45, 0,1,0);
			draw(gl);
			gl.glPopMatrix();
		}
*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition
	 * .khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		gl.glViewport(0, 0, w, h);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */

		float ratio = (float) w / h;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-1, 1, -1f/ratio, 1f/ratio, 1, 100);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition
	 * .khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		int[] depthbits = new int[1];
		gl.glGetIntegerv(GL_DEPTH_BITS, depthbits, 0);
		Log.d("BasicRenderer", "Depth Bits: " + depthbits[0]);

		Log.d("BasicRenderer", "Version: " + gl.glGetString(GL_VERSION));

//		gl.glDisable(GL10.GL_DITHER);

		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(.1f, .1f, 0.1f, 1);
		gl.glClearDepthf(1f);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
//		gl.glEnable(GL10.GL_NORMALIZE);

		
		drawLights(gl);
	}

	/**
	 * Draw lights.
	 * 
	 * @param gl
	 *            the gl
	 */
	private void drawLights(GL10 gl) {

//		gl.glLightModeli(GL10.GL_LIGHT_MODEL, GL10.GL_TRUE);

		// Directional light
//		float[] position = { 0f, 0f, 1f, 0 };

		// Point Light
		

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
//		setCamera(gl);
		gl.glLightfv(GL10.GL_LIGHT0, GL_POSITION, mLight.createPositionArray(), 0);

		gl.glLightfv(GL10.GL_LIGHT0, GL_DIFFUSE, mLight.createDiffuseArray(), 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL_AMBIENT, mLight.createAmbientArray(), 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL_SPECULAR, mLight.createSpecularArray(), 0);
	}

	/**
	 * Sets the camera.
	 * 
	 * @param gl
	 *            the new camera
	 */
	private void setCamera(GL10 gl) {
		GLU.gluLookAt(gl, 0, 0, 0, 0f, 0f, -1f, 0f, 1.0f, 0.0f);
	}

	/**
	 * Draw material.
	 * 
	 * @param gl
	 *            the gl
	 */
	private void drawMaterial(GL10 gl) {

		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, mMaterial.createDiffuseArray(), 0);

		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, mMaterial.createAmbientArray(), 0);

		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, mMaterial.createSpecularArray(), 0);

		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, mMaterial.shininess);
	}

	/**
	 * Draw.
	 * 
	 * @param gl
	 *            the gl
	 */
	public void draw(GL10 gl) {
		drawMaterial(gl);
		gl.glEnable(GL10.GL_NORMALIZE);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// set the color for the triangle
		gl.glFrontFace(GL10.GL_CCW);

		// Enable the vertex and texture state
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
//		gl.glColor4f(0f, 0f, 1f, 1f);

		// Draw the vertices as triangle strip
//		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
		gl.glDrawElements(GL10.GL_TRIANGLES, mIndexBuffer.capacity(), GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);

		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.d("GLError", "OpenGL Error: " + error);
		}
	}

	/**
	 * Gets the vertex buffer.
	 * 
	 * @return the vertex buffer
	 */
	public FloatBuffer getVertexBuffer() {
		return mVertexBuffer;
	}

	/**
	 * Sets the vertex buffer.
	 * 
	 * @param mVertexBuffer
	 *            the new vertex buffer
	 */
	public void setVertexBuffer(FloatBuffer mVertexBuffer) {
		this.mVertexBuffer = mVertexBuffer;
	}

	/**
	 * Gets the normal buffer.
	 * 
	 * @return the normal buffer
	 */
	public FloatBuffer getNormalBuffer() {
		return mNormalBuffer;
	}

	/**
	 * Sets the normal buffer.
	 * 
	 * @param mNormalBuffer
	 *            the new normal buffer
	 */
	public void setNormalBuffer(FloatBuffer mNormalBuffer) {
		this.mNormalBuffer = mNormalBuffer;
	}

	/**
	 * Gets the index buffer.
	 * 
	 * @return the index buffer
	 */
	public ShortBuffer getIndexBuffer() {
		return mIndexBuffer;
	}

	/**
	 * Sets the index buffer.
	 * 
	 * @param mIndexBuffer
	 *            the new index buffer
	 */
	public void setIndexBuffer(ShortBuffer mIndexBuffer) {
		this.mIndexBuffer = mIndexBuffer;
	}
	
	/**
	 * Sets the light.
	 * 
	 * @param light
	 *            the new light
	 */
	public void setLight(Light light) {
		mLight = light;
	}
	
	/**
	 * Sets the material.
	 * 
	 * @param mat
	 *            the new material
	 */
	public void setMaterial(Material mat) {
		mMaterial = mat;
	}

	
}