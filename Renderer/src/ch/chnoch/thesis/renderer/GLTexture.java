package ch.chnoch.thesis.renderer;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import ch.chnoch.thesis.renderer.interfaces.Texture;

/**
 * This class handles an OpenGL texture. It creates the texture from a specified
 * image, passes it to OpenGL, and stores a reference to this texture that will
 * be used for drawing.
 * <p>
 * Only works with OpenGL ES 2.0
 */
public class GLTexture implements Texture {

	private Context mContext;

	// Stores the OpenGL texture identifier
	private int mTextureID;

	private int mResource;

	/**
	 * Instantiates a new texture for OpenGL ES 2.0.
	 * 
	 * @param context
	 *            the context that the application is running in.
	 */
	public GLTexture(Context context) {
		mContext = context;
	}

	/**
	 * Load the texture from an image file.
	 * 
	 * @param resource
	 *            the resource
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void createTexture(int resource) throws IOException {
		mResource = resource;
	}

	/**
	 * This needs to be called from the renderer only. It loads the textures
	 * into OpenGL.
	 */
	public void load() {
		int[] textures = new int[1];
		glGenTextures(1, textures, 0);

		mTextureID = textures[0];
		glBindTexture(GL_TEXTURE_2D, mTextureID);

		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		InputStream is = mContext.getResources().openRawResource(mResource);
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// Ignore.
			}
		}

		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Texture#getID()
	 */
	public int getID() {
		return mTextureID;
	}
}
