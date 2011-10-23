package ch.chnoch.thesis.renderer;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.util.Util;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchHandler implements OnTouchListener {

	private boolean mOnNode = false;
	private boolean mIsTranslation = false;
	private boolean mRotate = false;
	private boolean mUpScaled = false;

	private float mPreviousX, mPreviousY;
	private long mEventStart, mEventEnd;

	private RenderContext mRenderer;

	private Trackball mTrackball;
	private Plane mPlane;
	private RayShapeIntersection mIntersection;

	private GLViewer mViewer;
	
	private ScaleGestureDetector mScaleDetector;

	private final float TOUCH_SCALE_FACTOR = 1;
	private final float TRANSLATION_SCALE_FACTOR = 1;

	private final String TAG = "TouchHandler";

	public TouchHandler(RenderContext renderer, GLViewer viewer) {
		mRenderer = renderer;
		mViewer = viewer;
		mTrackball = new Trackball();
		mPlane = new Plane();
		mPlane.setNormal(new Vector3f(0, 0, 1));
		
		mScaleDetector = new ScaleGestureDetector(viewer.getContext(), new ScaleListener());
		// runSimulation();
	}

	public boolean onTouch(View view, MotionEvent e) {
		// Let the gesture detector analyze the input first
		mScaleDetector.onTouchEvent(e);
		
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

				Ray ray = mViewer.unproject(x, y);
				Log.d("TouchHandler", "Ray: " + ray.toString());
				
				RayShapeIntersection intersect = mRenderer.getSceneManager()
						.intersectRayNode(ray);
				
				if (intersect.hit) {
					mIntersection = intersect;
					mOnNode = true;
				} else {
					mOnNode = false;
				}
				break;
			} catch (Exception exc) {
//				Log.e(TAG, exc.getMessage());
			}
			mViewer.requestRender();
		
		case MotionEvent.ACTION_MOVE:
			mEventEnd = e.getEventTime();
			
			if (mOnNode && !mScaleDetector.isInProgress()) {
				float distance = (float) Math.sqrt(Math.pow(mPreviousX - x, 2)
						+ Math.pow(mPreviousY - y, 2));
				Log.d("TouchHandler", "Distance: " + distance);
				
				if (mEventEnd - mEventStart > 500 || (mIsTranslation && !mRotate)) {
					Log.d("TouchHandler", "Moving Object");
					
					try {
						// Long Press occured: Manipulate object by moving it
						Ray prevRay = mViewer.unproject(mPreviousX, mPreviousY);
						Ray curRay = mViewer.unproject(x, y);

						// mPlane = findClosestPlane(prevRay);
						RayShapeIntersection startIntersection = mIntersection.node
								.intersect(prevRay);
						mPlane.setPointOnPlane(startIntersection.hitPoint);
						mPlane.setNode(mIntersection.node);

						RayShapeIntersection endIntersection = mPlane
								.intersect(curRay);

						Log.d("TouchHandler", "Moving from "
								+ startIntersection.hitPoint.toString()
								+ " to " + endIntersection.hitPoint.toString());
						
						mPlane.update(endIntersection.hitPoint,
								startIntersection.hitPoint);

						mIsTranslation = true;
						
						if (!mUpScaled) {
							mIntersection.node.setScale(mIntersection.node.getScale() + 0.1f);
							mUpScaled = true;
						}
					} catch (Exception exc) {
//						Log.e(TAG, exc.getMessage());
					}

				} else if (distance>0.1f){
					// Short press: Rotate object
					Log.d("TouchHandler", "Rotating Object");
					try {
						mTrackball.setNode(mIntersection.node);

						Ray startRay = mViewer
								.unproject(mPreviousX, mPreviousY);
						Ray endRay = mViewer.unproject(x, y);

						Log.d("TouchHandler",
								"startRay: " + startRay.toString());
						Log.d("TouchHandler", "endRay: " + endRay.toString());
						RayShapeIntersection startIntersection = mTrackball
								.intersect(startRay);
						RayShapeIntersection endIntersection = mTrackball
								.intersect(endRay);

						mTrackball.update(startIntersection.hitPoint,
								endIntersection.hitPoint, TOUCH_SCALE_FACTOR);
						mRotate = true;
					} catch (Exception exc) {
//						Log.e(TAG, exc.getMessage());
					}

				}
				mPreviousX = x;
				mPreviousY = y;

				mViewer.requestRender();
			}
			mEventStart = e.getEventTime();
			break;
		case MotionEvent.ACTION_UP:
			// reset all flags
			mIsTranslation = false;
			mOnNode = false;
			mRotate = false;
			
			if (mUpScaled) {
				mIntersection.node.setScale(mIntersection.node.getScale() - 0.1f);
				mUpScaled = false;
				mViewer.requestRender();
			}
			// runSimulation();
			break;

		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}

	public void setTrackball(Trackball trackball) {
		mTrackball = trackball;
	}

	/*
	 * Private Methods
	 */
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		float mScaleFactor = 1;
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
			Log.d("TouchHandler", "ScaleFactor: " + mScaleFactor);
			
			mIntersection.node.setScale(mScaleFactor);
			mViewer.requestRender();
			
			return true;
		}

	}


}
