package ch.chnoch.thesis.box2dintegration;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import ch.chnoch.thesis.renderer.AbstractTouchHandler.CameraMode;
import ch.chnoch.thesis.renderer.GLES20Renderer;
import ch.chnoch.thesis.renderer.GLException;
import ch.chnoch.thesis.renderer.GLMaterial;
import ch.chnoch.thesis.renderer.GLViewer;
import ch.chnoch.thesis.renderer.GraphSceneManager;
import ch.chnoch.thesis.renderer.Light;
import ch.chnoch.thesis.renderer.Material;
import ch.chnoch.thesis.renderer.PhysicsGroup;
import ch.chnoch.thesis.renderer.PhysicsTouchHandler;
import ch.chnoch.thesis.renderer.ShapeNode;
import ch.chnoch.thesis.renderer.interfaces.RendererInterface;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.util.Util;

public class PoolGame extends Activity {

	private static final String TAG = "PoolGame";

	private GraphSceneManager mSceneManager;
	private PhysicsGroup mPhysicsNode;
	private RendererInterface mRenderer;
	private GLViewer mViewer;
	private Shader mShader;

	private boolean mSimulationRunning;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createGame();
	}

	@Override
	public void onPause() {
		super.onPause();
		mViewer.onPause();
		mSimulationRunning = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		mViewer.onResume();
	}

	private void createGame() {
		init();
		createLights();
		createTable();
		createBalls();
		runSimulation();
	}

	private void runSimulation() {
		// mSimulation = new Simulation();
		// mSimulation.start();
		mSimulationRunning = true;
		new Thread(new Runnable() {
			public void run() {
				while (mSimulationRunning) {
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

	private void init() {
		mSceneManager = new GraphSceneManager();
		mSceneManager.getCamera()
.setCenterOfProjection(
				new Vector3f(-10, -15, -15));
		mSceneManager.getFrustum().setVertFOV(60);

		mRenderer = new GLES20Renderer(getApplicationContext());
		mRenderer.setSceneManager(mSceneManager);

		mViewer = new GLViewer(this, mRenderer);

		mViewer.setOnTouchListener(new PhysicsTouchHandler(mSceneManager,
				mRenderer, mViewer, CameraMode.ORIGIN_CENTRIC));
		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);

		mShader = createShaders();
	}

	private void createLights() {
		Light light = new Light();
		light.setType(Light.Type.POINT);
		light.setPosition(0, 0, -5);
		light.setSpecular(1, 1, 1);
		light.setDiffuse(0.7f, 0.7f, 0.7f);
		light.setAmbient(0.3f, 0.3f, 0.3f);

		mSceneManager.addLight(light);
	}

	private void createTable() {
		PhysicsGroup root = new PhysicsGroup(mSceneManager, new Vector2f(0, 0));
		mPhysicsNode = root;

		// ShapeNode groundBody = mPhysicsNode.addGroundBody(10, 5, 0.005f,
		// new Vector2f(0, 0));
		// groundBody.setActiveState(false);
		ShapeNode groundBody = mPhysicsNode.addGroundBody(0.0001f, 0.0001f,
				0.005f,
				new Vector2f(-20, -20));
		groundBody.setActiveState(false);

		ShapeNode ground = new ShapeNode(Util.loadCuboid(10, 5, 0.005f));
		ground.setActiveState(false);
		ground.move(new Vector3f(0, 0, 0.255f));
		mPhysicsNode.addChild(ground);
		ShapeNode headTable1 = mPhysicsNode.addRectangle(0.1f, 4, 0.5f,
				new Vector2f(10, 0), false, false);
		headTable1.setActiveState(false);
		ShapeNode headTable2 = mPhysicsNode.addRectangle(0.1f, 4, 0.5f,
				new Vector2f(-10, 0), false, false);
		headTable2.setActiveState(false);

		ShapeNode headTable3 = mPhysicsNode.addRectangle(4, 0.1f, 0.5f,
				new Vector2f(-5, 5), false, false);
		headTable3.setActiveState(false);
		ShapeNode headTable4 = mPhysicsNode.addRectangle(4, 0.1f, 0.5f,
				new Vector2f(5, 5), false, false);
		headTable4.setActiveState(false);
		ShapeNode headTable5 = mPhysicsNode.addRectangle(4, 0.1f, 0.5f,
				new Vector2f(5, -5), false, false);
		headTable5.setActiveState(false);
		ShapeNode headTable6 = mPhysicsNode.addRectangle(4, 0.1f, 0.5f,
				new Vector2f(-5, -5), false, false);
		headTable6.setActiveState(false);

		Material mat = createMaterial(0, 1, 0, 40);

		headTable1.setMaterial(mat);
		headTable2.setMaterial(mat);
		headTable3.setMaterial(mat);
		headTable4.setMaterial(mat);
		headTable5.setMaterial(mat);
		headTable6.setMaterial(mat);
		ground.setMaterial(mat);
		// groundBody.setMaterial(mat);

		mSceneManager.setRoot(root);
	}

	private void createBalls() {
		// for (int i = 0; i < 15; i++) {
		// mPhysicsNode.addCircle(0.5f, new Vector2f(i, 0));
		// }

		for (int i = 0; i < 5; i++) {
			for (int j = i - 3; j < 3.5f - i; j++) {
				Vector2f position = new Vector2f(i - 4, j);
				mPhysicsNode.addCircle(0.5f, position).setMaterial(
						createMaterial(1, 0, 0, 40));
			}
		}

		ShapeNode mainBall = mPhysicsNode.addCircle(0.5f, new Vector2f(4, 0));

		mainBall.setMaterial(createMaterial(0.8f, 0.8f, 0.8f, 40));
	}

	private Shader createShaders() {
		String vertexShader = Util.readRawText(getApplication(),
				R.raw.phongtexvert);
		String fragmentShader = Util.readRawText(getApplication(),
				R.raw.phongtexfrag);
		Shader shader = null;
		Log.d(TAG, "VertexShader: " + vertexShader);
		Log.d(TAG, "FragmentShader: " + fragmentShader);
		try {
			mRenderer.createShader(shader, vertexShader, fragmentShader);
			return shader;
		} catch (GLException exc) {
			Log.e(TAG, exc.getError());
		} catch (Exception e) {
			Log.e(TAG, "Error loading Shaders", e);
		}
		return null;
	}

	private Material createMaterial(float r, float g, float b, float shininess) {
		Material mat = new GLMaterial();

		mat.shininess = shininess;
		mat.mAmbient.set(0.3f * r, 0.3f * g, 0.3f * b);
		mat.mDiffuse.set(0.7f * r, 0.7f * g, 0.7f * b);
		mat.mSpecular.set(r, g, b);
		mat.setShader(mShader);
		return mat;
	}

}
