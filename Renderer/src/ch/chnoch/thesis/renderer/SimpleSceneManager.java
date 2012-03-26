package ch.chnoch.thesis.renderer;

import java.util.LinkedList;
import java.util.ListIterator;

import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerIterator;

/**
 * A simple scene manager is a basic implementation of a
 * {@link SceneManagerInterface}. It doesn't have any hierarchy, as it stores
 * all the shapes directly in a simple list. Since it only stores shapes they
 * cannot have any material associated with them. Because of these limitations a
 * SimpleSceneManager should only be used for very basic purposes. Consider
 * using {@link GraphSceneManager} for more advanced use cases.
 */
public class SimpleSceneManager extends AbstractSceneManager {

	private LinkedList<Shape> mShapes;

	/**
	 * Instantiates a new scene manager.
	 */
	public SimpleSceneManager() {
		super();
		mShapes = new LinkedList<Shape>();
	}

	/**
	 * Add a shape to the scene manager
	 * 
	 * @param shape
	 *            the shape
	 */
	public void addShape(Shape shape) {
		mShapes.add(shape);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.AbstractSceneManager#iterator()
	 */
	@Override
	public SceneManagerIterator iterator() {
		return new SimpleSceneManagerItr(this);
	}

	/**
	 * The iterator for the SimpleSceneManager. It traverses the list and
	 * returns all the shapes.
	 */
	private class SimpleSceneManagerItr implements SceneManagerIterator {

		/**
		 * Instantiates a new simple scene manager iterator.
		 * 
		 * @param sceneManager
		 *            the scene manager
		 */
		public SimpleSceneManagerItr(SimpleSceneManager sceneManager) {
			itr = sceneManager.mShapes.listIterator(0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ch.chnoch.thesis.renderer.SceneManagerIterator#hasNext()
		 */
		public boolean hasNext() {
			return itr.hasNext();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ch.chnoch.thesis.renderer.SceneManagerIterator#next()
		 */
		public RenderItem next() {
			Shape shape = itr.next();
			// Here the transformation in the RenderItem is simply the
			// transformation matrix of the shape. More sophisticated
			// scene managers will set the transformation for the
			// RenderItem differently.

			// don't use that anymore!!!!!
			return new RenderItem(null, shape.getTransformation());
		}

		ListIterator<Shape> itr;
	}
}
