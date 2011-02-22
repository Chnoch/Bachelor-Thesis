package ch.chnoch.thesis.renderer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;


/**
 * An OpenGL texture.
 */
public class GLTexture implements Texture {
	
	private Context mContext;
	private int mTextureID;	// Stores the OpenGL texture identifier
	
	public GLTexture(Context context) 
	{
		mContext = context;
	}

	/**
	 * Load the texture from an image file.
	 */
	public void load(String fileName) throws IOException
	{
		
		 int[] textures = new int[1];
	        GLES20.glGenTextures(1, textures, 0);

	        mTextureID = textures[0];
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
	                GLES20.GL_NEAREST);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_MAG_FILTER,
	                GLES20.GL_LINEAR);

	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
	                GLES20.GL_REPEAT);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
	                GLES20.GL_REPEAT);

	        InputStream is = new FileInputStream(fileName);
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

	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	        bitmap.recycle();
	}
}
