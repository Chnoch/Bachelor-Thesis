package ch.chnoch.thesis.viewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
	private SimpleSceneManager mSceneManager;
	private RenderContext mRenderer;
	private final String TAG = "GLViewerActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSceneManager = new SimpleSceneManager();
//		Shape shape = loadTeapot();
		Shape shape = loadCube();
		Shape shape2 = loadCube();
		Vector3f trans = new Vector3f(2,2,2);
		shape2.getTransformation().setTranslation(trans);
		mSceneManager.addShape(shape);
		mSceneManager.addShape(shape2);
		
		Trackball trackball = new Trackball(shape);
//		trackball.setSize(2, 2);

		boolean openGlES20 = detectOpenGLES20(); 
		if (openGlES20) {
			// Tell the surface view we want to create an OpenGL ES
			// 2.0-compatible
			// context, and set an OpenGL ES 2.0-compatible renderer.

			mRenderer = new GLRenderer(getApplication());

			Shader shader = createShaders();
			// exit if the shaders couldn't be loaded
			if (shader.getProgram() == 0)
				return;

			Material material = new Material();
			material.setShader(shader);

			shape.setMaterial(material);
		} else {
			mRenderer = new GLRenderer10(getApplication());
		}

		mViewer = new GLViewer(this, mRenderer, trackball);
		// Set the OpenGL Context to version 2.0
		// Has to be done after the Viewer is initialized
		if (openGlES20) {
			mViewer.setEGLContextClientVersion(2);
		}
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

	private Shader createShaders() {
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
	}

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

	private Shape loadCube() {
		// Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
//        VertexData vertexData = new VertexData(vertices.length / 3);
//        vertexData.addElement(vertices, VertexData.Semantic.POSITION, 3);
//        vertexData.addElement(colors, VertexData.Semantic.COLOR, 3);

//        vertexData.addIndices(indices);
        
        VertexBuffers vertexBuffer = new VertexBuffers();
        vertexBuffer.setColorBuffer(colors);
        vertexBuffer.setIndexBuffer(indices);
        vertexBuffer.setVertexBuffer(vertices);

        // Make a shape and add the object
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
	
	
	int one = 0x10000;
    float vertices[] = {
            -one, -one, -one,
            one, -one, -one,
            one,  one, -one,
            -one,  one, -one,
            -one, -one,  one,
            one, -one,  one,
            one,  one,  one,
            -one,  one,  one,
    };
    
    int colors[] = {
            0,    0,    0,  one,
            one,    0,    0,  one,
            one,  one,    0,  one,
            0,  one,    0,  one,
            0,    0,  one,  one,
            one,    0,  one,  one,
            one,  one,  one,  one,
            0,  one,  one,  one,
    };

    short indices[] = {
            0, 4, 5,    0, 5, 1,
            1, 5, 6,    1, 6, 2,
            2, 6, 7,    2, 7, 3,
            3, 7, 4,    3, 4, 0,
            4, 7, 6,    4, 6, 5,
            3, 0, 1,    3, 1, 2
    };
}