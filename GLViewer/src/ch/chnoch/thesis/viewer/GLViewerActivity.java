package ch.chnoch.thesis.viewer;

import java.io.InputStream;

import javax.vecmath.Vector3f;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import ch.chnoch.thesis.renderer.AbstractTouchHandler.CameraMode;
import ch.chnoch.thesis.renderer.GLES11Renderer;
import ch.chnoch.thesis.renderer.GLES20Renderer;
import ch.chnoch.thesis.renderer.GLException;
import ch.chnoch.thesis.renderer.GLMaterial;
import ch.chnoch.thesis.renderer.GLViewer;
import ch.chnoch.thesis.renderer.GraphSceneManager;
import ch.chnoch.thesis.renderer.Light;
import ch.chnoch.thesis.renderer.Material;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.ShapeNode;
import ch.chnoch.thesis.renderer.TouchHandler;
import ch.chnoch.thesis.renderer.TransformGroup;
import ch.chnoch.thesis.renderer.VertexBuffers;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.RendererInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.util.ObjReader;
import ch.chnoch.thesis.renderer.util.Util;

public class GLViewerActivity extends Activity {

	private static final String TAG = "GLViewerActivity";

	private GLViewer mViewer;
	private SceneManagerInterface mSceneManager;
	private RendererInterface mRenderer;

	private TouchHandler mTouchHandler;

	private Node mRoot;
	
	private Shader mShader;
	
	private Material cubeMaterial;
	
	private Shape teapot;

	/*
	 * 
	 * CALLBACK METHODS
	 */

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSceneManager = new GraphSceneManager();

		changeSceneManagerValues();

		boolean openGlES20 = detectOpenGLES20();
		if (openGlES20) {
			Log.d(TAG, "Using OpenGL ES 2.0");
			mRenderer = new GLES20Renderer(getApplicationContext());
		} else {
			Log.d(TAG, "Using OpenGL ES 1.1");
			mRenderer = new GLES11Renderer();
		}
		mViewer = new GLViewer(this, mRenderer);
		mRenderer.setSceneManager(mSceneManager);

		createShapes();
		createLights();
		mShader = createShaders(R.raw.phongtexvert, R.raw.phongtexfrag);
		
