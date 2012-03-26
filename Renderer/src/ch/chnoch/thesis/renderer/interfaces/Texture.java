package ch.chnoch.thesis.renderer.interfaces;

import java.io.IOException;

/**
 * Declares the functionality to manage textures in OpenGL.
 */
public interface Texture {

	/**
	 * Creates the texture. Android manages resources with an integer id. This
	 * method is used to create a reference to the resource with the correct
	 * image.
	 * 
	 * @param resource
	 *            the resource that references the image for the texture
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void createTexture(int resource) throws IOException;

	/**
	 * Loads the image from the Android resources and passes it to OpenGL as a
	 * texture. Creates an id for the texture that will be used for further
	 * reference of the texture.
	 */
	public void load();

	/**
	 * Gets the ID that is associated with this texture.
	 * 
	 * @return the ID
	 */
	public int getID();
}
