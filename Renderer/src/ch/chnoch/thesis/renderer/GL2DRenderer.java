package ch.chnoch.thesis.renderer;

import static android.opengl.GLES10.GL_CCW;
import static android.opengl.GLES10.GL_FIXED;
import static android.opengl.GLES10.GL_MODELVIEW;
import static android.opengl.GLES10.GL_NORMAL_ARRAY;
import static android.opengl.GLES10.GL_TRIANGLES;
import static android.opengl.GLES10.GL_UNSIGNED_SHORT;
import static android.opengl.GLES10.GL_VERTEX_ARRAY;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLException;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerIterator;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.util.GLUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class GL2DRenderer.
 */
public class GL2DRenderer extends AbstractRenderer {

	/** The COLO r_ attr. */
	private int COLOR_ATTR = 1;

	/** The VERTE x_ attr. */
	private int VERTEX_ATTR = 1;

	/** The m index buffer. */
	private ShortBuffer mIndexBuffer;

	/** The m vertex buffer. */
	private FloatBuffer mVertexBuffer;

	/** The m tex coords buffer. */
	private FloatBuffer mTexCoordsBuffer;

	/** The m color buffer. */
	private FloatBuffer mColorBuffer;

	/** The m normal buffer. */
	private FloatBuffer mNormalBuffer;

	/** The TAG. */
	private final String TAG = "GL2DRenderer";

	/**
	 * Instantiates a new g l2 d renderer.
	 */
	public GL2DRenderer() {
		super();
	}

	/**
	 * Instantiates a new g l2 d renderer.
	 * 
	 * @param sceneManager
	 *            the scene manager
	 */
	public GL2DRenderer(SceneManagerInterface sceneManager) {
		super(sceneManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.AbstractRenderer#createShader(ch.chnoch.thesis
	 * .renderer.interfaces.Shader, java.lang.String, java.lang.String)
	 */
	@Override
	public void createShader(Shader shader, String vertexShader, String fragmentShader)
			throws Exception {
		throw new GLException(0, "OpenGL ES 1.1 does not support shaders");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#createTexture()
	 */
	@Override
	public Texture createTexture() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.AbstractRenderer#onDrawFrame(javax.microedition
	 * .khronos.opengles.GL10)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#onSurfaceChanged(javax.
	 * microedition.khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int x, int y) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, 0, 5, 5, 0, 1);
		gl.glViewport(0, 0, x, y);
//		gl.glMatrixMode(GL10.GL_MODELVIEW);
//		gl.glLoadIdentity();

		// Displacement trick for exact pixelization
		// gl.glTranslatex(24576, 24576, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#onSurfaceCreated(javax.
	 * microedition.khronos.opengles.GL10,
	 * javax.microedition.khronos.egl.EGLConfig)
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(1, 1, 1, 1);
		gl.glDisable(GL_DEPTH_TEST);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
	}

	/**
	 * The main rendering method for one item.
	 * 
	 * @param renderItem
	 *            the object that needs to be drawn
	 * @param gl
	 *            the gl
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

	@Override
	public boolean supportsOpenGLES20() {
		return false;
	}
}
