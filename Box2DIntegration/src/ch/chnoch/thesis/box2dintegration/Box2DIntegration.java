package ch.chnoch.thesis.box2dintegration;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.box2d.Box2DBody.TType;
import ch.chnoch.thesis.renderer.interfaces.*;
import ch.chnoch.thesis.renderer.util.*;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class Box2DIntegration extends Activity implements OnClickListener {

	private GraphSceneManager mSceneManager;
	private Shape mShape;
	private Node mBullet, mRoot;
	private RenderContext mRenderer;
	private GLSurfaceView mViewer;

	private Simulation mSimulation;

	private static final String TAG = "Box2DIntegration";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSceneManager = new GraphSceneManager();
		mSceneManager.getCamera().setCenterOfProjection(new Vector3f(0, 0, 20));
		mSceneManager.getFrustum().setVertFOV(90);

		// mRenderer = new GLES20Renderer(getApplicationContext());
		mRenderer = new GLES11Renderer();
		// mRenderer = new GL2DRenderer();
		mRenderer.setSceneManager(mSceneManager);

		mViewer = new GLViewer(this, mRenderer, false);

		createLights();
		createShapes();

		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);

		mSceneManager.enablePhysicsEngine();
		// mViewer.setOnClickListener(this);

		mViewer.setOnTouchListener(new TouchHandler(mSceneManager, mRenderer,
				mViewer, true));

		runSimulation();

	}

	@Override
	public void onPause() {
		super.onPause();
		mViewer.onPause();
		// mSimulation.stopThread();
	}

	@Override
	public void onResume() {
		super.onResume();
		mViewer.onResume();
		// mSimulation.resumeThread();
	}

	private void runSimulation() {
		// mSimulation = new Simulation();
		// mSimulation.start();
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					for (int i = 0; i < 1; i++) {
						// Log.d("Simulation", "Updating scene");
						mSceneManager.updateScene();
						mViewer.requestRender();
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}

	private void createLights() {

		Light light = new Light(mSceneManager.getCamera());
		light.setType(Light.Type.POINT);
		light.setPosition(0, 5, 10);
		light.setDirection(1, 1, 1);
		light.setSpecular(1, 1, 1);
		light.setAmbient(0.4f, 0.4f, 0.4f);
		light.setDiffuse(0.3f, 0.3f, 0.3f);

		mSceneManager.addLight(light);
	}

	private void createShapes() {

		Node root = new TransformGroup();
		mRoot = root;
		// Node root = new ShapeNode(Util.loadCube(1));
		// mBullet = new ShapeNode(Util.loadCube(1));
		// Matrix4f trans = new Matrix4f();
		// trans.setTranslation(new Vector3f(13, 5, 0));
		// mBullet.setTranslationMatrix(trans);
		// root.addChild(mBullet);

		root.setMaterial(createMaterial());
		buildHalfPyramid(root, 1);
		buildHalfPyramid(root, -1);
		addGroundBodyToShapes();
		mSceneManager.setRoot(root);
	}

	private void addGroundBodyToShapes() {

		ShapeNode groundBody = new ShapeNode(Util.loadCuboid(50, 1, 10));
		groundBody.move(new Vector3f(0, -5.5f, 0));
		groundBody.setActiveState(false);
		
		Shape side = Util.loadCuboid(0.1f, 10, 10);
		ShapeNode rightSideBody = new ShapeNode(side);
		rightSideBody.move(new Vector3f(15, 5f,0));
		rightSideBody.setActiveState(false);
		ShapeNode leftSideBody = new ShapeNode(side);
		leftSideBody.move(new Vector3f(-15,5f,0));
		leftSideBody.setActiveState(false);
		
		
		Material mat = new GLMaterial();
		mat.shininess = 5;
		mat.mAmbient.set(0, 1, 0);
		mat.mDiffuse.set(0, 1, 0);
		mat.mSpecular.set(0, 1, 0);
		
		rightSideBody.setMaterial(mat);
		leftSideBody.setMaterial(mat);
		groundBody.setMaterial(mat);
		
		mRoot.addChild(rightSideBody);
		mRoot.addChild(leftSideBody);
		
		mRoot.addChild(groundBody);

	}

	private Material createMaterial() {
		Material mat = new GLMaterial();

		mat.shininess = 5;
		mat.mAmbient.set(1, 0, 0);
		mat.mDiffuse.set(1f, 0, 0);
		mat.mSpecular.set(1f, 0f, 0f);
		// mat.setShader(createShaders());
		return mat;
	}

	// private Shader createShaders() {
	// String vertexShader = Util.readRawText(getApplication(),
	// R.raw.phongvert);
	// String fragmentShader = Util.readRawText(getApplication(),
	// R.raw.phongfrag);
	// Shader shader = null;
	// Log.d(TAG, "VertexShader: " + vertexShader);
	// Log.d(TAG, "FragmentShader: " + fragmentShader);
	// try {
	// mRenderer.createShader(shader, vertexShader, fragmentShader);
	// return shader;
	// // if (shader.getProgram() == 0) {
	// // throw new RuntimeException();
	// // }
	// } catch (GLException exc) {
	// Log.e(TAG, exc.getError());
	// } catch (Exception e) {
	// Log.e(TAG, "Error loading Shaders", e);
	// }
	// return null;
	// }

	private void buildHalfPyramid(Node root, int mirror) {
		Shape shape = Util.loadCube(1);
		Material mat = createMaterial();

		for (int i = 0; i < 5; i++) {
			Node newRow = new TransformGroup();
			// Matrix4f rowTranslation = Util.getIdentityMatrix();
			// rowTranslation.setTranslation();
			// newRow.setTranslationMatrix(rowTranslation);
			newRow.move(new Vector3f(0, i * 4f, 0));

			for (int j = 0 + ((mirror + 1) / 2); j < 5 - i; j++) {
				Node cube = new ShapeNode(shape);
				// Matrix4f columnTranslation = Util.getIdentityMatrix();
				// columnTranslation.setTranslation();
				// cube.setTranslationMatrix(columnTranslation);
				cube.move(new Vector3f((-mirror) * j * 2.1f, 0, 0));
				cube.setMaterial(mat);

				newRow.addChild(cube);
			}
			root.addChild(newRow);
		}
	}

	private class Simulation extends Thread {
		private boolean isRunning = true;

		public void run() {
			while (isRunning) {
				Log.d("Simulation", "Updating scene");
				mSceneManager.updateScene();
				mViewer.requestRender();
			}
		}

		public void stopThread() {
			isRunning = false;
		}

		public void resumeThread() {
			isRunning = true;
			run();
		}
	}

	public void onClick(View arg0) {
		Log.d("Box2dIntegration", "onClick");
		// new Thread(new Simulation()).run();
		mBullet.getPhysicsProperties().setLinearVelocity(-200, 0);
	}
}