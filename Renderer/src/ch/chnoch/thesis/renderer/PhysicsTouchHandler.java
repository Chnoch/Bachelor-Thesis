package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class PhysicsTouchHandler extends AbstractTouchHandler {

	private static float SCALE_FACTOR = 1f;

	private static final String TAG = "PhysicsTouchHandler";

	public PhysicsTouchHandler(SceneManagerInterface sceneManager,
			RenderContext renderer, GLSurfaceView viewer) {
		super(sceneManager, renderer, viewer);
		mScaleDetector = new ScaleGestureDetector(viewer.getContext(),
				new ScaleListener());
	}

	@Override
	public boolean onTouch(View view, MotionEvent e) {
		// Let the gesture detector analyze the input first
		mScaleDetector.onTouchEvent(e);
		Log.d(TAG, "onTouch");

		float x = e.getX();
		float y = e.getY();
		y = view.getHeight() - y;
		

		if (!mScaleDetector.isInProgress()) {
			switch (e.getAction()) {

			case MotionEvent.ACTION_DOWN:
				mEventStart = e.getEventTime();
				unproject(x, y);
				findNode(x, y);

				break;
			case MotionEvent.ACTION_MOVE:
				mEventEnd = e.getEventTime();
				if (!mOnNode) {
					unproject(x, y);
					findNode(x, y);
				}

				if (mOnNode) {
					moveNode(x, y);
				}
				mEventStart = e.getEventTime();
				break;
			case MotionEvent.ACTION_UP:
				// reset all flags
				mOnNode = false;

				endTranslation();
				break;

			}
			mViewer.requestRender();
			mPreviousX = x;
			mPreviousY = y;
		}
		return true;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param mode
	 *            1: start, 2: end, 0: common
	 */
	private void moveNode(float x, float y) {
		Ray curRay = mViewer.unproject(x, y);
		Ray prevRay = mViewer.unproject(mPreviousX, mPreviousY);

		RayShapeIntersection startIntersection = mPlane.intersect(prevRay);
		RayShapeIntersection endIntersection = mPlane.intersect(curRay);

		mPlane.update(endIntersection.hitPoint, startIntersection.hitPoint);
	}

	private void findNode(float x, float y) {
		if (mOnNode) {
			mPlane.setPointOnPlane(mIntersection.hitPoint);
			mPlane.setNode(mIntersection.node);
		}
	}

	private void endTranslation() {
		mSceneManager.destroyJoints();
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		protected float mScaleFactor = 1;

		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			mScaleFactor = detector.getScaleFactor() * SCALE_FACTOR;
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
			Camera camera = mSceneManager.getCamera();
			Vector3f centerOfProjection = camera.getCenterOfProjection();

			centerOfProjection.z *= 1f/mScaleFactor;
			camera.setCenterOfProjection(centerOfProjection);
			mViewer.requestRender();
			return true;

		}
	}

}
