package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class AbstractTouchHandler implements OnTouchListener {

	protected boolean mOnNode = false;
	protected boolean mUpScaled = false;

	protected float mPreviousX, mPreviousY;
	protected float mEventStart, mEventEnd;

	protected RenderContext mRenderer;
	protected SceneManagerInterface mSceneManager;

	protected Trackball mTrackball;
	protected Plane mPlane;
	protected RayShapeIntersection mIntersection;

	protected GLViewer mViewer;

	protected ScaleGestureDetector mScaleDetector;

	protected final float TOUCH_SCALE_FACTOR = 1;

	public AbstractTouchHandler(SceneManagerInterface sceneManager,
			RenderContext renderer, GLSurfaceView viewer) {
		mSceneManager = sceneManager;
		mRenderer = renderer;
		mTrackball = new Trackball();
		mPlane = new Plane();
		mPlane.setNormal(new Vector3f(0, 0, 1));
		
		mViewer = (GLViewer)viewer;
		
	}

	@Override
	public abstract boolean onTouch(View view, MotionEvent e);
	
	protected void unproject(float x, float y) {
		Ray ray = mViewer.unproject(x, y);

		RayShapeIntersection intersect = mRenderer.getSceneManager()
				.intersectRayNode(ray);

		if (intersect.hit) {
			mIntersection = intersect;
			mOnNode = true;
		} else {
			mOnNode = false;
		}
	}
	
}
