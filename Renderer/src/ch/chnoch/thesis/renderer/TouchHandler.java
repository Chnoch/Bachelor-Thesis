package ch.chnoch.thesis.renderer;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.util.Util;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchHandler extends AbstractTouchHandler {

	private boolean mIsTranslation;
	private boolean mRotate;

	private static final float SCALE_THRESHOLD = 0.1f;

	private static final String TAG = "TouchHandler";

	public TouchHandler(SceneManagerInterface sceneManager,
			RenderContext renderer, GLViewer viewer) {
		super(sceneManager, renderer, viewer);

	}

	public boolean onTouch(View view, MotionEvent e) {
		int action = e.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;

		float x = e.getX();
		float y = e.getY();

		y = view.getHeight() - y;

		switch (actionCode) {

		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "ACTION_DOWN");
			try {
				mEventStart = e.getEventTime();
				unproject(x, y);
				break;
			} catch (Exception exc) {
				break;
			}
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.d(TAG, "ACTION_POINTER_DOWN");
			actionPointerDown(e);
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "ACTION_MOVE");
			mEventEnd = e.getEventTime();
			if (mMultitouch) {
				Log.d(TAG, "Multitouch");
				multitouchMove(e, x, y);
			} else if (mOnNode) {
				Log.d(TAG, "Singletouch");
				float distance = (float) Math.sqrt(Math.pow(mPreviousX - x, 2)
						+ Math.pow(mPreviousY - y, 2));

				if (mEventEnd - mEventStart > 300
						|| (mIsTranslation && !mRotate)) {
					Log.d("TouchHandler", "Moving Object");
					translate(x, y);

				} else if (distance > 0.1f) {
					// Short press: Rotate object
					rotate(x, y);
				}
			} else {
				Log.d(TAG, "No movement");
			}
			mEventStart = e.getEventTime();
			break;
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "ACTION_UP");
			// reset all flags
			mIsTranslation = false;
			mRotate = false;
			actionUp();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.d(TAG, "ACTION_POINTER_UP");
			actionPointerUp();
			break;
		}

		finalizeOnTouch(x, y);
		return true;
	}

	private void translate(float x, float y) {
		try {
			// Long Press occured: Manipulate object by moving it
			Ray prevRay = mViewer.unproject(mPreviousX, mPreviousY);
			Ray curRay = mViewer.unproject(x, y);

			// mPlane = findClosestPlane(prevRay);
			if (mIntersection.node != mPlane.getNode()) {
				RayShapeIntersection hitPointInter = mIntersection.node
						.intersect(prevRay);

				mPlane.setPointOnPlane(hitPointInter.hitPoint);
				mPlane.setNode(mIntersection.node);
			}

			RayShapeIntersection startIntersection = mPlane.intersect(prevRay);
			RayShapeIntersection endIntersection = mPlane.intersect(curRay);

			mPlane.update(endIntersection.hitPoint, startIntersection.hitPoint);

			mIsTranslation = true;

			if (!mUpScaled) {
				mIntersection.node
						.setScale(mIntersection.node.getScale() + 0.1f);
				mUpScaled = true;
			}
		} catch (Exception exc) {
			// Log.e(TAG, exc.getMessage());
		}
	}

	private void rotate(float x, float y) {
		mTrackball.setNode(mIntersection.node);

		Ray startRay = mViewer.unproject(mPreviousX, mPreviousY);
		Ray endRay = mViewer.unproject(x, y);

		RayShapeIntersection startIntersection = mTrackball.intersect(startRay);
		RayShapeIntersection endIntersection = mTrackball.intersect(endRay);

		mTrackball.update(startIntersection.hitPoint, endIntersection.hitPoint,
				TOUCH_SCALE_FACTOR);
		mRotate = true;
	}
	
	@Override
	protected void rotateWorld(float x, float y) {
		mWorldTrackball.setNode(mSceneManager.getRoot(), mSceneManager.getCamera(), true);
		
		Ray startRay = mViewer.unproject(mPreviousX, mPreviousY);
		Ray endRay = mViewer.unproject(x, y);

		Log.d(TAG, "Previous x: " + mPreviousX + " y: " + mPreviousY);

		RayShapeIntersection startIntersection = mWorldTrackball.intersect(startRay);
		RayShapeIntersection endIntersection = mWorldTrackball.intersect(endRay);

		Log.d(TAG, "startIntersection: " + startIntersection.toString());
		Log.d(TAG, "endIntersection: " + endIntersection.toString());

		mUpdateLocation = mWorldTrackball.update(startIntersection.hitPoint,
				endIntersection.hitPoint, WORLD_ROTATE_FACTOR);
	}
}
