package ch.chnoch.thesis.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GLViewer extends GLSurfaceView {
	
	public GLViewer(Context context) {
		super(context);
	}
	
	public GLViewer(Context context, GLRenderer renderer) {
		super(context);
		setRenderer(renderer);
	}
	
}
