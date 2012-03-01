package ch.chnoch.thesis.renderer;

import java.util.List;
import java.util.Stack;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class GraphSceneManager.
 */
public class GraphSceneManager extends AbstractSceneManager {

	/** The m root. */
	private Node mRoot;
	
	/**
	 * Instantiates a new graph scene manager.
	 */
	public GraphSceneManager() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.AbstractSceneManager#iterator()
	 */
	public SceneManagerIterator iterator() {
		return new GraphSceneIterator(this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.AbstractSceneManager#setRoot(ch.chnoch.thesis
	 * .renderer.interfaces.Node)
	 */
	@Override
	public void setRoot(Node root) {
		this.mRoot = root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.AbstractSceneManager#getRoot()
	 */
	@Override
	public Node getRoot() {
		return this.mRoot;
	}
	
	/*
	 * The Iterator for the scene.
	 */

	/**
	 * The Class GraphSceneIterator.
	 */
	private class GraphSceneIterator implements SceneManagerIterator {

		/** The root. */
		private Node root;

		/** The stack. */
		private Stack<RenderItem> stack;

		/**
		 * Instantiates a new graph scene iterator.
		 * 
		 * @param manager
		 *            the manager
		 */
		public GraphSceneIterator(GraphSceneManager manager) {
			root = manager.getRoot();
			stack = new Stack<RenderItem>();
			init(root, root.getTransformationMatrix());
		}

		/**
		 * Inits the.
		 * 
		 * @param node
		 *            the node
		 * @param t
		 *            the t
		 */
		private void init(Node node, Matrix4f t) {
			if (node.getShape() != null) {
				stack.push(new RenderItem(node, t));
			}

			if (node.getChildren() != null) {
				List<Node> children = node.getChildren();

				for (Node child : children) {
					Matrix4f mat = new Matrix4f(t);
					mat.mul(child.getTransformationMatrix());
					init(child, mat);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ch.chnoch.thesis.renderer.SceneManagerIterator#hasNext()
		 */
		public boolean hasNext() {
			return !stack.empty();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ch.chnoch.thesis.renderer.SceneManagerIterator#next()
		 */
		public RenderItem next() {
			return stack.pop();
		}

	}
}
