package ch.chnoch.thesis.renderer.util;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;

import android.opengl.GLES20;
import android.util.Log;

/**
 * A collection of usable methods related to interacting with OpenGL ES.
 */
public class GLUtil {
	
	/** a float array representing a 4x4 matrix. */
	private static float[] f4 = new float[16];
	
	/** a float array representing a 3x3 matrix. */
	private static float[] f3 = new float[9];

	/**
	 * Checks for the last GL error that occured. Throws a runtime exception if
	 * an error is found. To be called with the last operation done (first
	 * parameter), so the error can be traced.
	 *
	 * @param operation the operation
	 * @param tag the tag
	 * @throws Exception the exception
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
	 * Convert a {@link Matrix4f} to a float array in column major ordering, as
	 * used by OpenGL.
	 * 
	 * @param m
	 *            the matrix
	 * @return the float array
	 */
	public static float[] matrix4fToFloat16(Matrix4f m) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				f4[j * 4 + i] = m.getElement(i, j);
		return f4;
	}

	/**
	 * Converts a {@link Matrix3f} to a float array in column major ordering, as
	 * used by OpenGL.
	 * 
	 * @param m
	 *            the m
	 * @return the float[]
	 */
	public static float[] matrix3fToFloat9(Matrix3f m) {
		for (int i= 0; i< 3; i++) {
			for (int j=0;j<3;j++) {
				f3[j*3 + i] = m.getElement(i, j);
			}
		}
		return f3;
	}

	/**
	 * Converts regular integers to 16/16 fixed point integers, that are used in
	 * Androids OpenGL ES 1.1 implementation.
	 * 
	 * @param buffer
	 *            the buffer containing the float values
	 */
	 public static void convertFloatToFixedPoint(FloatBuffer buffer) {		 
		for (int i = 0; i< buffer.capacity(); i++) {
			float value = buffer.get(i);
			buffer.put(i, (int) (value*65536));
		}
		buffer.position(0);
	}
}
