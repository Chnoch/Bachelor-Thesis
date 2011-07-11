package ch.chnoch.thesis.renderer.interfaces;

import java.io.IOException;

/**
 * Declares the functionality to manage textures.
 */
public interface Texture {

	public void createTexture(int resource) throws IOException;
	
	public void load();
	
	public int getID();
}
