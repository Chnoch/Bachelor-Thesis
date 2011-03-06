package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GLViewer extends GLSurfaceView {
	
	private GLRenderer10 mRenderer;
	private SceneManagerInterface mSceneManager;
	
	private float mPreviousX;
    private float mPreviousY;
	
	private final float TOUCH_SCALE_FACTOR = 0.01f;
    private final float TRACKBALL_SCALE_FACTOR = 1;
	
	public GLViewer(Context context, SceneManagerInterface sceneManager) {
		super(context);
		this.mSceneManager = sceneManager;
	}
	
	public GLViewer(Context context, RenderContext renderer, SceneManagerInterface sceneManager) {
		super(context);
		mSceneManager = sceneManager;
		mRenderer = (GLRenderer10) renderer;
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
    @Override public boolean onTrackballEvent(MotionEvent e) {
//        mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
//        mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
        
        float angleX = e.getX() * TRACKBALL_SCALE_FACTOR;
        float angleY = e.getY() * TRACKBALL_SCALE_FACTOR;
        
        rotateCamera(angleX, angleY);
        
//        Vector3f centerOfProj = camera.getCenterOfProjection();
//        centerOfProj.x += e.getX() * TRACKBALL_SCALE_FACTOR;
//        centerOfProj.y += e.getY() * TRACKBALL_SCALE_FACTOR;
//        camera.update();
        requestRender();
        return true;
    }

    @Override public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
//        Camera camera = mSceneManager.getCamera();
//        Vector3f centerOfProj = camera.getCenterOfProjection();
        
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dx = x - mPreviousX;
            float dy = y - mPreviousY;
            
//            mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
//            mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
            
            rotateCamera(dx* TOUCH_SCALE_FACTOR, dy*TOUCH_SCALE_FACTOR);
            
//            centerOfProj.x += dx * TOUCH_SCALE_FACTOR;
//            centerOfProj.y += dy * TOUCH_SCALE_FACTOR;
//            camera.update();
            requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
    
    private void rotateCamera(float angleX, float angleY) {
    	Camera camera = mSceneManager.getCamera();
    	Matrix4f rotation = new Matrix4f();
    	rotation.rotZ(angleX);
    	
        camera.getCameraMatrix().mul(rotation);
        
        rotation.rotX(-angleY);
        
        camera.getCameraMatrix().mul(rotation);
    }
}