		mTouchHandler = new TouchHandler(mSceneManager, mRenderer,
 mViewer,
				CameraMode.CAMERA_CENTRIC);
		mViewer.setOnTouchListener(mTouchHandler);

		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		mViewer.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mViewer.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.texture_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle item selection
		int id = item.getItemId();
		if (id == R.id.cube) {
			addCube();
		} else if (id == R.id.aluminium) {
			cubeMaterial.setTexture(createTexture(R.raw.aluminium));
		} else if (id == R.id.wall) {
			cubeMaterial.setTexture(createTexture(R.raw.wall));
		} else if (id == R.id.teapot) {
			addTeapot();
		} else if (id == R.id.sphere) {
			addSphere();
			// } else if (id == R.id.selectObject) {
			// selectObject();
			// } else if (id == R.id.changeCameraMode) {
			// showCameraOptions();
		} else if (id == R.id.reset) {
			resetCamera();
		} else {
			return super.onOptionsItemSelected(item);
		}
//		mat.setTextureChanged(true);
		mViewer.requestRender();
		return true;
	}


	/*
	 * 
	 * 
	 * Private Instantiation Methods
	 */
	private void changeSceneManagerValues() {
		mSceneManager.getCamera().setCenterOfProjection(new Vector3f(0, 0, 25));
		mSceneManager.getFrustum().setVertFOV(45);
		mSceneManager.getFrustum().setFarPlane(500);
		mSceneManager.getFrustum().setNearPlane(1f);
	}

	private void addCube() {
		Shape shape = Util.loadCuboid(1, 1, 1);
		ShapeNode node = new ShapeNode(shape);
		Vector3f ambient = new Vector3f(0.3f,0.3f,0.3f);
		Vector3f diffuse= new Vector3f(0.7f,0.7f,0.7f);
		Vector3f specular= new Vector3f(1,1,1);
		
		node.setMaterial(createMaterial(ambient, diffuse, specular, 100, R.raw.wall));
		mRoot.addChild(node);
	}
	
	

	private void addTeapot() {
		ShapeNode node = new ShapeNode(teapot);
		Vector3f ambient = new Vector3f(0.3f,0.3f,0.3f);
		Vector3f diffuse= new Vector3f(0.7f,0.7f,0.7f);
		Vector3f specular= new Vector3f(1,1,1);
		node.setMaterial(createMaterial(ambient, diffuse, specular, 100,
				R.raw.teapot_texture));
		mRoot.addChild(node);
	}

	private void addSphere() {
		Vector3f ambient = new Vector3f(0.3f,0.3f,0.3f);
		Vector3f diffuse= new Vector3f(0.7f,0.7f,0.7f);
		Vector3f specular= new Vector3f(1,1,1);
		
		Shape shape = Util.loadSphere(25, 25, 1);
		ShapeNode node = new ShapeNode(shape);

		node.setMaterial(createMaterial(ambient, diffuse, specular, 100, R.raw.wall));
		mRoot.addChild(node);
	}

	private Shader createShaders(int vertexShaderRef, int fragmentShaderRef) {
		String vertexShader = Util.readRawText(getApplication(), vertexShaderRef);
		String fragmentShader = Util.readRawText(getApplication(), fragmentShaderRef);
		Shader shader = null;
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

	private Texture createTexture(int id) {
		Texture tex = null;
		try {
			tex = mRenderer.createTexture();
			tex.createTexture(id);
		} catch (Exception exc) {
			Log.e(TAG, exc.getMessage());
		}
		return tex;
	}
	
	private void createShapes() {
		// Vector3f ambient = new Vector3f(0.3f,0.3f,0.3f);
		// Vector3f diffuse= new Vector3f(0.7f,0.7f,0.7f);
		// Vector3f specular= new Vector3f(1,1,1);
		// Material mainMaterial = createMaterial(1, 0, 0, 100);
		Material sphereMaterial = createMaterial(1, 1, 1, 120, R.raw.earth);
		Material teapotMaterial = createMaterial(1, 1, 1, 120,
				R.raw.teapot_texture);
		cubeMaterial = createMaterial(1, 1, 1, 120, R.raw.cube_texture);
		Material groundMaterial = createMaterial(1, 1, 1, 100, R.raw.ground);
		
		Shape sphere = Util.loadSphere(20, 20, 1);
		teapot = loadStructure(R.raw.teapot_alt);
		Shape cube = Util.loadCuboid(3, 1, 2);
		Shape groundShape = Util.loadCuboid(200, 0.1f, 200);

		Vector3f transSphere = new Vector3f(5, 9, 4);
		Vector3f transCube = new Vector3f(0, 4, -6);
		Vector3f transTeapot = new Vector3f(0, 0, 0);
		Vector3f transLeft = new Vector3f(-5, 0, 0);
		Vector3f transRight = new Vector3f(5, 0, 0);
		Vector3f transGround = new Vector3f(-100, -20, -100);


		mRoot = new TransformGroup();
		mSceneManager.setRoot(mRoot);

		Node groundNode = new ShapeNode(groundShape);
		groundNode.setActiveState(false);
		Node teapotsGroup = new TransformGroup();
		Node sphereGroup = new TransformGroup();
		Node cubeGroup = new TransformGroup();

		Node cubeNode = new ShapeNode(cube);
		Node cube2Node = new ShapeNode(cube);
		Node sphereNode = new ShapeNode(sphere);
		Node sphere2Node = new ShapeNode(sphere);
		Node teapotNode = new ShapeNode(teapot);
		Node teapot2Node = new ShapeNode(teapot);

		groundNode.move(transGround);

		teapotsGroup.move(transTeapot);
		sphereGroup.move(transSphere);
		cubeGroup.move(transCube);

		sphereNode.move(transLeft);
		teapotNode.move(transLeft);
		cubeNode.move(transLeft);
		sphere2Node.move(transRight);
		teapot2Node.move(transRight);
		cube2Node.move(transRight);
		
		groundNode.setMaterial(groundMaterial);

		sphereNode.setMaterial(sphereMaterial);
		teapotNode.setMaterial(teapotMaterial);
		cubeNode.setMaterial(cubeMaterial);

		sphere2Node.setMaterial(sphereMaterial);
		teapot2Node.setMaterial(teapotMaterial);
		cube2Node.setMaterial(cubeMaterial);

		teapotsGroup.addChild(teapotNode);
		teapotsGroup.addChild(teapot2Node);
		cubeGroup.addChild(cubeNode);
		cubeGroup.addChild(cube2Node);
		sphereGroup.addChild(sphereNode);
		sphereGroup.addChild(sphere2Node);
		
		mRoot.addChild(groundNode);
		mRoot.addChild(cubeGroup);
		mRoot.addChild(sphereGroup);
		mRoot.addChild(teapotsGroup);
	}

	private void createLights() {
		Light light = new Light();
		light.setType(Light.Type.POINT);

		light.setPosition(new Vector3f(0, 5, 15));
		light.setSpecular(new Vector3f(1, 1, 1));
		light.setDiffuse(new Vector3f(.5f, .5f, 0.5f));
		light.setAmbient(new Vector3f(.2f, .2f, 0.2f));

		mSceneManager.addLight(light);
	}

	private Material createMaterial(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shininess, int texture) {
		Material mat = new GLMaterial();

		mat.shininess = shininess;
		mat.mAmbient = ambient;
		mat.mDiffuse = diffuse;
		mat.mSpecular = specular;
		mat.setShader(mShader);
		mat.setTexture(createTexture(texture));
		
		return mat;
	}

	private Material createMaterial(float r, float g, float b, float shininess,
			int texture) {
		Material mat = new GLMaterial();

		mat.shininess = shininess;
		mat.mAmbient.set(0.3f * r, 0.3f * g, 0.3f * b);
		mat.mDiffuse.set(0.7f * r, 0.7f * g, 0.7f * b);
		mat.mSpecular.set(r, g, b);
		mat.setShader(mShader);
		mat.setTexture(createTexture(texture));
		return mat;
	}

	private Shape loadStructure(int resource) {
		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexBuffers vertexBuffer = null;

		try {
			InputStream source = getApplication().getResources()
					.openRawResource(resource);
			vertexBuffer = ObjReader.read(source, 1);
		} catch (Exception exc) {
			Log.e(TAG, "Error loading Vertex data", exc);
		}

		return new Shape(vertexBuffer);
	}

	private boolean detectOpenGLES20() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		return (info.reqGlEsVersion >= 0x20000);
	}

	private void selectObject() {
		mTouchHandler.selectObjectForCameraMovement();
		Toast.makeText(getApplication(), "Choose object to focus camera on", 2)
				.show();
	}

	private void showCameraOptions() {
		final CharSequence[] items = { "Camera Centric", "Origin Centric",
				"Object Centric" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select camera option");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					mTouchHandler.setCameraMode(CameraMode.CAMERA_CENTRIC);
					break;
				case 1:
					mSceneManager.getCamera().setLookAtPoint(
							new Vector3f(0, 0, 0));
					mTouchHandler.setCameraMode(CameraMode.ORIGIN_CENTRIC);
					mViewer.requestRender();
					break;
				default:
					mTouchHandler.setCameraMode(CameraMode.OBJECT_CENTRIC);
				}
			}
		});
		builder.show();
	}

	private void resetCamera() {
		mSceneManager.getCamera().reset();
	}
}