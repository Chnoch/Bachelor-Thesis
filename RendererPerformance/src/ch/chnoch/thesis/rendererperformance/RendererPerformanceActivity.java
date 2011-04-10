package ch.chnoch.thesis.rendererperformance;

import android.app.Activity;
import android.os.Bundle;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.util.*;
import android.opengl.GLSurfaceView;

public class RendererPerformanceActivity extends Activity {

	private GraphSceneManager mSceneManager;
	private Shape mShape;
	private Node mNode, mRoot;
	private RenderContext mRenderer;
	private GLSurfaceView mViewer;
	private Matrix4f mRotation, mTranslation;
	private static final int NUM_OF_SHAPES = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSceneManager = new GraphSceneManager();
		mSceneManager.getCamera().setCenterOfProjection(new Vector3f(100,20,40));
		mSceneManager.getFrustum().setFarPlane(300);
		mShape = Util.loadCube(1);

		mRoot = new TransformGroup();
		mRoot.setTransformationMatrix(Util.getIdentityMatrix());
		mSceneManager.setRoot(mRoot);

		mRenderer = new GLRenderer10(getApplication());
		mRenderer.setSceneManager(mSceneManager);
		mViewer = new GLViewer(this, mRenderer);

		mRotation = Util.getIdentityMatrix();
		mRotation.rotY(0.01f);
		
		mTranslation = Util.getIdentityMatrix();

		for (int i = 0; i < NUM_OF_SHAPES; i++) {
			mNode = new ShapeNode();
			mNode.setShape(mShape);
			mNode.setTransformationMatrix(new Matrix4f(mTranslation));
			mRoot.addChild(mNode);
			mTranslation.m03 += 1;
		}

		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);

		AnimationTask task = new AnimationTask();
		task.start();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public class AnimationTask extends Thread {

		public void run() {
			while (true) {
				SceneManagerIterator it = mSceneManager.iterator();
				while (it.hasNext()) {
					RenderItem item = it.next();
					item.getNode().getTransformationMatrix().mul(mRotation);
				}
				mViewer.requestRender();
			}
		}

	}
}
