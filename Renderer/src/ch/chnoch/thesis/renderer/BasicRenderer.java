package ch.chnoch.thesis.renderer;

import static android.opengl.GLES10.GL_AMBIENT;
import static android.opengl.GLES10.GL_DEPTH_BITS;
import static android.opengl.GLES10.GL_DIFFUSE;
import static android.opengl.GLES10.GL_FRONT_AND_BACK;
import static android.opengl.GLES10.GL_POSITION;
import static android.opengl.GLES10.GL_SHININESS;
import static android.opengl.GLES10.GL_SPECULAR;
import static android.opengl.GLES10.GL_VERSION;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

public class BasicRenderer implements GLSurfaceView.Renderer {

	private FloatBuffer mVertexBuffer;
	private FloatBuffer mNormalBuffer;
	private ShortBuffer mIndexBuffer;

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

		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

		ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
		nbb.order(ByteOrder.nativeOrder());
		mNormalBuffer = nbb.asFloatBuffer();
		mNormalBuffer.put(normals);
		mNormalBuffer.position(0);

		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);

		drawLights(gl);
	}

	private void drawLights(GL10 gl) {

//		gl.glLightModeli(GL10.GL_LIGHT_MODEL, GL10.GL_TRUE);

		// Directional light
//		float[] position = { 0f, 0f, 1f, 0 };

		// Point Light
		float[] position = { 0f, 0f, 0, 1 };
		float[] diffuse = { .6f, .6f, .6f, 1f };
		float[] specular = { 1, 1, 1, 1 };
		float[] ambient = { 0.2f, 0.2f, .2f, 1 };

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
//		setCamera(gl);
		gl.glLightfv(GL10.GL_LIGHT0, GL_POSITION, position, 0);

		gl.glLightfv(GL10.GL_LIGHT0, GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL_AMBIENT, ambient, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL_SPECULAR, specular, 0);
	}

	private void setCamera(GL10 gl) {
		GLU.gluLookAt(gl, 0, 0, 0, 0f, 0f, -1f, 0f, 1.0f, 0.0f);
	}

	private void setMaterial(GL10 gl) {
		float shininess = 30;
		float[] ambient = { 0, 0, 0.3f, 1 };
		float[] diffuse = { 0, 0, .7f, 1 };
		float[] specular = { 1, 1, 1, 1 };

		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, diffuse, 0);

		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, ambient, 0);

		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, specular, 0);

		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, shininess);
	}

	public void draw(GL10 gl) {
		setMaterial(gl);
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

	private float vertices[] = {
			// Vertices for the square
			-1.0f, -1.0f, 0.0f, // 0. left-bottom
			1.0f, -1.0f, 0.0f, // 1. right-bottom
			-1.0f, 1.0f, 0.0f, // 2. left-top
			1.0f, 1.0f, 0.0f // 3. right-top
	};

	private short indices[] = {
			0,1,3,
			0,3,2
	};

	private float normals[] = {
			0,0,1,
			0,0,1,
			0,0,1,
			0,0,1
	};
}