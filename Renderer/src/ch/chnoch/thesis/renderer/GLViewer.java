package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import ch.chnoch.thesis.renderer.util.Util;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class GLViewer extends GLSurfaceView {

	private GLRenderer10 mRenderer;

	private float mPreviousX;
	private float mPreviousY;

	private int mWidth, mHeight;

	private final float TOUCH_SCALE_FACTOR = 1;
	private final float TRACKBALL_SCALE_FACTOR = 1;

	private Trackball mTrackball;
	
	private boolean mOnNode = false;
	private Node mPointerOnNode;

	public GLViewer(Context context) {
		super(context);
	}

	public GLViewer(Context context, RenderContext renderer) {
		super(context);
		mRenderer = (GLRenderer10) renderer;
		mRenderer.setViewer(this);

		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		mTrackball = new Trackball();
	}

	public void setTrackball(Trackball trackball) {
		mTrackball = trackball;
	}

	/*
	 * Leave Trackball-Control for now...
	 * 
	 * @Override public boolean onTrackballEvent(MotionEvent e) {
	 * 
	 * float angleX = e.getX() * TRACKBALL_SCALE_FACTOR; float angleY = e.getY()
	 * * TRACKBALL_SCALE_FACTOR;
	 * 
	 * mTrackball.simpleUpdate(angleX, angleY);
	 * 
	 * requestRender(); return true; }
	 */

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			Log.d("GLViewer", "Action_Move");
			if (mOnNode) {
				mTrackball.setNode(mPointerOnNode);
				Ray startRay = Util.unproject(mPreviousX, mPreviousY, mRenderer);
				Ray endRay = Util.unproject(x, y, mRenderer);
				RayShapeIntersection startIntersection = mTrackball.intersect(startRay);
				startIntersection.hitPoint.y = mHeight - startIntersection.hitPoint.y; 
				RayShapeIntersection endIntersection = mTrackball.intersect(endRay);
				endIntersection.hitPoint.y = mHeight - endIntersection.hitPoint.y; 
				mTrackball.update(startIntersection.hitPoint, endIntersection.hitPoint, TOUCH_SCALE_FACTOR);
				requestRender();
				mPreviousX = x;
				mPreviousY = y;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			Log.d("GLViewer", "Action_Down");
			RayShapeIntersection intersect = Util.intersectRayBox(Util.unproject(x,y, mRenderer), mRenderer.getSceneManager());
			if (intersect.hit) {
				Log.d("ACTION_DOWN", "Coordinates: " + x + ", " + y);
				Log.d("ACTION_DOWN", "Box with BB low: " + intersect.node.getBoundingBox().getLow()+ " and high: " + intersect.node.getBoundingBox().getHigh());
				mOnNode = true;
				mPointerOnNode = intersect.node;
			} else {
				Log.d("ACTION_DOWN", "Nothing hit");
				mOnNode = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.d("GLViewer", "Action_Up");
			mOnNode = false;
			break;
		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("Keydown", "chars: " + event.getCharacters());
		Vector3f center = mRenderer.getSceneManager().getCamera().getCenterOfProjection();
        if (keyCode == KeyEvent.KEYCODE_W) {
        	center.z -= 1;
        } else if (keyCode == KeyEvent.KEYCODE_S) {
        	center.z += 1;
        } else if (keyCode == KeyEvent.KEYCODE_A) {
        	center.x -= 1;
        } else if (keyCode == KeyEvent.KEYCODE_D) {
        	center.x += 1;
        }
        mRenderer.getSceneManager().getCamera().update();
        requestRender();
        return super.onKeyDown(keyCode, event);
    }


	public void surfaceHasChanged(int width, int height) {
		mWidth = width;
		mHeight = height;
	}
}
