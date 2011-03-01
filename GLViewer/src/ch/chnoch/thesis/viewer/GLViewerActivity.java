package ch.chnoch.thesis.viewer;

import java.io.IOException;

import ch.chnoch.thesis.renderer.GLRenderer;
import ch.chnoch.thesis.renderer.GLViewer;
import ch.chnoch.thesis.renderer.util.ObjReader;
import ch.chnoch.thesis.renderer.Material;
import ch.chnoch.thesis.renderer.Shader;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.SimpleSceneManager;
import ch.chnoch.thesis.renderer.VertexData;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GLViewerActivity extends Activity {
	
	private GLViewer viewer;
	private SimpleSceneManager mSceneManager;
	private GLRenderer mRenderer;
	private final String TAG = "GLViewerActivity";
	

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		mSceneManager = new SimpleSceneManager();
		Shape shape = loadTeapot();
		mSceneManager.addShape(shape);
		
		Shader shader = createShaders();
		// exit if the shaders couldn't be loaded
		if (shader.getProgram()== 0) return;
		
		Material material = new Material();
		material.setShader(shader);
		
		
		shape.setMaterial(material);
		
		mRenderer = new GLRenderer(getApplication());
		
		mRenderer.setSceneManager(mSceneManager);
        viewer = new GLViewer(getApplication(), mRenderer);
        
        setContentView(viewer);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	viewer.onPause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	viewer.onResume();
    }
    
    private Shader createShaders() {
    	String vertexShader = getApplication().getResources().getString(R.raw.simplevert);
    	String fragmentShader = getApplication().getResources().getString(R.raw.simplefrag);
    	
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
		VertexData vertexData = null;
        try {
        	String teapotSrc = getApplication().getResources().getString(R.raw.teapot);
            vertexData = ObjReader.read(teapotSrc, 1);
        } catch (IOException exc) {
        	Log.e(TAG, "Error loading Vertex data", exc);
        }
        
        return new Shape(vertexData);
	}
}