package ch.chnoch.thesis.renderer.interfaces;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * Declares the functionality to manage textures.
 */
public interface Texture {

	/**
	 * Creates the texture.
	 *
	 * @param resource the resource
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void createTexture(int resource) throws IOException;
	
	/**
	 * Load.
	 */
	public void load();
	
	/**
	 * Gets the iD.
	 *
	 * @return the iD
	 */
	public int getID();
}
