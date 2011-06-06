package ch.chnoch.thesis.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix4f;

import android.opengl.GLSurfaceView;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;

public abstract class AbstractRenderer implements RenderContext {
	
	protected SceneManagerInterface mSceneManager;
	protected Frustum mFrustum;
	protected Camera mCamera;
	protected Matrix4f mViewportMatrix;
	protected GLViewer mViewer;

	Matrix4f t = new Matrix4f();
	
	public AbstractRenderer() {
		mViewportMatrix = new Matrix4f();
	}
	
	public AbstractRenderer(SceneManagerInterface sceneManager) {
		mViewportMatrix = new Matrix4f();
		setSceneManager(sceneManager);
	}

	@Override
	public void setSceneManager(SceneManagerInterface sceneManager) {
		mSceneManager = sceneManager;
		mCamera = mSceneManager.getCamera();
		mFrustum = mSceneManager.getFrustum();
	}

	@Override
	public Matrix4f getViewportMatrix() {
		return mViewportMatrix;
		}

	@Override
	public SceneManagerInterface getSceneManager() {
		return mSceneManager;
	}

	@Override
	public void setViewer(GLViewer viewer) {
		mViewer = viewer;
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
	
	public abstract void onDrawFrame(GL10 gl);
	
	public abstract void onSurfaceChanged(GL10 gl, int width, int height);
	
	public abstract void onSurfaceCreated(GL10 gl, EGLConfig config);
	
	public abstract void createShader(Shader shader, String vertexShader, String fragmentShader)
	throws Exception;
	
	public abstract Texture makeTexture();
}
