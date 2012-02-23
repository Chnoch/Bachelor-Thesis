package ch.chnoch.thesis.renderer;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

public class PhysicsTouchHandler extends AbstractTouchHandler {

	private static final String TAG = "PhysicsTouchHandler";


	public PhysicsTouchHandler(SceneManagerInterface sceneManager,
			RenderContext renderer, GLViewer viewer, CameraMode cameraMode) {
		super(sceneManager, renderer, viewer, cameraMode);
		mPlane.set2DMode(true);
	}

	@Override
	public boolean onTouch(View view, MotionEvent e) {

		int action = e.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;

		float x = e.getX();
		float y = e.getY();
		y = view.getHeight() - y;

		switch (actionCode) {

		case MotionEvent.ACTION_DOWN:
			mEventStart = e.getEventTime();
			unproject(x, y);
			findNode(x, y);

			break;
		case MotionEvent.ACTION_MOVE:
			mEventEnd = e.getEventTime();
			if (mMultitouch) {
				// Log.d(TAG, "Multitouch");
				multitouchMove(e, x, y);
			} else {
				if (!mOnNode) {
					unproject(x, y);
					findNode(x, y);
				}

				if (mOnNode) {
					moveNode(x, y);
				}
			}
			mEventStart = e.getEventTime();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.d(TAG, "ACTION_POINTER_DOWN");
			actionPointerDown(e);
			endTranslation();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.d(TAG, "ACTION_POINTER_UP");
			actionPointerUp();
		case MotionEvent.ACTION_UP:
			// reset all flags
			actionUp();
			endTranslation();
			break;

		}
		
		finalizeOnTouch(x,y);
		return true;
	}

	protected void makeRotation(MotionEvent e, float x, float y) {
		rotateCamera(e);
		moveCamera(x, y);
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

}
