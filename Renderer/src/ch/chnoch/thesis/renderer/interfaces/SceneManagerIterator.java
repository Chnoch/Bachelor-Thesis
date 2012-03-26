package ch.chnoch.thesis.renderer.interfaces;

import ch.chnoch.thesis.renderer.RenderItem;

/**
 * An iterator to traverse scenes. It returns objects of type {@link RenderItem}, 
 * which bundle information about shapes and their transformations.
 */
public interface SceneManagerIterator {

	/**
	 * Checks whether another item exists in the iterator.
	 * 
	 * @return true, if successful
	 */
	public boolean hasNext();

	/**
	 * Returns the next render item
	 * 
	 * @return the render item
	 */
	public RenderItem next();
}
