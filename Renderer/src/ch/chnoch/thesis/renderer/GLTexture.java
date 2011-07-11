package ch.chnoch.thesis.renderer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ch.chnoch.thesis.renderer.interfaces.Texture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static android.opengl.GLES20.*;
import android.opengl.GLUtils;


/**
 * An OpenGL texture.
 */
public class GLTexture implements Texture {
	
	private int mTextureID;	// Stores the OpenGL texture identifier
	private Context mContext;
	private int mResource;
	
	public GLTexture(Context context) 
	{
		mContext = context;
	}

	/**
	 * Load the texture from an image file.
	 */
	public void createTexture(int resource) throws IOException
	{
		mResource = resource;
	} 
	
	/**
	 * This needs to be called from the renderer only, therefor it's
	 * package scope
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
	
	public int getID() {
		return mTextureID;
	}
}
