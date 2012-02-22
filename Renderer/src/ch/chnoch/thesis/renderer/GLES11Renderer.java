package ch.chnoch.thesis.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.interfaces.Texture;
import ch.chnoch.thesis.renderer.util.GLUtil;
import ch.chnoch.thesis.renderer.util.Util;

import android.content.Context;
import android.util.Log;
import static android.opengl.GLES10.*;
import static android.opengl.GLES20.glClearColor;

// TODO: Auto-generated Javadoc
/**
 * The Class GLES11Renderer.
 */
public class GLES11Renderer extends AbstractRenderer {

	/** The m index buffer. */
	private ShortBuffer mIndexBuffer;
	
	/** The m vertex buffer. */
	private FloatBuffer mVertexBuffer;
	
	/** The m tex coords buffer. */
	private FloatBuffer mTexCoordsBuffer;
	
	/** The m color buffer. */
	private FloatBuffer mColorBuffer;
	
	/** The m normal buffer. */
	private FloatBuffer mNormalBuffer;
	
	/** The height. */
	private int width, height;

	/** The TAG. */
	private final String TAG = "GLES11Renderer";

	/**
	 * This constructor is called by {@link GLRenderPanel}.
	 *
	 */
	public GLES11Renderer() {
		super();
	}

	/**
	 * Instantiates a new gLE s11 renderer.
	 *
	 * @param sceneManager the scene manager
	 */
	public GLES11Renderer(SceneManagerInterface sceneManager) {
		super(sceneManager);
	}

	/*
	 * Public Methods
	 */

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#createShader(ch.chnoch.thesis.renderer.interfaces.Shader, java.lang.String, java.lang.String)
	 */
	@Override
	public void createShader(Shader shader, String vertexShader,
			String fragmentShader) throws Exception {
		throw new GLException("OpenGL ES 1.1 does not support shaders");
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#createTexture()
	 */
	public Texture createTexture() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Framework Callback Methods
	 */

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	public void onDrawFrame(GL10 gl) {
//		calculateFPS();
//		long oldTime = System.currentTimeMillis();

		SceneManagerIterator shapeIterator = mSceneManager.iterator();

		/*
		 * Usually, the first thing one might want to do is to clear the screen.
		 * The most efficient way of doing this is to use glClear().
		 */
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//		int count = 0;
		while (shapeIterator.hasNext()) {
			// Log.d("Renderer", "count: " + count);
//			count++;
			draw(shapeIterator.next(), gl);
		}

//		long newTime = System.currentTimeMillis();
//
//		long diff = newTime - oldTime;
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.d("GLError", "Error: " + error);
		}
		// Log.d("RendererFrame", "Rendering single frame: " + diff + " ms");

	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "onsurfacechanged method called");
		Log.d(TAG, "width: " + width + " height: " + height);
		mViewer.surfaceHasChanged(width, height);
		setViewportMatrix(width, height);

		this.width = width;
		this.height = height;
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
//		gl.glFrustumf(-1f,1f, (float)(-9.0/16.0), (float)(+9.0/16.0), 1f, 50f);
		
		gl.glViewport(0, 0, width, height);
		
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.d("GLError", "Error onSurfaceChanged: " + error);
		}
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.AbstractRenderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.d(TAG, "onsurfacecreated method called");

		int[] depthbits = new int[1];
		gl.glGetIntegerv(GL_DEPTH_BITS, depthbits, 0);
		Log.d(TAG, "Depth Bits: " + depthbits[0]);

		Log.d(TAG, "Version: " + gl.glGetString(GL_VERSION));

		/*
		 * By default, OpenGL enables features that improve quality but reduce
		 * performance. One might want to tweak that especially on software
		 * renderer.
		 */
//		gl.glDisable(GL_DITHER);

		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

		gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		gl.glClearDepthf(1f);
//		 gl.glEnable(GL_CULL_FACE);
		gl.glShadeModel(GL_SMOOTH);
		gl.glEnable(GL_DEPTH_TEST);
		// gl.glDepthFunc(GL_LEQUAL);
		// gl.glDepthMask(true);
		// gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		// gl.glMatrixMode(GL_PROJECTION);
		// gl.glLoadMatrixf(
		// GLUtil.matrix4fToFloat16(mFrustum.getProjectionMatrix()), 0);

		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.d("GLError", "Error onSurfaceCreated preLights: " + error);
		}
		
		setLights(gl);
		
		error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.d("GLError", "Error onSurfaceCreated: " + error);
		}
	}

	/*
	 * Private Methods
	 */

	/**
	 * The main rendering method for one item.
	 *
	 * @param renderItem the object that needs to be drawn
	 * @param gl the gl
	 */
	private void draw(RenderItem renderItem, GL10 gl) {

		// Log.d("Renderer", "Called draw method");
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

		// Log.d("ModelView", t.toString());
		gl.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);

		setMaterial(renderItem.getNode().getMaterial(), gl);

		gl.glEnable(GL_NORMALIZE);
		gl.glEnableClientState(GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glFrontFace(GL_CCW);
		gl.glVertexPointer(3, GL_FLOAT, 0, mVertexBuffer);
//		 gl.glColorPointer(4, GL_FLOAT, 0, mColorBuffer);
		gl.glNormalPointer(GL_FLOAT, 0, mNormalBuffer);
		// gl.glEnable(GL_TEXTURE_2D);
		// gl.glTexCoordPointer(2, GL_FLOAT, 0, mTexCoordsBuffer);
		gl.glDrawElements(GL_TRIANGLES, mIndexBuffer.capacity(),
				GL_UNSIGNED_SHORT, mIndexBuffer);
		gl.glDisableClientState(GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL_NORMAL_ARRAY);
		gl.glDisable(GL_NORMALIZE);
		
	}

	/**
	 * Sets the lights.
	 *
	 * @param gl the new lights
	 */
	private void setLights(GL10 gl) {
		int lightIndex[] = { GL_LIGHT0, GL_LIGHT1, GL_LIGHT2, GL_LIGHT3,
				GL_LIGHT4, GL_LIGHT5, GL_LIGHT6, GL_LIGHT7 };

		gl.glEnable(GL_LIGHTING);
//		gl.glLoadIdentity();
		gl.glMatrixMode(GL_MODELVIEW);
		t.set(mCamera.getCameraMatrix());
		// Log.d("ModelView", t.toString());
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
	 * Pass the material properties to OpenGL, including textures and shaders.
	 *
	 * @param m the m
	 * @param gl the gl
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

	// only used to calculate rendering time per frame
	/** The frame count. */
	private int frameCount;
	
	/** The previous time. */
	private long currentTime, previousTime = 0;
	
	/** The fps. */
	private float fps;

	/**
	 * Calculate fps.
	 */
	private void calculateFPS() {
		// Increase frame count
		frameCount++;

		currentTime = System.currentTimeMillis();

		// Calculate time passed
		long timeInterval = currentTime - previousTime;

		if (timeInterval > 1000) {
			fps = timeInterval / frameCount;

			previousTime = currentTime;

			frameCount = 0;

			// Log.d("Renderer", "Rendertime per Frame: " + fps + " ms");
		}
	}

}
