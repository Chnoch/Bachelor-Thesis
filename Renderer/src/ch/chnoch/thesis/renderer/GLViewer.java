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

	private final float TOUCH_SCALE_FACTOR = 0.2f;
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
			// float dx = (x - mPreviousX) * TOUCH_SCALE_FACTOR;
			// float dy = (y - mPreviousY) * TOUCH_SCALE_FACTOR;
			RayBoxIntersection prevIntersect = Util.unproject(mPreviousX,
					mPreviousY, mRenderer);
			if (prevIntersect.hit) {
				mTrackball.setNode(prevIntersect.node);
				RayBoxIntersection curIntersect = Util.unprojectOnTrackball(x, y, mTrackball, mRenderer);
				Vector3f prev = prevIntersect.hitPoint;
				Vector3f cur = curIntersect.hitPoint;
				mTrackball.update(cur, prev, TOUCH_SCALE_FACTOR);
				// mTrackball.simpleUpdate(x, y, mPreviousX, mPreviousY,
				// TOUCH_SCALE_FACTOR);
				// mRenderer.pick(x,y);
				requestRender();
				mPreviousX = x;
				mPreviousY = y;
			}
		case MotionEvent.ACTION_DOWN:
			RayBoxIntersection intersect = Util.unproject(x,
					y, mRenderer);
			if (intersect.hit) {
				Log.d("ACTION_DOWN", "Coordinates: " + x + ", " + y);
				Log.d("ACTION_DOWN", "Box with BB low: " + intersect.node.getBoundingBox().getLow()+ " and high: " + intersect.node.getBoundingBox().getHigh());
			} else {
				Log.d("ACTION_DOWN", "Nothing hit");
			}
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
