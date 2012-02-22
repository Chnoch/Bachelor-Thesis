package ch.chnoch.thesis.renderer;

// TODO: Auto-generated Javadoc
/**
 * An iterator to traverse scenes. It returns objects of type {@link RenderItem}, 
 * which bundle information about shapes and their transformations.
 */
public interface SceneManagerIterator {

	/**
	 * Checks for next.
	 *
	 * @return true, if successful
	 */
	public boolean hasNext();
	
	/**
	 * Next.
	 *
	 * @return the render item
	 */
	public RenderItem next();
}
