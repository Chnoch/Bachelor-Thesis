package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

// TODO: Auto-generated Javadoc
/**
 * The Class KeyHandler.
 */
public class KeyHandler implements OnKeyListener {
	
	/** The m renderer. */
	private RenderContext mRenderer;
	
	/** The viewer. */
	private GLViewer viewer;
	
	/**
	 * Instantiates a new key handler.
	 *
	 * @param renderer the renderer
	 */
	public KeyHandler(RenderContext renderer) {
		mRenderer = renderer;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	public boolean onKey(View view, int keyCode, KeyEvent event) {
		if (view instanceof GLViewer) {
			viewer = (GLViewer) view;
		}
		
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
        viewer.requestRender();
        return true;
	}

}
