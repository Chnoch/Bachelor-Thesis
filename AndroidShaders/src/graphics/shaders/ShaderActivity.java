/**
 * OpenGL ES 2.0 examples for Android
 * ----------------------------------

   The purpose was to create a simple framework for working with 
   OpenGL ES 2.0 in Android. Features:

   1) Loads triangular meshes in these formats:
 	- (.OFF): Vertex positions + normals
 	- (.OBJ): Vertex positions, normals and texture coordinates

   2) Shader functionality. There are three shaders:
    - Gouraud Shading (Per-vertex lighting)
    - Phong Shading (Per-pixel lighting)
    - Normal Mapping

    The code was constructed by using GLES20TriangleRenderer.java from the Android SDK as a base.
    @Author Shayan Javed.
    Last edited: 13th March 2011
 */

package graphics.shaders;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

/**
 * This sample shows how to check for OpenGL ES 2.0 support at runtime, and then
 * use either OpenGL ES 1.0 or OpenGL ES 2.0, as appropriate.
 */
public class ShaderActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a new GLSurfaceView - this holds the GL Renderer
		mGLSurfaceView = new GLSurfaceView(this);

		// detect if OpenGL ES 2.0 support exists - if it doesn't, exit.
		if (detectOpenGLES20()) {
			// Tell the surface view we want to create an OpenGL ES 2.0-compatible
			// context, and set an OpenGL ES 2.0-compatible renderer.
			mGLSurfaceView.setEGLContextClientVersion(2);
			renderer = new Renderer(this);
			mGLSurfaceView.setRenderer(renderer);
		} 
		else { // quit if no support - get a better phone! :P
			this.finish();
		}

		// set the content view
		setContentView(mGLSurfaceView);
	}

	/**
	 * Detects if OpenGL ES 2.0 exists
	 * @return true if it does
	 */
	private boolean detectOpenGLES20() {
		ActivityManager am =
			(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		Log.d("OpenGL Ver:", info.getGlEsVersion());
		return (info.reqGlEsVersion >= 0x20000);
	}

	/************
	 *  MENU FUNCTIONS
	 **********/
	/*
	 * Creates the menu and populates it via xml
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_menu, menu);
		return true;
	}

	/*
	 * On selection of a menu item
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.toggleLight:
			renderer.toggleLight();
			return true;
		case R.id.gouraud: 			// Gouraud Shading
			renderer.setShader(this.GOURAUD_SHADER);
			return true;
		case R.id.phong: 			// Phong Shading
			renderer.setShader(this.PHONG_SHADER);
			return true;
		case R.id.normal_map:		// Normal Mapping
			renderer.setShader(this.NORMALMAP_SHADER);
			return true;
		case R.id.quit:				// Quit the program
			quit();
			return true;
		case R.id.cube:				// Cube
			renderer.setObject(this.CUBE);
			return true;
		case R.id.octahedron:		// Octahedron
			renderer.setObject(this.OCTAHEDRON);
			return true;
		case R.id.tetrahedron:		// Tetrahedron
			renderer.setObject(this.TETRAHEDRON);
			return true;
		case R.id.texture:			// Enable/disable texturing
			renderer.flipTexturing();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/************
	 * TOUCH FUNCTION - Should allow user to rotate the environment
	 **********/
	@Override public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:			// one touch: drag
			Log.d("ShaderActivity", "mode=DRAG" );
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:	// two touches: zoom
			Log.d("ShaderActivity", "mode=ZOOM" );
			oldDist = spacing(e);
			if (oldDist > 10.0f) {
				mode = ZOOM; // zoom
			}
			break;
		case MotionEvent.ACTION_UP:		// no mode
			mode = NONE;
			Log.d("ShaderActivity", "mode=NONE" );
			oldDist = 100.0f;
			break;
		case MotionEvent.ACTION_POINTER_UP:		// no mode
			mode = NONE;
			Log.d("ShaderActivity", "mode=NONE" );
			oldDist = 100.0f;
			break;
		case MotionEvent.ACTION_MOVE:						// rotation
			if (e.getPointerCount() > 1 && mode == ZOOM) {
				newDist = spacing(e);
				Log.d("SPACING: ", "OldDist: " + oldDist + ", NewDist: " + newDist);
				if (newDist > 10.0f) {
					float scale = newDist/oldDist; // scale
					// scale in the renderer
					renderer.changeScale(scale);

					oldDist = newDist;
				}
			}
			else if (mode == DRAG){
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				renderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
				renderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
				mGLSurfaceView.requestRender();
			}
			break;
		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}

	// finds spacing
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}


	// Quit the app
	private void quit() {
		//super.onDestroy();
		this.finish();
	}

	/********************************
	 * PROPERTIES
	 *********************************/

	private GLSurfaceView mGLSurfaceView;

	// The Renderer
	Renderer renderer;

	// rotation
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private float mPreviousX;
	private float mPreviousY;

	// shader constants
	private final int GOURAUD_SHADER = 0;
	private final int PHONG_SHADER = 1;
	private final int NORMALMAP_SHADER = 2;


	// object constants
	private final int OCTAHEDRON = 0;
	private final int TETRAHEDRON = 1;
	private final int CUBE = 2;

	// touch events
	private final int NONE = 0;
	private final int DRAG = 0;
	private final int ZOOM = 0;

	// pinch to zoom
	float oldDist = 100.0f;
	float newDist;

	int mode = 0;
}
