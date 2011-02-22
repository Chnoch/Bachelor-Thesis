package ch.chnoch.thesis.renderer;

import java.io.IOException;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class GLViewer extends GLSurfaceView {
	
	public GLViewer(Context context, GLRenderer renderer) {
		super(context);
		setEGLContextClientVersion(2);
		
		setRenderer(renderer);
	}
	
}
