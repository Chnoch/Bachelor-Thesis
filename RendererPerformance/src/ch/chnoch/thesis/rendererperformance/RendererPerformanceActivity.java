package ch.chnoch.thesis.rendererperformance;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import ch.chnoch.thesis.renderer.GLES11Renderer;
import ch.chnoch.thesis.renderer.GLViewer;
import ch.chnoch.thesis.renderer.GraphSceneManager;
import ch.chnoch.thesis.renderer.RenderItem;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.ShapeNode;
import ch.chnoch.thesis.renderer.TransformGroup;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.RendererInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerIterator;
import ch.chnoch.thesis.renderer.util.Util;

public class RendererPerformanceActivity extends Activity {

	private GraphSceneManager mSceneManager;
	private Shape mShape;
	private Node mNode, mRoot;
	private RendererInterface mRenderer;
	private GLSurfaceView mViewer;
	private Matrix4f mRotation, mTranslation;
	private AxisAngle4f mAxisAngle;
	private static final int NUM_OF_SHAPES = 100;
	private AnimationTask task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSceneManager = new GraphSceneManager();
		mSceneManager.getCamera().setCenterOfProjection(
				new Vector3f(100, 20, 40));
		mSceneManager.getFrustum().setFarPlane(300);
		mShape = Util.loadCube(10);

		mRoot = new TransformGroup();
		mSceneManager.setRoot(mRoot);

		mRenderer = new GLES11Renderer();
		mRenderer.setSceneManager(mSceneManager);
		mViewer = new GLViewer(this, mRenderer);

		mRotation = Util.getIdentityMatrix();
		mAxisAngle = new AxisAngle4f(new Vector3f(1,1,1), 0.01f);
		mRotation.rotY(0.01f);

		mTranslation = Util.getIdentityMatrix();

		for (int i = 0; i < NUM_OF_SHAPES; i++) {
			mNode = new ShapeNode(mShape);
//			mNode.move(mTranslation);
			mRoot.addChild(mNode);
			mTranslation.m03 += 1;
		}

		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);

		task = new AnimationTask();
		task.start();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
//		task.start();
	}

	public class AnimationTask extends Thread {

		public void run() {
//			for (int i=0;i<100;i++) {
			while (true) {
				long oldTime = System.currentTimeMillis();
				SceneManagerIterator it = mSceneManager.iterator();
				while (it.hasNext()) {
					RenderItem item = it.next();
					Matrix4f t = item.getNode().getRotationMatrix();
					mRotation.set(mAxisAngle);
					t.mul(mRotation);
					item.getNode().setRotationMatrix(t);
				}
				long newTime = System.currentTimeMillis();
				
				long diff = newTime - oldTime;
				Log.d("RendererPerformance", "Calculating Matrices: " + diff + " ms");
				
				mViewer.requestRender();
			}
		}

	}
}
