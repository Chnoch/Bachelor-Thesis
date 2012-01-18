package ch.chnoch.thesis.viewer;

import java.io.IOException;
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
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.util.ObjReader;
import ch.chnoch.thesis.renderer.util.Util;

public class GLViewerActivity extends Activity {

	private static final String TAG = "GLViewerActivity";

	private GLViewer mViewer;
	private SceneManagerInterface mSceneManager;
	private RenderContext mRenderer;

	private TouchHandler mTouchHandler;

	private Node mRoot;
	
	private Shader mShader;

	/*
	 * 
	 * CALLBACK METHODS
	 */

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSceneManager = new GraphSceneManager();

		mSceneManager.getCamera().setCenterOfProjection(new Vector3f(0, 0, 10));
		mSceneManager.getFrustum().setVertFOV(45);


		boolean openGlES20 = detectOpenGLES20();

		if (openGlES20) {
			Log.d(TAG, "Using OpenGL ES 2.0");
			mRenderer = new GLES20Renderer(getApplicationContext());
		} else {
			Log.d(TAG, "Using OpenGL ES 1.1");
			mRenderer = new GLES11Renderer();
		}
		mViewer = new GLViewer(this, mRenderer, openGlES20);
		mRenderer.setSceneManager(mSceneManager);

		createShapes();
		createLights();
		mShader = createShaders(R.raw.phongtexvert, R.raw.phongtexfrag);
		
		mTouchHandler = new TouchHandler(mSceneManager, mRenderer,
 mViewer,
				CameraMode.OBJECT_CENTRIC);
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

//		Material mat = mShapeNodeBig.getMaterial();
		// Handle item selection
		int id = item.getItemId();
		if (id == R.id.aluminium) {
//			mat.setTexture(createTexture(R.raw.aluminium));
		} else if (id == R.id.wall) {
//			mat.setTexture(createTexture(R.raw.wall));
		} else if (id == R.id.cube) {
			addCube();
		} else if (id == R.id.teapot) {
			addTeapot();
		} else if (id == R.id.sphere) {
			addSphere();
		} else if (id == R.id.selectObject) {
			selectObject();
		} else if (id == R.id.changeCameraMode) {
			showCameraOptions();
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
		Shape shape = loadStructure(R.raw.teapot_alt);
		ShapeNode node = new ShapeNode(shape);
		Vector3f ambient = new Vector3f(0.3f,0.3f,0.3f);
		Vector3f diffuse= new Vector3f(0.7f,0.7f,0.7f);
		Vector3f specular= new Vector3f(1,1,1);
		node.setMaterial(createMaterial(ambient, diffuse, specular, 100, R.raw.wall));
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
		Texture tex = mRenderer.makeTexture();
		try {
			tex.createTexture(id);
		} catch (IOException exc) {
			Log.e(TAG, exc.getMessage());
		}
		return tex;
	}

	
	private void createShapes() {
		Vector3f ambient = new Vector3f(0.3f,0.3f,0.3f);
		Vector3f diffuse= new Vector3f(0.7f,0.7f,0.7f);
		Vector3f specular= new Vector3f(1,1,1);
		Material mainMaterial = createMaterial(ambient, diffuse, specular, 100, R.raw.wall);
		Material groundMaterial = createMaterial(ambient, diffuse, specular, 100, R.raw.wood);
		
//		Shape sphere = loadStructure(R.raw.cubetex);
		Shape sphere = Util.loadSphere(50, 50, 1);
//		sphere = loadStructure(R.raw.sphere_prec);
		Shape teapot = loadStructure(R.raw.teapot_alt);
		Shape cube = Util.loadCuboid(3, 1, 2);
		 Shape groundShape = Util.loadCuboid(30, 0.1f, 30);

//		Vector3f transY = new Vector3f(0, 3, 0);
//		Vector3f transLeft = new Vector3f(-2, 0, 0);
//		Vector3f transRight = new Vector3f(2, 0, 0);
//		Vector3f transGround = new Vector3f(-15,-4,-15);
		Vector3f transY = new Vector3f(0, 0, 0);
		Vector3f transLeft = new Vector3f(10, 0, -3);
		Vector3f transRight = new Vector3f(0, 4, -6);
		Vector3f transGround = new Vector3f(-15,-4,-15);

		mRoot = new TransformGroup();
//		mRoot.move(new Vector3f(0,-5,0));
		mSceneManager.setRoot(mRoot);

		Node groundNode = new ShapeNode(groundShape);
		groundNode.setActiveState(false);
		Node sphereNode = new ShapeNode(sphere);
		Node teapotsGroup = new TransformGroup();
		Node teapot1 = new ShapeNode(teapot);
		Node teapot2 = new ShapeNode(cube);
		
		groundNode.move(transGround);
		teapotsGroup.move(transY);
		sphereNode.move(transRight);
		teapot1.move(transLeft);
		teapot2.move(transRight);
		
		groundNode.setMaterial(groundMaterial);
		sphereNode.setMaterial(mainMaterial);
		teapot1.setMaterial(mainMaterial);
		teapot2.setMaterial(mainMaterial);

		teapotsGroup.addChild(teapot1);
		teapotsGroup.addChild(teapot2);
		
		mRoot.addChild(groundNode);
		mRoot.addChild(sphereNode);
		mRoot.addChild(teapotsGroup);
	}

	private void createLights() {
		Light light = new Light(mSceneManager.getCamera());
		light.setType(Light.Type.POINT);

		light.setPosition(new Vector3f(0, 0, 10));
		light.setSpecular(new Vector3f(0, 1, 0));
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
					mTouchHandler.setCameraMode(CameraMode.ORIGIN_CENTRIC);
					break;
				default:
					mTouchHandler.setCameraMode(CameraMode.OBJECT_CENTRIC);
				}
			}
		});
		builder.show();
	}
}