package ch.chnoch.thesis.renderer;

import static android.opengl.GLES10.GL_AMBIENT;
import static android.opengl.GLES10.GL_CCW;
import static android.opengl.GLES10.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES10.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES10.GL_DEPTH_TEST;
import static android.opengl.GLES10.GL_DIFFUSE;
import static android.opengl.GLES10.GL_FASTEST;
import static android.opengl.GLES10.GL_FLOAT;
import static android.opengl.GLES10.GL_FRONT_AND_BACK;
import static android.opengl.GLES10.GL_LIGHT0;
import static android.opengl.GLES10.GL_LIGHT1;
import static android.opengl.GLES10.GL_LIGHT2;
import static android.opengl.GLES10.GL_LIGHT3;
import static android.opengl.GLES10.GL_LIGHT4;
import static android.opengl.GLES10.GL_LIGHT5;
import static android.opengl.GLES10.GL_LIGHT6;
import static android.opengl.GLES10.GL_LIGHT7;
import static android.opengl.GLES10.GL_LIGHTING;
import static android.opengl.GLES10.GL_MODELVIEW;
import static android.opengl.GLES10.GL_NORMALIZE;
import static android.opengl.GLES10.GL_NORMAL_ARRAY;
import static android.opengl.GLES10.GL_PERSPECTIVE_CORRECTION_HINT;
import static android.opengl.GLES10.GL_POSITION;
import static android.opengl.GLES10.GL_PROJECTION;
import static android.opengl.GLES10.GL_SHININESS;
import static android.opengl.GLES10.GL_SMOOTH;
import static android.opengl.GLES10.GL_SPECULAR;
import static android.opengl.GLES10.GL_SPOT_CUTOFF;
import static android.opengl.GLES10.GL_SPOT_DIRECTION;
import static android.opengl.GLES10.GL_SPOT_EXPONENT;
import static android.opengl.GLES10.GL_TEXTURE_2D;
import static android.opengl.GLES10.GL_TRIANGLES;
import static android.opengl.GLES10.GL_UNSIGNED_SHORT;
import static android.opengl.GLES10.GL_VERTEX_ARRAY;
import static android.opengl.GLES20.glClearColor;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerIterator;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.util.GLUtil;

/**
 * This is an implementation of a renderer for devices that only support OpenGL
 * ES 1.1. It contains everything that is necessary to convert all the object
 * and properties of our library into the appropriate OpenGL objects and pass
 * them to the Android framework where the rendering will occur via OpenGL.
 * 
 * OpenGL ES 1.1 has a fixed pipeline. Therefore you don't have any shaders that
 * you can specify, but rather you pass everything you have to OpenGL and let
 * the fixed pipeline do the rest. You can pass the indices, vertices, texture
 * coordinates, colors and normals of a triangle mesh. You can also pass light
 * as well as material to OpenGL.
 * 
 * As of March 2012 only 10% of the activated Android devices only support
 * OpenGL ES 1.1. Therefore this Renderer is only used for legacy purposes and
 * the longer it takes the less it will have to be used.
 */
public class GLES11Renderer extends AbstractRenderer {

	private ShortBuffer mIndexBuffer;
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTexCoordsBuffer;
	private FloatBuffer mColorBuffer;
	private FloatBuffer mNormalBuffer;
	
	/**
	 * Instantiates a new renderer using the OpenGL ES 1.1 platform
	 * 
	 */
	public GLES11Renderer() {
		super();
	}

	/**
	 * Instantiates a new renderer using the OpenGL ES 1.1 platform
	 * 
	 * @param sceneManager
	 *            the scene manager that will be rendered
	 */
	public GLES11Renderer(SceneManagerInterface sceneManager) {
		super(sceneManager);
	}

	/**
	 * This method creates an exception as OpenGL ES 1.1 doesn't support
	 * shaders.
	 */
	@Override
	public void createShader(Shader shader, String vertexShader,
			String fragmentShader) throws Exception {
		throw new GLException("OpenGL ES 1.1 does not support shaders");
	}

	/**
	 * Using textures in OpenGL ES 1.1 hasn't been implemented yet.
	 */
	public Texture createTexture() throws Exception {
		throw new GLException(
				"Using textures in OpenGL ES 1.1 has not been implemented");
	}

	/*
	 * FRAMEWORK CALLBACK METHODS
	 */

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	public void onDrawFrame(GL10 gl) {

		SceneManagerIterator shapeIterator = mSceneManager.iterator();

		/*
		 * Usually, the first thing one might want to do is to clear the screen.
		 * The most efficient way of doing this is to use glClear().
		 */
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		while (shapeIterator.hasNext()) {
			draw(shapeIterator.next(), gl);
		}
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.e("GLError", "Error: " + error);
		}
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		mViewer.surfaceHasChanged(width, height);
		setViewportMatrix(width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glLoadMatrixf(
				GLUtil.matrix4fToFloat16(mFrustum.getProjectionMatrix()),
				0);
		
		gl.glViewport(0, 0, width, height);
		
		// mSceneManager.getFrustum().setAspectRatio(width / height);

		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.d("GLError", "Error onSurfaceChanged: " + error);
		}
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

		gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		gl.glClearDepthf(1f);
		gl.glShadeModel(GL_SMOOTH);
		gl.glEnable(GL_DEPTH_TEST);

		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.e("GLError", "Error onSurfaceCreated preLights: " + error);
		}
		
		setLights(gl);
		
		error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.e("GLError", "Error onSurfaceCreated: " + error);
		}
	}

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * The main rendering method for one node.
	 * 
	 * @param renderItem
	 *            the object that needs to be drawn
	 * @param gl
	 *            the OpenGL reference
	 */
	private void draw(RenderItem renderItem, GL10 gl) {

		Shape shape = renderItem.getNode().getShape();
		VertexBuffers buffers = shape.getVertexBuffers();
		mVertexBuffer = buffers.getVertexBuffer();
		mColorBuffer = buffers.getColorBuffer();
		mIndexBuffer = buffers.getIndexBuffer();
		mTexCoordsBuffer = buffers.getTexCoordsBuffer();
		mNormalBuffer = buffers.getNormalBuffer();

		gl.glMatrixMode(GL_MODELVIEW);
		t.set(mCamera.getCameraMatrix());
		t.mul(renderItem.getT());

		gl.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);

		setMaterial(renderItem.getNode().getMaterial(), gl);

		gl.glEnable(GL_NORMALIZE);
		gl.glEnableClientState(GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glFrontFace(GL_CCW);
		gl.glVertexPointer(3, GL_FLOAT, 0, mVertexBuffer);
		if (mColorBuffer != null)
			gl.glColorPointer(4, GL_FLOAT, 0, mColorBuffer);
		if (mNormalBuffer != null)
		gl.glNormalPointer(GL_FLOAT, 0, mNormalBuffer);

		if (mTexCoordsBuffer != null) {
			gl.glEnable(GL_TEXTURE_2D);
			gl.glTexCoordPointer(2, GL_FLOAT, 0, mTexCoordsBuffer);
		}
		gl.glDrawElements(GL_TRIANGLES, mIndexBuffer.capacity(),
				GL_UNSIGNED_SHORT, mIndexBuffer);
		gl.glDisableClientState(GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL_NORMAL_ARRAY);
		gl.glDisable(GL_NORMALIZE);
		
	}

	/**
	 * Sets the lights.
	 * 
	 * @param gl
	 *            the OpenGL reference object
	 */
	private void setLights(GL10 gl) {
		int lightIndex[] = { GL_LIGHT0, GL_LIGHT1, GL_LIGHT2, GL_LIGHT3,
				GL_LIGHT4, GL_LIGHT5, GL_LIGHT6, GL_LIGHT7 };

		gl.glEnable(GL_LIGHTING);
		gl.glMatrixMode(GL_MODELVIEW);
		t.set(mCamera.getCameraMatrix());
		gl.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);

		Iterator<Light> iter = mSceneManager.lightIterator();

		int i = 0;
		Light l;
		while (iter.hasNext() && i < 8) {
			l = iter.next();
			
			gl.glEnable(lightIndex[i]);
			
			if (l.getType() == Light.Type.DIRECTIONAL) {
				gl.glLightfv(lightIndex[i], GL_POSITION,
						l.createDirectionArray(), 0);
			}
			if (l.getType() == Light.Type.POINT || l.getType() == Light.Type.SPOT) {
				float[] position = l.createPositionArray();
				gl.glLightfv(lightIndex[i], GL_POSITION, position
						, 0);
				
			}
			if (l.getType() == Light.Type.SPOT) {
				gl.glLightfv(lightIndex[i], GL_SPOT_DIRECTION,
						l.createSpotDirectionArray(), 0);
				gl.glLightf(lightIndex[i], GL_SPOT_EXPONENT, l.getSpotExponent());
				gl.glLightf(lightIndex[i], GL_SPOT_CUTOFF, l.getSpotCutoff());
			}

			gl.glLightfv(lightIndex[i], GL_DIFFUSE, l.createDiffuseArray(), 0);
			gl.glLightfv(lightIndex[i], GL_AMBIENT, l.createAmbientArray(), 0);
			gl.glLightfv(lightIndex[i], GL_SPECULAR, l.createSpecularArray(), 0);

			i++;
		}
		
	}

	/**
	 * Pass the material properties to OpenGL, including textures.
	 * 
	 * @param m
	 *            the material that needs to be drawn
	 * @param gl
	 *            the OpenGL reference
	 */
	private void setMaterial(Material m, GL10 gl) {
		if (m != null) {
			gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE,
					m.createDiffuseArray(), 0);

			gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT,
					m.createAmbientArray(), 0);

			gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR,
					m.createSpecularArray(), 0);

			gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, m.shininess);
		}
	}

	@Override
	public boolean supportsOpenGLES20() {
		return false;
	}

	/**
	 * // only used to calculate rendering time per frame private int
	 * frameCount; private long currentTime, previousTime = 0; private float
	 * fps;
	 * 
	 * /** Calculates the frames per second. / private void calculateFPS() { //
	 * Increase frame count frameCount++;
	 * 
	 * currentTime = System.currentTimeMillis();
	 * 
	 * // Calculate time passed long timeInterval = currentTime - previousTime;
	 * 
	 * if (timeInterval > 1000) { fps = timeInterval / frameCount;
	 * 
	 * previousTime = currentTime;
	 * 
	 * frameCount = 0; } }
	 */

}
