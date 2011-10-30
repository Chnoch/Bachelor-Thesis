package ch.chnoch.thesis.basicglactivity;

import ch.chnoch.thesis.renderer.BasicRenderer;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;


public class BasicGLActivityActivity extends Activity {

	private GLSurfaceView mGLView;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGLView = new GLSurfaceView(this);
		mGLView.setEGLConfigChooser(false);
		mGLView.setRenderer(new BasicRenderer());
		mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setContentView(mGLView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}
}