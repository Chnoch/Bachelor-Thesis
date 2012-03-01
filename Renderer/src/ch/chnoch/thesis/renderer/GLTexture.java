package ch.chnoch.thesis.renderer;

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


// TODO: Auto-generated Javadoc
/**
 * An OpenGL texture.
 */
public class GLTexture implements Texture {
	
	/** The m texture id. */
	private int mTextureID;	// Stores the OpenGL texture identifier

	/** The m context. */
	private Context mContext;

	/** The m resource. */
	private int mResource;
	
	/**
	 * Instantiates a new gL texture.
	 * 
	 * @param context
	 *            the context
	 */
	public GLTexture(Context context) 
	{
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
	public void createTexture(int resource) throws IOException
	{
		mResource = resource;
	} 
	
	/**
	 * This needs to be called from the renderer only, therefor it's package
	 * scope.
	 */
	public void load() {
		int[] textures = new int[1];
        glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        glBindTexture(GL_TEXTURE_2D, mTextureID);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
                GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D,
                GL_TEXTURE_MAG_FILTER,
                GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
                GL_REPEAT);

        InputStream is = mContext.getResources()
        .openRawResource(mResource);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
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
