package ch.chnoch.thesis.basicglactivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.BasicGLES20Renderer;
import ch.chnoch.thesis.renderer.BasicRenderer;
import ch.chnoch.thesis.renderer.GLES20Renderer;
import ch.chnoch.thesis.renderer.GLException;
import ch.chnoch.thesis.renderer.GLMaterial;
import ch.chnoch.thesis.renderer.Light;
import ch.chnoch.thesis.renderer.Material;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;


public class BasicGLActivityActivity extends Activity {

	private GLSurfaceView mGLView;
	private BasicGLES20Renderer mRenderer;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRenderer = new BasicGLES20Renderer();
		init();
		mGLView = new GLSurfaceView(this);
		mGLView.setEGLConfigChooser(false);
		mGLView.setEGLContextClientVersion(2);
		mGLView.setRenderer(mRenderer);
		mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		setContentView(mGLView);
	}
	
	private void init() {
		FloatBuffer mVertexBuffer = ByteBuffer.allocateDirect(vertices.length
                * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(vertices).position(0);
		
//		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
//		vbb.order(ByteOrder.nativeOrder());
//		FloatBuffer mVertexBuffer = vbb.asFloatBuffer();
//		mVertexBuffer.put(vertices);
//		mVertexBuffer.position(0);

		ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
		nbb.order(ByteOrder.nativeOrder());
		FloatBuffer  mNormalBuffer = nbb.asFloatBuffer();
		mNormalBuffer.put(normals);
		mNormalBuffer.position(0);

		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer mIndexBuffer = ibb.asShortBuffer();
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
		
		
		mRenderer.setIndexBuffer(mIndexBuffer);
		mRenderer.setNormalBuffer(mNormalBuffer);
		mRenderer.setVertexBuffer(mVertexBuffer);
		
		initLight();
		initMaterial();
	}
	
	
	private void initLight() {
		float[] position = { 0f, 0f, 0, 1 };
		float[] diffuse = { .6f, .6f, .6f, 1f };
		float[] specular = { 1, 1, 1, 1 };
		float[] ambient = { 0.2f, 0.2f, .2f, 1 };
		
		Light light = new Light(null);
		light.setAmbient( 0.2f, 0.2f, .2f);
		light.setDiffuse(0.6f,.6f,.6f);
		light.setPosition(0,0,0);
		light.setSpecular(1,1,1);
		mRenderer.setLight(light);
	}
	
	private void initMaterial(){
		Material mat = new GLMaterial();
		
		mat.mAmbient = new Vector3f(0, 0, .3f);
		mat.mDiffuse = new Vector3f(0, 0, .7f);
		mat.mSpecular = new Vector3f(1, 1, 1);
		mat.shininess = 30;
		
		Shader shader = createShaders();
		mat.setShader(shader);
		
		mRenderer.setMaterial(mat);
	}
	
	
	private Shader createShaders() {
		String vertexShader = readRawText(R.raw.phongvert);
		String fragmentShader = readRawText(R.raw.phongfrag);
		Shader shader = null;
		try {
			mRenderer.createShader(shader, vertexShader, fragmentShader);
			return shader;
			// if (shader.getProgram() == 0) {
			// throw new RuntimeException();
			// }
		} catch (GLException exc) {
			Log.e("BasicActivity", exc.getError());
		} catch (Exception exc) {
			Log.e("BasicActivity", "Error loading Shaders", exc);
		}
		return null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
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
	
	private float vertices[] = {
			// Vertices for the square
			-1.0f, -1.0f, 0.0f, // 0. left-bottom
			1.0f, -1.0f, 0.0f, // 1. right-bottom
			-1.0f, 1.0f, 0.0f, // 2. left-top
			1.0f, 1.0f, 0.0f // 3. right-top
	};

	private short indices[] = {
			0,1,3,
			0,3,2
	};

	private float normals[] = {
			0,0,1,
			0,0,1,
			0,0,1,
			0,0,1
	};
}