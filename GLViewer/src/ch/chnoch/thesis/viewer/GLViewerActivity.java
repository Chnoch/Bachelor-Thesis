package ch.chnoch.thesis.viewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.interfaces.*;
import ch.chnoch.thesis.renderer.util.*;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;

public class GLViewerActivity extends Activity implements OnClickListener {

	private GLViewer mViewer;
	private SceneManagerInterface mSceneManager;
	private RenderContext mRenderer;
	private final String TAG = "GLViewerActivity";

	private Node mRoot, mSmallGroup, mShapeNodeBig, mShapeNodeSmallOne,
			mShapeNodeSmallTwo;

	private Shape mShapeSmall, mShapeBig;
	
	/*
	 * 
	 * CALLBACK METHODS
	 * 
	 */

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSceneManager = new GraphSceneManager();
		
		mSceneManager.getCamera().setCenterOfProjection(new Vector3f(0, 0, 10));

		createShapes();
		createLights();
		setMaterial();

		
		boolean openGlES20 = detectOpenGLES20();
//		 boolean openGlES20 = false;

		if (openGlES20) {
			Log.d(TAG, "Using OpenGL ES 2.0");
			// Tell the surface view we want to create an OpenGL ES
			// 2.0-compatible
			// context, and set an OpenGL ES 2.0-compatible renderer.
			mRenderer = new GLES20Renderer(getApplicationContext());
			Shader shader = createShaders();
			Texture texture = createTexture(R.raw.wall);
//			mShapeNodeBig.getMaterial().setTexture(texture);
			mShapeNodeBig.getMaterial().setShader(shader);
			
//			mShapeNodeSmallOne.setMaterial(material);
			
			
		} else {
			Log.d(TAG, "Using OpenGL ES 1.1");
			mRenderer = new GLES11Renderer();
		}
		
		mViewer = new GLViewer(this, mRenderer, openGlES20);
		mRenderer.setSceneManager(mSceneManager);

		TouchHandler touchHandler = new TouchHandler(mRenderer, mViewer);
		mViewer.setOnTouchListener(touchHandler);
		KeyHandler keyHandler = new KeyHandler(mRenderer);
		mViewer.setOnKeyListener(keyHandler);
		
		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);

		// enablePhysics();
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
	public void onBackPressed() {
		Log.d(TAG, "OnBackPressed");
		this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.texture_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Material mat = mShapeNodeBig.getMaterial();
		// Handle item selection
	    switch (item.getItemId()) {
	    case R.id.aluminium:
	        mat.setTexture(createTexture(R.raw.aluminium));
	        break;
//	    case R.id.wood:
//	    	mat.setTexture(createTexture(R.raw.wood));
//	    	break;
	    case R.id.wall:
	    	mat.setTexture(createTexture(R.raw.wall));
	    	break;
	    case R.id.cube:
	    	addCube();
	    	break;
	    case R.id.teapot:
	    	addTeapot();
	    	break;
	    case R.id.sphere:
	    	addSphere();
	    	break;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	    mat.setTextureChanged(true);
	    mViewer.requestRender();
	    return true;
	}
	
	
	/*
	 * 
	 * 
	 * Private Instantiation Methods
	 * 
	 * 
	 */
	private void addCube() {
		Shape shape = loadStructure(R.raw.cubetex);
		ShapeNode node = new ShapeNode(shape);

		node.setMaterial(mShapeNodeBig.getMaterial());
		mRoot.addChild(node);
	}
	
	private void addTeapot() {
		Shape shape = loadStructure(R.raw.teapot_alt);
		ShapeNode node = new ShapeNode(shape);

		node.setMaterial(mShapeNodeBig.getMaterial());
		mRoot.addChild(node);
	}
	
	private void addSphere() {
		
		Shape shape = Util.loadSphere(20, 20, 1);
		ShapeNode node = new ShapeNode(shape);
		
		node.setMaterial(mShapeNodeBig.getMaterial());
		mRoot.addChild(node);
	}
	
	
	private Shader createShaders() {
		String vertexShader = readRawText(R.raw.pointlightvert);
		String fragmentShader = readRawText(R.raw.pointlightfrag);
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
//		mShapeBig = Util.loadCube(4);
//		mShapeSmall = Util.loadCube(1);
		mShapeBig = Util.loadSphere(10,10,1);
		mShapeSmall = loadStructure(R.raw.teapot);
		// Shape groundShape = Util.loadGround();

		Vector3f transY = new Vector3f(0, 5, 0);
		
		
		Vector3f transLeft = new Vector3f(-2, 0, 0);
		Vector3f transRight = new Vector3f(2, 0, 0);

		Matrix4f smallTrans = Util.getIdentityMatrix();
		smallTrans.setTranslation(transY);
		Matrix4f leftTrans = Util.getIdentityMatrix();
		leftTrans.setTranslation(transLeft);
		Matrix4f rightTrans = Util.getIdentityMatrix();
		rightTrans.setTranslation(transRight);
		
		mRoot = new TransformGroup();
		mSceneManager.setRoot(mRoot);

		// mRoot.addChild(new ShapeNode(groundShape));

		mShapeNodeBig = new ShapeNode(mShapeBig);
		mRoot.addChild(mShapeNodeBig);
		
		mSmallGroup = new TransformGroup();
		mSmallGroup.initTranslationMatrix(smallTrans);
		mRoot.addChild(mSmallGroup);

		mShapeNodeSmallOne = new ShapeNode(mShapeSmall);
		mShapeNodeSmallOne.initTranslationMatrix(leftTrans);
		mShapeNodeSmallTwo = new ShapeNode(mShapeSmall);
		mShapeNodeSmallTwo.initTranslationMatrix(rightTrans);
//
//		mSmallGroup.addChild(mShapeNodeSmallOne);
//		mSmallGroup.addChild(mShapeNodeSmallTwo);
	}
	
	private void createLights() {
		Light light = new Light(mSceneManager.getCamera());
		light.setType(Light.Type.POINT);
		
		light.setDirection(new Vector3f(0,0, -1));
		light.setPosition(new Vector3f(0,0,0));
		light.setSpecular(new Vector3f(1,1,1));
		light.setAmbient(new Vector3f(0.5f,0.5f,0.5f));
		light.setDiffuse(new Vector3f(0.5f,0.5f,0.5f));
		
		mSceneManager.addLight(light);
	}
	
	
	private void setMaterial() {
		Material mat = new GLMaterial();

		mat.shininess = 120;
		mat.mAmbient.set(0,0,0.5f);
		mat.mDiffuse.set(0,0,0.5f);
		mat.mSpecular.set(1,1,1);

		mShapeNodeBig.setMaterial(mat);
		mShapeNodeSmallOne.setMaterial(mat);
		mShapeNodeSmallTwo.setMaterial(mat);
	}
	
	private void enablePhysics() {
		mSceneManager.enablePhysicsEngine();
		mViewer.setOnClickListener(this);
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
	
	private String readRawText(int id) {
		InputStream raw = getApplication().getResources().openRawResource(id);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = raw.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = raw.read();
			}
			raw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}

	private boolean detectOpenGLES20() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		return (info.reqGlEsVersion >= 0x20000);
	}

	/*
	 * Used for the physics Simulation
	 */
	private class Simulation implements Runnable {
		public void run() {
			for (int i = 0; i < 100; i++) {
				mSceneManager.updateScene();
				mViewer.requestRender();
			}
		}
	}
	

	public void onClick(View v) {
		Log.d("Box2dIntegration", "onClick");
		new Thread(new Simulation()).run();
	}
}