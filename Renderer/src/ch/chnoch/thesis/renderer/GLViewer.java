package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.util.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class GLViewer.
 */
public class GLViewer extends GLSurfaceView {

	/** The m renderer. */
	private RenderContext mRenderer;

	/** The m height. */
	private int mWidth, mHeight;

	/**
	 * Instantiates a new gL viewer.
	 * 
	 * @param context
	 *            the context
	 */
	public GLViewer(Context context) {
		super(context);
		// Turn on error-checking and logging
		// setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR |
		// GLSurfaceView.DEBUG_LOG_GL_CALLS);
	}

	/**
	 * Instantiates a new gL viewer.
	 * 
	 * @param context
	 *            the context
	 * @param renderer
	 *            the renderer
	 * @param openGLES20
	 *            the open gle s20
	 */
	public GLViewer(Context context, RenderContext renderer, boolean openGLES20) {
		super(context);
		mRenderer = renderer;
		mRenderer.setViewer(this);

		if (openGLES20) {
			setEGLContextClientVersion(2);
		}

		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	/**
	 * Surface has changed.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public void surfaceHasChanged(int width, int height) {
		mWidth = width;
		mHeight = height;
		mRenderer.getSceneManager().getFrustum().setAspectRatio((float)width/height);
	}

	/**
	 * Width.
	 * 
	 * @return the int
	 */
	public int width() {
		return mWidth;
	}

	/**
	 * Height.
	 * 
	 * @return the int
	 */
	public int height() {
		return mHeight;
	}

	/**
	 * Unproject.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the ray
	 */
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

	/**
	 * Creates the matrices.
	 * 
	 * @return the matrix4f
	 */
	private Matrix4f createMatrices() {

		Matrix4f staticMatrix = new Matrix4f(mRenderer.getViewportMatrix());
		Matrix4f projMatrix = mRenderer.getSceneManager().getFrustum()
				.getProjectionMatrix();
		staticMatrix.mul(projMatrix);
		Matrix4f cameraMatrix = mRenderer.getSceneManager().getCamera()
				.getCameraMatrix();
		staticMatrix.mul(cameraMatrix);

		return staticMatrix;
	}

}
