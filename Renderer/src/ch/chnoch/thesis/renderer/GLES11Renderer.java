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

import android.content.Context;
import android.util.Log;
import static android.opengl.GLES10.*;

public class GLES11Renderer extends AbstractRenderer {

	private ShortBuffer mIndexBuffer;
	private IntBuffer mVertexBuffer;
	private FloatBuffer mTexCoordsBuffer;
	private IntBuffer mColorBuffer;
	private IntBuffer mNormalBuffer;
	private int width, height;

	private final String TAG = "GLES11Renderer";

	/**
	 * This constructor is called by {@link GLRenderPanel}.
	 * 
	 * @param drawable
	 *            the OpenGL rendering context. All OpenGL calls are directed to
	 *            this object.
	 */
	public GLES11Renderer() {
		super();
	}
	

	public GLES11Renderer(SceneManagerInterface sceneManager) {
		super(sceneManager);
	}
	

	/*
	 * Public Methods
	 */

	@Override
	public Shader makeShader(String vertexShader, String fragmentShader) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Texture makeTexture() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Framework Callback Methods
	 */

	public void onDrawFrame(GL10 gl) {
		calculateFPS();
		long oldTime = System.currentTimeMillis();

		SceneManagerIterator shapeIterator = mSceneManager.iterator();

		/*
		 * Usually, the first thing one might want to do is to clear the screen.
		 * The most efficient way of doing this is to use glClear().
		 */
		gl.glViewport(0, 0, width, height);
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		int count = 0;
		while (shapeIterator.hasNext()) {
			Log.d("Renderer", "count: " + count);
			count++;
			draw(shapeIterator.next(), gl);
		}

		long newTime = System.currentTimeMillis();

		long diff = newTime - oldTime;
		int error = gl.glGetError();
		if (error!=GL10.GL_NO_ERROR){
			Log.d("GLError", "Error: " +error);
		}
//		Log.d("RendererFrame", "Rendering single frame: " + diff + " ms");

	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "onsurfacechanged method called");
		Log.d(TAG, "width: " + width + " height: " + height);
		mViewer.surfaceHasChanged(width, height);
//		setViewportMatrix(width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */
		float ratio = (float)width/height;
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 2, 50);
		float[] projectionMat = new float[16];
		((GL11)gl).glGetFloatv(GL11.GL_PROJECTION_MATRIX, projectionMat, 0);
		Log.d("Projection Matrix", new Matrix4f(projectionMat).toString());
		this.width = width;
		this.height = height;
//		gl.glLoadMatrixf(
//				GLUtil.matrix4fToFloat16(mFrustum.getProjectionMatrix()), 0);
//		gl.glViewport(0, 0, width, height);
	}

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
		gl.glDisable(GL_DITHER);
		
		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);

		gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		gl.glClearDepthf(1f);
//		gl.glEnable(GL_CULL_FACE);
		gl.glShadeModel(GL_SMOOTH);
		gl.glEnable(GL_DEPTH_TEST);
//		gl.glDepthFunc(GL_LEQUAL);
//		gl.glDepthMask(true);
//		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

//		gl.glMatrixMode(GL_PROJECTION);
//		gl.glLoadMatrixf(
//				GLUtil.matrix4fToFloat16(mFrustum.getProjectionMatrix()), 0);

		setLights(gl);
	}

	/*
	 * Private Methods
	 */

	/**
	 * The main rendering method for one item.
	 * 
	 * @param renderItem
	 *            the object that needs to be drawn
	 */
	private void draw(RenderItem renderItem, GL10 gl) {

		Log.d("Renderer", "Called draw method");
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

		Log.d("ModelView", t.toString());
		gl.glLoadMatrixf(GLUtil.matrix4fToFloat16(t), 0);

		setMaterial(renderItem.getNode().getMaterial(), gl);

		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glFrontFace(GL_CW);
		gl.glVertexPointer(3, GL_FIXED, 0, mVertexBuffer);
		// gl.glColorPointer(4, GL_FIXED, 0, mColorBuffer);
		gl.glEnableClientState(GL_NORMAL_ARRAY);
		gl.glNormalPointer(GL_FIXED, 0, mNormalBuffer);
		// gl.glEnable(GL_TEXTURE_2D);
		// gl.glTexCoordPointer(2, GL_FLOAT, 0, mTexCoordsBuffer);
		gl.glDrawElements(GL_TRIANGLES, mIndexBuffer.capacity(),
				GL_UNSIGNED_SHORT, mIndexBuffer);
//		gl.glPointSize(3);
//		gl.glDrawArrays(GL_POINTS, 0, 24);
		gl.glDisableClientState(GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL_VERTEX_ARRAY);
	}

	private void setLights(GL10 gl) {
		int lightIndex[] = { GL_LIGHT0, GL_LIGHT1, GL_LIGHT2,
				GL_LIGHT3, GL_LIGHT4, GL_LIGHT5, GL_LIGHT6,
				GL_LIGHT7 };

		gl.glEnable(GL_LIGHTING);
		gl.glLoadIdentity();

		Iterator<Light> iter = mSceneManager.lightIterator();

		int i = 0;
		Light l;
		while (iter.hasNext() && i < 8) {
			l = iter.next();
			gl.glEnable(lightIndex[i]);

			if (l.type == Light.Type.DIRECTIONAL) {
				gl.glLightfv(lightIndex[i], GL_POSITION,
						l.createDirectionArray(), 0);
			}
			if (l.type == Light.Type.POINT || l.type == Light.Type.SPOT) {
				gl.glLightfv(lightIndex[i], GL_POSITION,
						l.createPositionArray(), 0);
			}
			if (l.type == Light.Type.SPOT) {
				gl.glLightfv(lightIndex[i], GL_SPOT_DIRECTION,
						l.createSpotDirectionArray(), 0);
				gl.glLightf(lightIndex[i], GL_SPOT_EXPONENT,
						l.spotExponent);
				gl.glLightf(lightIndex[i], GL_SPOT_CUTOFF, l.spotCutoff);
			}

			gl.glLightfv(lightIndex[i], GL_DIFFUSE,
					l.createDiffuseArray(), 0);
			gl.glLightfv(lightIndex[i], GL_AMBIENT,
					l.createAmbientArray(), 0);
			gl.glLightfv(lightIndex[i], GL_SPECULAR,
					l.createSpecularArray(), 0);

			i++;
		}
		
		gl.glEnable(GL10.GL_CCW);
	}

	/**
	 * Pass the material properties to OpenGL, including textures and shaders.
	 */
	private void setMaterial(Material m, GL10 gl) {
		if (m != null) {
			gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE,
					m.createDiffuseArray(), 0);

			gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT,
					m.createAmbientArray(), 0);

			gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR,
					m.createSpecularArray(), 0);

			gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS,
					m.shininess);
		}
	}

	// only used to calculate rendering time per frame
	private int frameCount;
	private long currentTime, previousTime = 0;
	private float fps;

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

//			Log.d("Renderer", "Rendertime per Frame: " + fps + " ms");
		}
	}


}
