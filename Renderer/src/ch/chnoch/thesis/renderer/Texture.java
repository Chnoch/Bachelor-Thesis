package ch.chnoch.thesis.renderer;

import java.io.IOException;

/**
 * Declares the functionality to manage textures.
 */
public interface Texture {

	public void load(String fileName) throws IOException;
}
