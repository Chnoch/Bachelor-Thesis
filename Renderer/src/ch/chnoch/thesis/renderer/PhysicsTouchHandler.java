package ch.chnoch.thesis.renderer;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

/**
 * The PhysicsTouchHandler is used to interact with application that have
 * support for a physics engine built in. Because of this it needs to move the
 * objects differently than if you just interact with a 3D scene. Instead of
 * simply moving a shape to its destination point it creates a physical joint on
 * the destination that attracts the shape, so that it gains physical momentum
 * and keeps moving even if you remove your fingers.
 */
public class PhysicsTouchHandler extends AbstractTouchHandler {

	private static final String TAG = "PhysicsTouchHandler";

	/**
	 * Instantiates a new physics touch handler. Sets the 2D mode of the plane
	 * to true so that this plane knows that it only needs to account for 2
	 * dimensions.
	 * 
	 * @param sceneManager
	 *            the scene manager representing your 3D scene
	 * @param renderer
	 *            the renderer that is used for drawing content on the screen.
	 * @param viewer
	 *            the viewer that represents the view
	 * @param cameraMode
	 *            the camera mode that is set
	 */
	public PhysicsTouchHandler(SceneManagerInterface sceneManager,
			RenderContext renderer, GLViewer viewer, CameraMode cameraMode) {
		super(sceneManager, renderer, viewer, cameraMode);
		mPlane.set2DMode(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.AbstractTouchHandler#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
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
			findNode();

			break;
		case MotionEvent.ACTION_MOVE:
			mEventEnd = e.getEventTime();
			if (mMultitouch) {
				// Log.d(TAG, "Multitouch");
				multitouchMove(e, x, y);
			} else {
				if (!mOnNode) {
					unproject(x, y);
					findNode();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.AbstractTouchHandler#makeRotation(android.view
	 * .MotionEvent, float, float)
	 */
	protected void makeRotation(MotionEvent e, float x, float y) {
		rotateCamera(e);
		moveCamera(x, y);
	}

	/**
	 * Moves the node accordingly. This always compares the two last
	 * {@link MotionEvent} that are received from the Android framework.
	 * 
	 * @param x
	 *            the current x-coordinate
	 * @param y
	 *            the current y-coordinate
	 */
	private void moveNode(float x, float y) {
		Ray curRay = mViewer.unproject(x, y);
		Ray prevRay = mViewer.unproject(mPreviousX, mPreviousY);

		RayShapeIntersection startIntersection = mPlane.intersect(prevRay);
		RayShapeIntersection endIntersection = mPlane.intersect(curRay);

		mPlane.update(endIntersection.hitPoint, startIntersection.hitPoint);
	}

	/**
	 * Sets the node that is stored in the intersection to the plane so that it
	 * can be used for interaction.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private void findNode() {
		if (mOnNode) {
			mPlane.setPointOnPlane(mIntersection.hitPoint);
			mPlane.setNode(mIntersection.node);
		}
	}

	/**
	 * Ends a translation and destroys all the joints that are still stored in
	 * the physics world.
	 */
	private void endTranslation() {
		mSceneManager.destroyJoints();
	}

}
