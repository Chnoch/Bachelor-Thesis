package ch.chnoch.thesis.renderer.util;

import java.nio.IntBuffer;

import javax.vecmath.Matrix4f;

import android.opengl.GLES20;
import android.util.Log;

public class GLUtil {
	private static float[] f = new float[16];

	/**
	 * Checks for the last GL error that occured. Throws a runtime exception if
	 * an error is found. To be called with the last operation done (first
	 * parameter), so the error can be traced.
	 */
	public static void checkGlError(String operation, String tag)
			throws Exception {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(tag, operation + ": glError " + error);
			throw new RuntimeException(operation + ": glError " + error);
		}
	}

	/**
	 * Convert a Matrix4f to a float array in column major ordering, as used by
	 * OpenGL.
	 */
	public static float[] matrix4fToFloat16(Matrix4f m) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				f[j * 4 + i] = m.getElement(i, j);
		return f;
	}
	
	/**
	 * Converts regular integers to 16/16 fixed point integers, that are used
	 * in Android.
	 * @param buffer
	 */
	 public static void convertIntToFixedPoint(IntBuffer buffer) {
		while (buffer.hasRemaining()) {
			int value = buffer.get();
			buffer.put(value*65536);
		}
		buffer.position(0);
	}
}
