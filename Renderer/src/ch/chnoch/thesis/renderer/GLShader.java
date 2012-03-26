package ch.chnoch.thesis.renderer;

import android.opengl.GLES20;
import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.Shader;
import ch.chnoch.thesis.renderer.util.GLUtil;

/**
 * This class manages an OpenGL shader. It will load the shader source code into
 * OpenGL, store a reference to the shader and make that available as well as
 * providing the service of enabling or disabling a shader.
 * <p>
 * Only works with OpenGL ES 2.0
 */
public class GLShader implements Shader {

	private int mProgram = 0; // The shader identifier

	/** The Constant TAG. */
	private static final String TAG = "GLShader";

	/**
	 * Instantiates a new OpenGL shader.
	 */
	public GLShader() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Shader#load(java.lang.String,
	 * java.lang.String)
	 */
	public int load(String vertexSource, String fragmentSource)
			throws Exception {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		if (vertexShader == 0) {
			return 0;
		}

		int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		if (pixelShader == 0) {
			return 0;
		}

		mProgram = GLES20.glCreateProgram();
		if (mProgram != 0) {
			GLES20.glAttachShader(mProgram, vertexShader);
			GLUtil.checkGlError("glAttachShader", TAG);
			GLES20.glAttachShader(mProgram, pixelShader);
			GLUtil.checkGlError("glAttachShader", TAG);
			GLES20.glLinkProgram(mProgram);
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus,
					0);
			if (linkStatus[0] != GLES20.GL_TRUE) {
				Log.e(TAG, "Could not link program: ");
				Log.e(TAG, GLES20.glGetProgramInfoLog(mProgram));
				GLES20.glDeleteProgram(mProgram);
				mProgram = 0;
			}
		}
		return mProgram;
	}

	/**
	 * Loads either a vertex or fragment shader into OpenGL.
	 * 
	 * @param shaderType
	 *            the shader type
	 * @param source
	 *            the shader source
	 * @return the shader reference
	 */
	private int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType);
		if (shader != 0) {
			GLES20.glShaderSource(shader, source);
			GLES20.glCompileShader(shader);
			int[] compiled = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == 0) {
				Log.e(TAG, "Could not compile shader " + shaderType + ":");
				Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}
		return shader;
	}

	/**
	 * Activate the shader program. As long as the shader is active, the vertex
	 * shader is executed for each vertex, and the fragment shader for each
	 * pixel that is rendered.
	 */
	public void use() {
		try {
			GLES20.glUseProgram(mProgram);
			GLUtil.checkGlError("glUseProgram", TAG);
		} catch (Exception exc) {
			Log.d(TAG, "Couldn't use program: " + exc.getMessage());
		}
	}

	/**
	 * Disables the shader and goes back to using OpenGL standard functionality
	 * to process vertices and fragments/pixels.
	 */
	public void disable() {
		try {
			GLES20.glUseProgram(0);
			GLUtil.checkGlError("glUseProgram", TAG);
		} catch (Exception exc) {
			Log.d(TAG, "Couldn't use program: " + exc.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Shader#getProgram()
	 */
	public int getProgram() {
		return mProgram;
	}
}
