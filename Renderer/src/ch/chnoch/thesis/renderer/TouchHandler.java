package ch.chnoch.thesis.renderer;

import java.util.List;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.util.Util;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchHandler implements OnTouchListener {

	private boolean mOnNode = false;
	private boolean mIsTranslation = false;
	private boolean mRotate = false;

	private float mPreviousX, mPreviousY;
	private long mEventStart, mEventEnd;

	private RenderContext mRenderer;

	private Trackball mTrackball;
	private Plane mPlane;
	private RayShapeIntersection mIntersection;

	private GLViewer mViewer;

	private final float TOUCH_SCALE_FACTOR = 1;
	private final float TRANSLATION_SCALE_FACTOR = 1;

	public TouchHandler(RenderContext renderer, GLViewer viewer) {
		mRenderer = renderer;
		mViewer = viewer;
		mTrackball = new Trackball();
		mPlane = new Plane();
		mPlane.setNormal(new Vector3f(0, 0, 1));
//		runSimulation();
	}

	public boolean onTouch(View view, MotionEvent e) {
		if (view instanceof GLViewer) {
			mViewer = (GLViewer) view;
		}

		float x = e.getX();
		float y = e.getY();

		y = view.getHeight() - y;

		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			mEventEnd = e.getEventTime();

			if (mOnNode) {
				float distance = (float)Math.sqrt(Math.pow(mPreviousX - x,2) + Math.pow(mPreviousY - y,2));
				Log.d("TouchHandler", "Distance: " + distance);
				if (mEventEnd - mEventStart > 500
						|| (mIsTranslation && !mRotate)|| distance < 0.8f ) {
					Log.d("TouchHandler", "Moving Object");
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
							+ startIntersection.hitPoint.toString() + " to "
							+ endIntersection.hitPoint.toString());
					mPlane.update(endIntersection.hitPoint,
							startIntersection.hitPoint);

					mIsTranslation = true;

				} else {
					// Short press: Rotate object
					Log.d("TouchHandler", "Rotating Object");

					mTrackball.setNode(mIntersection.node);

					Ray startRay = mViewer.unproject(mPreviousX, mPreviousY);
					Ray endRay = mViewer.unproject(x, y);
					
					Log.d("TouchHandler", "startRay: " + startRay.toString());
					Log.d("TouchHandler", "endRay: " + endRay.toString());
					RayShapeIntersection startIntersection = mTrackball
							.intersect(startRay);
					RayShapeIntersection endIntersection = mTrackball
							.intersect(endRay);
					
					mTrackball.update(startIntersection.hitPoint,
							endIntersection.hitPoint, TOUCH_SCALE_FACTOR);
					mRotate = true;

				}
				mPreviousX = x;
				mPreviousY = y;

				mViewer.requestRender();
			}
			mEventStart = e.getEventTime();
			break;
		case MotionEvent.ACTION_DOWN:

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
		case MotionEvent.ACTION_UP:
			// reset all flags
			mIsTranslation = false;
			mOnNode = false;
			mRotate = false;
//			runSimulation();
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

	private Plane findClosestPlane(Ray ray) {
		List<Plane> planes = mIntersection.node.getBoundingBox().getPlanes();
		Plane closestPlane = null;
		RayShapeIntersection tempInter = new RayShapeIntersection();
		Vector3f tempVec = new Vector3f();
		// Initialize the closest Vector at Infinity
		float tempClosestDist = Float.MAX_VALUE;
		for (Plane plane : planes) {
			tempInter = plane.intersect(ray);
			if (tempInter.hit) {
				tempVec.sub(tempInter.hitPoint, ray.getOrigin());
				if (tempVec.length() < tempClosestDist) {
					tempClosestDist = tempVec.length();
					closestPlane = plane;
				}
			}
		}

		return closestPlane;
	}
	
	private void runSimulation() {
		Log.d("TouchHandler", "Simulation started");
		new Thread(new Simulation()).run();
	}

	private class Simulation implements Runnable {
		public void run() {
			for (int i = 0; i < 1000; i++) {
				mRenderer.getSceneManager().updateScene();
				mViewer.requestRender();
			}
		}
	}

}
