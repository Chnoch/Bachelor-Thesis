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
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchHandler extends AbstractTouchHandler {

	private boolean mIsTranslation;
	private boolean mRotate;
	
		private final String TAG = "TouchHandler";

	public TouchHandler(SceneManagerInterface sceneManager,
			RenderContext renderer, GLSurfaceView viewer) {
		super(sceneManager, renderer, viewer);

		mScaleDetector = new ScaleGestureDetector(viewer.getContext(),
				new ScaleListener());
	}

	public boolean onTouch(View view, MotionEvent e) {
		// Let the gesture detector analyze the input first
		 mScaleDetector.onTouchEvent(e);
		Log.d(TAG, "onTouch");
		if (view instanceof GLViewer) {
			mViewer = (GLViewer) view;
		}

		float x = e.getX();
		float y = e.getY();

		y = view.getHeight() - y;

		switch (e.getAction()) {

		case MotionEvent.ACTION_DOWN:
			try {
				mEventStart = e.getEventTime();
				unproject(x, y);
				break;
			} catch (Exception exc) {
				// Log.e(TAG, exc.getMessage());
			}

		case MotionEvent.ACTION_MOVE:
			mEventEnd = e.getEventTime();

			if (mOnNode && !mScaleDetector.isInProgress()) {
				float distance = (float) Math.sqrt(Math.pow(mPreviousX - x, 2)
						+ Math.pow(mPreviousY - y, 2));

				 if (mEventEnd - mEventStart > 300 || (mIsTranslation && !mRotate)) {
					Log.d("TouchHandler", "Moving Object");
					 translate(x, y);

				} else if (distance > 0.1f) {
					// Short press: Rotate object
						rotate(x, y);
				}

			}
			mEventStart = e.getEventTime();
			break;
		case MotionEvent.ACTION_UP:
			// reset all flags
			mIsTranslation = false;
			mOnNode = false;
			mRotate = false;

			if (mUpScaled) {
				mIntersection.node
						.setScale(mIntersection.node.getScale() - 0.1f);
				mUpScaled = false;
			}
			break;

		}
		
		mViewer.requestRender();
		mPreviousX = x;
		mPreviousY = y;
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

			Log.d("TouchHandler",
					"Moving from " + startIntersection.hitPoint.toString()
							+ " to " + endIntersection.hitPoint.toString());

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

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		protected float mScaleFactor = 1;
		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			if (mIntersection != null) {
				mScaleFactor *= detector.getScaleFactor();
				mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
				// Log.d("TouchHandler", "ScaleFactor: " + mScaleFactor);

				mIntersection.node.setScale(mScaleFactor);
				mViewer.requestRender();
			}
			return true;

		}
	}

}
