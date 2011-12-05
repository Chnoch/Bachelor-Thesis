package ch.chnoch.thesis.box2dintegration;

import java.io.InputStream;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.interfaces.*;
import ch.chnoch.thesis.renderer.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Box2DIntegration extends Activity {

	private GraphSceneManager mSceneManager;
	private Shape mShape;
	private Node mRoot;
	private PhysicsGroup mPhysicsNode;
	private RenderContext mRenderer;
	private GLSurfaceView mViewer;

	private boolean mSimulationRunning;

	private static final String TAG = "Box2DIntegration";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createNewTowerSimulation();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.newsimluation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mSimulationRunning = false;
		// Handle item selection
		int id = item.getItemId();
		if (id == R.id.newSimulation) {
			final CharSequence[] items = { "Tower", "Pyramid" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select new simulation");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch (item) {
					case 0:
						createNewTowerSimulation();
						break;
					default:
						createNewPyramidSimulation();

					}
				}
			});
			builder.show();
		}
		return true;
	}

	private void createNewTowerSimulation() {
		init();
		createLights();
		createTowerShapes();
		runSimulation();
	}

	private void createNewPyramidSimulation() {
		init();
		createLights();
		createPyramidShapes();
		runSimulation();
	}

	private void init() {
		mSceneManager = new GraphSceneManager();
		mSceneManager.getCamera().setCenterOfProjection(new Vector3f(0, 0, 30));
		mSceneManager.getFrustum().setVertFOV(90);

		mRenderer = new GLES20Renderer(getApplicationContext());
		// mRenderer = new GLES11Renderer();
		// mRenderer = new GL2DRenderer();
		mRenderer.setSceneManager(mSceneManager);

		mViewer = new GLViewer(this, mRenderer, true);

		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);

		mViewer.setOnTouchListener(new PhysicsTouchHandler(mSceneManager,
				mRenderer, mViewer));
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

	private void createLights() {

		Light light = new Light(mSceneManager.getCamera());
		light.setType(Light.Type.POINT);
		light.setPosition(0, 5, 10);
		light.setSpecular(1, 1, 1);
		light.setAmbient(0.3f, 0.3f, 0.3f);
		light.setDiffuse(0.7f, 0.7f, 0.7f);

		mSceneManager.addLight(light);
	}

	private void createPyramidShapes() {

		PhysicsGroup root = new PhysicsGroup(mSceneManager);
		mPhysicsNode = root;

		root.setMaterial(createMaterial());
		buildPyramid(root);
		addGroundBodyToShapes();
		mSceneManager.setRoot(root);
	}

	private void buildPyramid(PhysicsGroup root) {
		Material mat = createMaterial();

		for (int i = 0; i < 5; i++) {
			for (int j = i-3; j < 3.5f-i; j++) {
				Vector2f position = new Vector2f(4*j, 4*i);
				root.addRectangle(2, 2, 0.4f, position, true, true).setMaterial(mat);
			}
		}
		mPhysicsNode.addCircle(2, new Vector2f(-15, 8)).setMaterial(mat);
		mPhysicsNode.addCircle(2, new Vector2f(15, 8)).setMaterial(mat);
	}

	private void createTowerShapes() {
		PhysicsGroup root = new PhysicsGroup(mSceneManager);
		mPhysicsNode = root;

		// root.addRectangle(1, 0.3f, 1, new Vector2f(0,0), true, true);
		// root.addRectangle(1, 2, 1,new Vector2f(5,2), true, true);
		addGroundBodyToShapes();
		createNewTower();
		mSceneManager.setRoot(root);

	}

	private void createNewTower() {
		Material mat = createMaterial();
		for (int i = -1; i < 2; i++) {
			for (int j = 0; j < 5; j++) {
				mPhysicsNode.addRectangle(1, 1, 1, new Vector2f(3 * i, j * 2),
						true, true).setMaterial(mat);
			}
		}

		ShapeNode node = mPhysicsNode.addCircle(1, new Vector2f(-10, 4));
		node.setShape(loadStructure(R.raw.teapot_alt, 1.5f));
		mPhysicsNode.addCircle(2, new Vector2f(10, 4)).setMaterial(mat);
	}

	private void addGroundBodyToShapes() {

		ShapeNode groundBody = mPhysicsNode.addGroundBody(50, 0.1f, 10,
				new Vector2f(0, -6f));
		groundBody.setActiveState(false);

		ShapeNode rightSideBody = mPhysicsNode.addRectangle(0.1f, 10, 10,
				new Vector2f(20, 0), false, false);
		rightSideBody.setActiveState(false);

		ShapeNode leftSideBody = mPhysicsNode.addRectangle(0.1f, 10, 10,
				new Vector2f(-20, 0), false, false);
		leftSideBody.setActiveState(false);

		Material mat = createMaterial();

		rightSideBody.setMaterial(mat);
		leftSideBody.setMaterial(mat);
		groundBody.setMaterial(mat);
	}

	private Material createMaterial() {
		Material mat = new GLMaterial();

		mat.shininess = 5;
		mat.mAmbient.set(0.3f, 0, 0);
		mat.mDiffuse.set(0.7f, 0, 0);
		mat.mSpecular.set(1f, 0f, 0f);
		mat.setShader(createShaders());
		return mat;
	}

	private Shader createShaders() {
		String vertexShader = Util.readRawText(getApplication(),
				R.raw.phongvert);
		String fragmentShader = Util.readRawText(getApplication(),
				R.raw.phongfrag);
		Shader shader = null;
		Log.d(TAG, "VertexShader: " + vertexShader);
		Log.d(TAG, "FragmentShader: " + fragmentShader);
		try {
			mRenderer.createShader(shader, vertexShader, fragmentShader);
			return shader;
			// if (shader.getProgram() == 0) {
			// throw new RuntimeException();
			// }
		} catch (GLException exc) {
			Log.e(TAG, exc.getError());
		} catch (Exception e) {
			Log.e(TAG, "Error loading Shaders", e);
		}
		return null;
	}
	
	private Shape loadStructure(int resource, float scale) {
		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexBuffers vertexBuffer = null;

		try {
			InputStream source = getApplication().getResources()
					.openRawResource(resource);
			vertexBuffer = ObjReader.read(source, scale);
		} catch (Exception exc) {
			Log.e(TAG, "Error loading Vertex data", exc);
		}

		return new Shape(vertexBuffer);
	}

}