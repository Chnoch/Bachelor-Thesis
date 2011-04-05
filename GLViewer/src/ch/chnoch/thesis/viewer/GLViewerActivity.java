package ch.chnoch.thesis.viewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.util.*;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

public class GLViewerActivity extends Activity {

	private GLSurfaceView mViewer;
	private GraphSceneManager mSceneManager;
	private RenderContext mRenderer;
	private final String TAG = "GLViewerActivity";
	
	private Node mRoot, mSmallGroup, mShapeNodeBig, mShapeNodeSmallOne, mShapeNodeSmallTwo;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSceneManager = new GraphSceneManager();
//		Shape shape = loadTeapot();
		Shape shapeBig = Util.loadCube(4);
		Shape shapeSmall = Util.loadCube(1);
		Vector3f transY = new Vector3f(0,5,0);
		Vector3f transLeft = new Vector3f(-2,0,0);
		Vector3f transRight = new Vector3f(2,0,0);
		
		Matrix4f smallTrans = Util.getIdentityMatrix();
		smallTrans.setTranslation(transY);
		Matrix4f leftTrans = Util.getIdentityMatrix();
		leftTrans.setTranslation(transLeft);
		Matrix4f rightTrans = Util.getIdentityMatrix();
		rightTrans.setTranslation(transRight);
		
		mRoot = new TransformGroup();
		mRoot.setTransformationMatrix(Util.getIdentityMatrix());
		mSceneManager.setRoot(mRoot);
		
		mShapeNodeBig = new ShapeNode();
		mShapeNodeBig.setShape(shapeBig);
		mShapeNodeBig.setTransformationMatrix(Util.getIdentityMatrix());
		mRoot.addChild(mShapeNodeBig);
		
		mSmallGroup = new TransformGroup();
		mSmallGroup.setTransformationMatrix(smallTrans);
		mRoot.addChild(mSmallGroup);
		
		mShapeNodeSmallOne = new ShapeNode();
		mShapeNodeSmallOne.setTransformationMatrix(leftTrans);
		mShapeNodeSmallTwo = new ShapeNode();
		mShapeNodeSmallTwo.setTransformationMatrix(rightTrans);
		
		mShapeNodeSmallOne.setShape(shapeSmall);
		mShapeNodeSmallTwo.setShape(shapeSmall);
		
		mSmallGroup.addChild(mShapeNodeSmallOne);
		mSmallGroup.addChild(mShapeNodeSmallTwo);
		
		
		boolean openGlES20 = detectOpenGLES20(); 
//		if (false) {
			// Tell the surface view we want to create an OpenGL ES
			// 2.0-compatible
			// context, and set an OpenGL ES 2.0-compatible renderer.

//			mRenderer = new GLRenderer(getApplication());

//			Shader shader = createShaders();
			// exit if the shaders couldn't be loaded
//			if (shader.getProgram() == 0)
//				return;
//
//			Material material = new Material();
//			material.setShader(shader);

//			shape.setMaterial(material);
//		} else {
			mRenderer = new GLRenderer10(getApplication());
//		}

		mViewer = new GLViewer(this, mRenderer);
		// Set the OpenGL Context to version 2.0
		// Has to be done after the Viewer is initialized
//		if (openGlES20) {
//			mViewer.setEGLContextClientVersion(2);
//		}
		mRenderer.setSceneManager(mSceneManager);

		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);
	}

	private boolean detectOpenGLES20() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		return (info.reqGlEsVersion >= 0x20000);
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

	/*private Shader createShaders() {
		String vertexShader = readRawText(R.raw.simplevert);
		String fragmentShader = readRawText(R.raw.simplefrag);

		Shader shader = mRenderer.makeShader();
		int program = 0;
		try {
			program = shader.load(vertexShader, fragmentShader);
			if (program == 0) {
				throw new RuntimeException();
			}
		} catch (Exception e) {
			Log.e(TAG, "Error loading Shaders", e);
		}
		return shader;
	}*/

	private Shape loadTeapot() {
		// Construct a data structure that stores the vertices, their
		// attributes, and the triangle mesh connectivity
		VertexBuffers vertexBuffer = null;
		try {
			InputStream teapotSrc = getApplication().getResources()
					.openRawResource(R.raw.teapot);
			vertexBuffer = ObjReader.read(teapotSrc, 1);
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
}