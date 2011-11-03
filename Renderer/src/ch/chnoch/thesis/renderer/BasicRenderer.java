package ch.chnoch.thesis.renderer;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;

import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;

public class BasicRenderer extends AbstractRenderer {
	SceneManagerInterface mSceneManager;
	
	public BasicRenderer(SceneManagerInterface sceneManager) {
		mSceneManager = sceneManager;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glDisable(GL10.GL_DITHER);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        
        SceneManagerIterator it = mSceneManager.iterator();
        while (it.hasNext()) {
        	draw(gl, it.next().getNode().getShape());
        }
	}
	
	public void draw(GL10 gl, Shape shape) {
		if (shape != null) {
			VertexBuffers buffers = shape.getVertexBuffers();
			FloatBuffer vertices = buffers.getVertexBuffer();
			ShortBuffer indices = buffers.getIndexBuffer();
        gl.glFrontFace(GL10.GL_CCW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.capacity(),
                GL10.GL_UNSIGNED_SHORT, indices);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		gl.glViewport(0, 0, w, h);

        float ratio = (float) w / h;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
       gl.glDisable(GL10.GL_DITHER);

       gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
               GL10.GL_FASTEST);

       gl.glClearColor(.5f, .5f, .5f, 1);
       gl.glShadeModel(GL10.GL_SMOOTH);
       gl.glEnable(GL10.GL_DEPTH_TEST);

	}

	@Override
	public void createShader(Shader shader, String vertexShader,
			String fragmentShader) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Texture makeTexture() {
		// TODO Auto-generated method stub
		return null;
	}

}
