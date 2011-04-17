package ch.chnoch.thesis.renderer;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import android.content.Context;
import android.opengl.GLSurfaceView;

public class GLViewer extends GLSurfaceView {

	private GLRenderer10 mRenderer;

	private int mWidth, mHeight;

	public GLViewer(Context context) {
		super(context);
	}

	public GLViewer(Context context, RenderContext renderer) {
		super(context);
		mRenderer = (GLRenderer10) renderer;
		mRenderer.setViewer(this);

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
}
