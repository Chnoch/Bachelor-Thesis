package ch.chnoch.thesis.viewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLContext;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.interfaces.*;
import ch.chnoch.thesis.renderer.util.*;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView.EGLContextFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import javax.microedition.khronos.egl.*;

public class GLViewerActivity extends Activity implements OnClickListener {

	private GLViewer mViewer;
	private SceneManagerInterface mSceneManager;
	private RenderContext mRenderer;
	private final String TAG = "GLViewerActivity";

	private Node mRoot, mSmallGroup, mShapeNodeBig, mShapeNodeSmallOne,
			mShapeNodeSmallTwo;

	private Shape mShapeSmall, mShapeBig;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSceneManager = new GraphSceneManager();
		// Shape shape = loadTeapot();

		
		mSceneManager.getCamera().setCenterOfProjection(new Vector3f(0, 10, 20));
		
		createShapes();
		createLights();
		setMaterial();
		
		boolean openGlES20 = detectOpenGLES20();
//		boolean openGlES20 = false;
		
		if (openGlES20) {
			Log.d(TAG, "Using OpenGL ES 2.0");
			// Tell the surface view we want to create an OpenGL ES
			// 2.0-compatible
			// context, and set an OpenGL ES 2.0-compatible renderer.
			mRenderer = new GLES20Renderer();
			Shader shader = createShaders();
			Material material = new Material();
			material.setShader(shader);
			mShapeBig.setMaterial(material);
			mShapeSmall.setMaterial(material);
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

	private Shader createShaders() {
		String vertexShader = readRawText(R.raw.lightvert);
		String fragmentShader = readRawText(R.raw.lightfrag);
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

	
	/*
	 * Private Methods
	 */

	private void createShapes() {
		mShapeBig = Util.loadCube(4);
		mShapeSmall = Util.loadCube(1);
//		mShapeBig = loadStructure(R.raw.cube, 1);
//		mShapeSmall = loadStructure(R.raw.cube, 1);
		// Shape groundShape = Util.loadGround();

		Vector3f transY = new Vector3f(0, 8, 0);

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

		mSmallGroup.addChild(mShapeNodeSmallOne);
		mSmallGroup.addChild(mShapeNodeSmallTwo);
	}

	private void createLights() {

		Light light = new Light(mSceneManager.getCamera());
		light.mType = Light.Type.DIRECTIONAL;
		light.mPosition.set(5, 5, 5);
		light.mDirection.set(1, 1, 1);
		light.mSpecular.set(1, 1, 1);
		light.mAmbient.set(0.4f, 0.4f, 0.4f);
		light.mDiffuse.set(0.3f, 0.3f, 0.3f);

		
		mSceneManager.addLight(light);
	}

	private void setMaterial() {
		Material mat = new Material();

		
		mat.shininess = 5;
		mat.mAmbient.set(1, 0, 0);
		mat.mDiffuse.set(1f, 0, 0);
		mat.mSpecular.set(1f, 0f, 0f);

		mShapeNodeBig.setMaterial(mat);
		mShapeNodeSmallOne.setMaterial(mat);
		mShapeNodeSmallTwo.setMaterial(mat);
	}

	private void enablePhysics() {
		mSceneManager.enablePhysicsEngine();
		mViewer.setOnClickListener(this);
	}

	private Shape loadStructure(int resource, int size) {
		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexBuffers vertexBuffer = null;
		try {
			InputStream teapotSrc = getApplication().getResources()
					.openRawResource(resource);
			vertexBuffer = ObjReader.read(teapotSrc, 1);
			
			if (size != 1) {
				IntBuffer buffer = vertexBuffer.getVertexBuffer();
				buffer.position(0);
				for (int i=0; i< buffer.limit()-1; i++){
					int value = buffer.get();
					buffer.put( value * size);
				}
			}
		} catch (IOException exc) {
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