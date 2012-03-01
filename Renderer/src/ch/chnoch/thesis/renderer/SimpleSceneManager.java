package ch.chnoch.thesis.renderer;

import java.util.LinkedList;
import java.util.ListIterator;

// TODO: Auto-generated Javadoc
/**
 * A simple scene manager that stores objects in a linked list.
 */
public class SimpleSceneManager extends AbstractSceneManager {

	/** The m shapes. */
	private LinkedList<Shape> mShapes;

	/**
	 * Instantiates a new simple scene manager.
	 */
	public SimpleSceneManager() {
		super();
		mShapes = new LinkedList<Shape>();
	}

	/**
	 * Adds the shape.
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
	 * The Class SimpleSceneManagerItr.
	 */
	private class SimpleSceneManagerItr implements SceneManagerIterator {

		/**
		 * Instantiates a new simple scene manager itr.
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

		/** The itr. */
		ListIterator<Shape> itr;
	}
}
