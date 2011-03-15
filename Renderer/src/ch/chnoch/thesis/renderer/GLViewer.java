package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GLViewer extends GLSurfaceView {
	
	private GLRenderer10 mRenderer;
	
	private float mPreviousX;
    private float mPreviousY;
    
    private final float TOUCH_SCALE_FACTOR = 0.01f;
    private final float TRACKBALL_SCALE_FACTOR = 1;
    
    private Trackball mTrackball;
	
	public GLViewer(Context context) {
		super(context);
	}
	
	public GLViewer(Context context, RenderContext renderer, Trackball trackball) {
		super(context);
		mRenderer = (GLRenderer10) renderer;
		mRenderer.setViewer(this);
		
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		mTrackball = trackball;
	}
	
	public void setTrackball(Trackball trackball) {
		mTrackball = trackball;
	}
	
	/*
	 * Leave Trackball-Control for now...
	@Override
	public boolean onTrackballEvent(MotionEvent e) {

		float angleX = e.getX() * TRACKBALL_SCALE_FACTOR;
		float angleY = e.getY() * TRACKBALL_SCALE_FACTOR;

		mTrackball.simpleUpdate(angleX, angleY);

		requestRender();
		return true;
	}
	*/

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dx = (x - mPreviousX) * TOUCH_SCALE_FACTOR;
			float dy = (y - mPreviousY) * TOUCH_SCALE_FACTOR;

			mTrackball.simpleUpdate(x, y, mPreviousX, mPreviousY, TOUCH_SCALE_FACTOR);
//			mRenderer.pick(x,y);
			requestRender();
		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}
	
	private void pick_point(float x, float y) {
		SceneManagerInterface sceneManager = mRenderer.getSceneManager();
		Camera camera = sceneManager.getCamera();
		Frustum frustum = sceneManager.getFrustum();
		
		Matrix4f t = camera.getCameraMatrix();
	}
	
	
 
	public void surfaceHasChanged(int width, int height) {
		mTrackball.setSize(width, height);
	}
}
