package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.RendererInterface;
import ch.chnoch.thesis.renderer.util.Util;

/**
 * This class extends GLSurfaceView, an important class from the Android
 * framework. GLSurfaceView represents the view that is actually drawn on the
 * screen. OpenGL is used for drawing to the screen. This class holds a
 * reference to the actual renderer that will be used to render the content on
 * the screen. It also provides support for a basic translation of screen
 * coordinates to object-space coordinates.
 */
public class GLViewer extends GLSurfaceView {

	private RendererInterface mRenderer;

	private int mWidth, mHeight;

	/**
	 * Instantiates a new GLViewer. Needs a reference to the RenderContext,
	 * which renders everything to the screen.
	 * 
	 * @param context
	 *            The context that the application is running in.
	 * @param renderer
	 *            The RenderContext that is used for rendering.
	 * @param openGLES20
	 *            A flag that makes sure that all the OpenGL settings are set
	 *            appropriately if you use version 1.1 or 2.0
	 */
	public GLViewer(Context context, RendererInterface renderer) {
		super(context);
		mRenderer = renderer;
		mRenderer.setViewer(this);

		if (renderer.supportsOpenGLES20()) {
			setEGLContextClientVersion(2);
		}

		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	/**
	 * Callback method from the framework if the surface has changed. This
	 * usually happens when you rotate the screen.
	 * 
	 * @param width
	 *            The new width of the view
	 * @param height
	 *            The new height of the view
	 */
	public void surfaceHasChanged(int width, int height) {
		mWidth = width;
		mHeight = height;
		mRenderer.getSceneManager().getFrustum().setAspectRatio((float)width/height);
	}

	/**
	 * Returns the current width of the view.
	 * 
	 * @return the width
	 */
	public int width() {
		return mWidth;
	}

	/**
	 * Returns the current height of the view.
	 * 
	 * @return the height
	 */
	public int height() {
		return mHeight;
	}

	/**
	 * Unprojects the screen coordinates into a ray in the 3D space. It creates
	 * a point and a direction in the object space where the ray will pass
	 * through. This can be used if the user should be able to interact with the
	 * 3D space.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 * @return the ray in the object space
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
	 * Creates the matrices that are used for the projection from the 3D-space
	 * onto a 2D-screen. (Result=ViewMatrix*ProjectionMatrix*ViewportMatrix)
	 * 
	 * @return the complete matrix
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
