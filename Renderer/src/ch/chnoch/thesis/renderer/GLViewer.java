package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.util.Util;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class GLViewer extends GLSurfaceView {

	private RenderContext mRenderer;

	private int mWidth, mHeight;

	public GLViewer(Context context) {
		super(context);
		// Turn on error-checking and logging
		// setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR |
		// GLSurfaceView.DEBUG_LOG_GL_CALLS);
	}

	public GLViewer(Context context, RenderContext renderer, boolean openGLES20) {
		super(context);
		mRenderer = renderer;
		mRenderer.setViewer(this);

		setEGLConfigChooser(true);
		// Turn on error-checking and logging
		// setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR |
		// GLSurfaceView.DEBUG_LOG_GL_CALLS);
		if (openGLES20){
			setEGLContextClientVersion(2);
		}
			
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	public void surfaceHasChanged(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public int width() {
		return mWidth;
	}

	public int height() {
		return mHeight;
	}

	public Ray unproject(float x, float y) {

		Matrix4f staticMatrix = createMatrices();
		Matrix4f inverse;

		Vector3f origin = new Vector3f(x, y, 1);
		Vector3f direction = new Vector3f(x, y, -1);
		inverse = new Matrix4f(staticMatrix);
		try {
			inverse.invert();

		} catch (RuntimeException exc) {
			// Matrix not invertable, therefore no action.
			Log.e("UNPROJECT", "Matrix can't be inverted");
			return null;
		}

		Util.transform(inverse, origin);
		Util.transform(inverse, direction);

		direction.sub(origin);
		direction.normalize();

		return new Ray(origin, direction);
	}

	private Matrix4f createMatrices() {

		Matrix4f staticMatrix = new Matrix4f(mRenderer.getViewportMatrix());
		Matrix4f projMatrix = mRenderer.getSceneManager().getFrustum()
		.getProjectionMatrix(false);
		staticMatrix.mul(projMatrix);
		Matrix4f cameraMatrix = mRenderer.getSceneManager().getCamera()
		.getCameraMatrix();
		staticMatrix.mul(cameraMatrix);

		return staticMatrix;
	}

}
