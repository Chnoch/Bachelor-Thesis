package ch.chnoch.thesis.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.util.GLUtil;

import static android.opengl.GLES10.GL_CCW;
import static android.opengl.GLES10.GL_FIXED;
import static android.opengl.GLES10.GL_MODELVIEW;
import static android.opengl.GLES10.GL_NORMAL_ARRAY;
import static android.opengl.GLES10.GL_TRIANGLES;
import static android.opengl.GLES10.GL_UNSIGNED_SHORT;
import static android.opengl.GLES10.GL_VERTEX_ARRAY;
import static android.opengl.GLES20.*;

public class GL2DRenderer extends AbstractRenderer {

	private int COLOR_ATTR = 1;
	private int VERTEX_ATTR = 1;
	private ShortBuffer mIndexBuffer;
	private IntBuffer mVertexBuffer;
	private FloatBuffer mTexCoordsBuffer;
	private IntBuffer mColorBuffer;
	private IntBuffer mNormalBuffer;

	private final String TAG = "GL2DRenderer";

	public GL2DRenderer() {
		super();
	}

	public GL2DRenderer(SceneManagerInterface sceneManager) {
		super(sceneManager);
	}

	@Override
	public Shader makeShader(String vertexShader, String fragmentShader)
			throws Exception {
		return null;
	}

	@Override
	public Texture makeTexture() {
		return null;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		SceneManagerIterator shapeIterator = mSceneManager.iterator();

		/*
		 * Usually, the first thing one might want to do is to clear the screen.
		 * The most efficient way of doing this is to use glClear().
		 */
		gl.glClear(GL_COLOR_BUFFER_BIT);

		while (shapeIterator.hasNext()) {
			draw(shapeIterator.next(), gl);
		}

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int x, int y) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, 0, x, y, 0, 1);
		gl.glDisable(GL_DEPTH_TEST);
//		gl.glMatrixMode(GL10.GL_MODELVIEW);
//		gl.glLoadIdentity();

		// Displacement trick for exact pixelization
		// gl.glTranslatex(24576, 24576, 0);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(1, 1, 1, 1);
	}

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
		
		gl.glMatrixMode(GL_MODELVIEW);
		t.set(mCamera.getCameraMatrix());
		t.mul(renderItem.getT());

		gl.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);

		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glFrontFace(GL_CCW);
		gl.glVertexPointer(3, GL_FIXED, 0, mVertexBuffer);
		gl.glColorPointer(4, GL_FIXED, 0, mColorBuffer);
		gl.glEnableClientState(GL_NORMAL_ARRAY);
		gl.glNormalPointer(GL_FIXED, 0, mNormalBuffer);
		// gl.glEnable(GL_TEXTURE_2D);
		// gl.glTexCoordPointer(2, GL_FLOAT, 0, mTexCoordsBuffer);
		gl.glDrawElements(GL_TRIANGLES, mIndexBuffer.capacity(),
				GL_UNSIGNED_SHORT, mIndexBuffer);
		gl.glDisableClientState(GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL_VERTEX_ARRAY);
	}
}
