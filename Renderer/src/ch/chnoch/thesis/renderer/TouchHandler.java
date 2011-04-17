package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
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
	private RayShapeIntersection mIntersection;

	private GLViewer mViewer;

	private final float TOUCH_SCALE_FACTOR = 1;
	private final float TRANSLATION_SCALE_FACTOR = 0.05f;

	public TouchHandler(RenderContext renderer, GLViewer viewer) {
		mRenderer = renderer;
		mViewer = viewer;
		mTrackball = new Trackball();
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
//				if (mEventEnd - mEventStart > 500 || (mIsTranslation && !mRotate)) {
				if (false) {
					Log.d("TouchHandler", "Moving Object");
					// Long Press occured: Manipulate object by moving it
					Ray prevRay = Util.unproject(mPreviousX, mPreviousY, mRenderer);
					Ray curRay = Util.unproject(x, y, mRenderer);
					RayShapeIntersection prevInter = mIntersection.node
							.getBoundingBox().hitPoint(prevRay);
					RayShapeIntersection curInter = mIntersection.node
					.getBoundingBox().hitPoint(curRay);
					prevInter.node = mIntersection.node;
					curInter.node = mIntersection.node;
					
					
					float dx = (curInter.hitPoint.x - prevInter.hitPoint.x);
					float dy = (curInter.hitPoint.y - prevInter.hitPoint.y);
					
					// translation vector. 3rd dimension??
					Vector3f trans = new Vector3f(dx, dy, 0);
					
					Matrix4f transMatrix = Util.getIdentityMatrix();
					transMatrix.setTranslation(trans);
					if (prevInter.hit) {
						Matrix4f t = prevInter.node.getTranslationMatrix();
						t.mul(transMatrix);
						prevInter.node.setTranslationMatrix(t);
					}
					mIsTranslation = true;

				} else {
					// Short press: Rotate object
					Log.d("TouchHandler", "Rotating Object");

					mTrackball.setNode(mIntersection.node);

					Ray startRay = Util.unproject(mPreviousX, mPreviousY,
							mRenderer);
					Ray endRay = Util.unproject(x, y, mRenderer);
//					Log.d("TouchHandler", "startRay: Origin: " + startRay.getOrigin().toString() + " Direction: " + startRay.getDirection().toString());
//					Log.d("TouchHandler", "endRay: Origin: " + endRay.getOrigin().toString() + " Direction: " + endRay.getDirection().toString());
					RayShapeIntersection startIntersection = mTrackball
							.intersect(startRay);
					RayShapeIntersection endIntersection = mTrackball
							.intersect(endRay);
					Log.d("TouchHandler", "startIntersection: Hit: " + startIntersection.hit + " Hitpoint: " + startIntersection.hitPoint.toString());
					Log.d("TouchHandler", "endIntersection: Hit: " + endIntersection.hit + " Hitpoint: " + endIntersection.hitPoint.toString());
					
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

			RayShapeIntersection intersect = Util.intersectRayBox(
					Util.unproject(x, y, mRenderer),
					mRenderer.getSceneManager());
			if (intersect.hit) {
				mIntersection = intersect;
				mOnNode = true;
			} else {
				mOnNode = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			//reset all flags
			mIsTranslation = false;
			mOnNode = false;
			mRotate = false;
			break;
		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}

	public void setTrackball(Trackball trackball) {
		mTrackball = trackball;
	}

}
