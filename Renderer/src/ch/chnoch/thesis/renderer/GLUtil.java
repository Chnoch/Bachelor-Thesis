package ch.chnoch.thesis.renderer;

import android.opengl.GLES20;
import android.util.Log;

public class GLUtil {

	public static void checkGlError(String operation, String tag) throws Exception {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(tag, operation + ": glError " + error);
			throw new RuntimeException(operation + ": glError " + error);
		}
	}
}
