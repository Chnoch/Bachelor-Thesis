package ch.chnoch.thesis.renderer;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

import ch.chnoch.thesis.renderer.box2d.Box2DWorld;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

/**
 * A simple scene manager that stores objects in a linked list.
 */
public class SimpleSceneManager extends AbstractSceneManager {

	private LinkedList<Shape> mShapes;

	public SimpleSceneManager() {
		super();
		mShapes = new LinkedList<Shape>();
	}

	public void addShape(Shape shape) {
		mShapes.add(shape);
	}

	@Override
	public SceneManagerIterator iterator() {
		return new SimpleSceneManagerItr(this);
	}

	private class SimpleSceneManagerItr implements SceneManagerIterator {

		public SimpleSceneManagerItr(SimpleSceneManager sceneManager) {
			itr = sceneManager.mShapes.listIterator(0);
		}

		public boolean hasNext() {
			return itr.hasNext();
		}

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
