package ch.chnoch.thesis.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.RendererInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;

/**
 * This class implements some, but not all of the common tasks of a renderer.
 * Other renderers (software / hardware) should inherit from this class.
 */
public abstract class AbstractRenderer implements RendererInterface {
	
	/** The scene manager. */
	protected SceneManagerInterface mSceneManager;

	/** The frustum. */
	protected Frustum mFrustum;

	/** The camera. */
	protected Camera mCamera;

	/** The viewport matrix. */
	protected Matrix4f mViewportMatrix;

	/** The viewer. */
	protected GLViewer mViewer;

	/** The translation matrix */
	Matrix4f t = new Matrix4f();
	
	/**
	 * Instantiates a new abstract renderer.
	 */
	public AbstractRenderer() {
		mViewportMatrix = new Matrix4f();
	}
	
	/**
	 * Instantiates a new abstract renderer.
	 * 
	 * @param sceneManager
	 *            the scene manager
	 */
	public AbstractRenderer(SceneManagerInterface sceneManager) {
		mViewportMatrix = new Matrix4f();
		setSceneManager(sceneManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.RendererInterface#setSceneManager(ch
	 * .chnoch.thesis.renderer.interfaces.SceneManagerInterface)
	 */
	@Override
	public void setSceneManager(SceneManagerInterface sceneManager) {
		mSceneManager = sceneManager;
		mCamera = mSceneManager.getCamera();
		mFrustum = mSceneManager.getFrustum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.RendererInterface#getViewportMatrix
	 * ()
	 */
	@Override
	public Matrix4f getViewportMatrix() {
		return mViewportMatrix;
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.RendererInterface#getSceneManager()
	 */
	@Override
	public SceneManagerInterface getSceneManager() {
		return mSceneManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.RendererInterface#setViewer(ch.chnoch
	 * .thesis.renderer.GLViewer)
	 */
	@Override
	public void setViewer(GLViewer viewer) {
		mViewer = viewer;
	}

	/**
	 * Sets the viewport matrix.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public void setViewportMatrix(int width, int height) {
		// reset the viewport matrix
		mViewportMatrix.setM00(width / 2.f);
		mViewportMatrix.setM03((width) / 2.f);

		mViewportMatrix.setM11(height / 2.f);
		mViewportMatrix.setM13((height) / 2.f);

		mViewportMatrix.setM22(1/2.f);
		mViewportMatrix.setM33(1);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.
	 * khronos.opengles.GL10)
	 */
	public abstract void onDrawFrame(GL10 gl);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition
	 * .khronos.opengles.GL10, int, int)
	 */
	public abstract void onSurfaceChanged(GL10 gl, int width, int height);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition
	 * .khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	public abstract void onSurfaceCreated(GL10 gl, EGLConfig config);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.RendererInterface#createShader(ch
	 * .chnoch .thesis.renderer.interfaces.Shader, java.lang.String,
	 * java.lang.String)
	 */
	public abstract void createShader(Shader shader, String vertexShader, String fragmentShader)
	throws Exception;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.RendererInterface#createTexture()
	 */
	public abstract Texture createTexture() throws Exception;
}
