package ch.chnoch.thesis.renderer;

import java.util.List;
import java.util.Stack;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.Node;

/**
 * This is a possible implementation of how to manage a 3D-scene. A Graph is
 * used to store all the objects. It can contain group nodes and leaf nodes with
 * different properties as children to build up a graph.
 */
public class GraphSceneManager extends AbstractSceneManager {

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
	
	/**
	 * This class implements an iterator over a GraphSceneManager. It transforms
	 * every Node that needs to be drawn to the correct place in the object
	 * space and then returns all the nodes that are to be drawn, so the
	 * renderer can easily iterate over all of them.
	 */
	private class GraphSceneIterator implements SceneManagerIterator {

		private Node root;

		private Stack<RenderItem> stack;

		/**
		 * Instantiates a new iterator.
		 * 
		 * @param manager
		 *            the scene manager where the scene is stored
		 */
		public GraphSceneIterator(GraphSceneManager manager) {
			root = manager.getRoot();
			stack = new Stack<RenderItem>();
			init(root, root.getTransformationMatrix());
		}

		/**
		 * Initializes a node with the correct transformation matrix. This
		 * method is used recursively to correctly apply the transformation of
		 * parent nodes to its children.
		 * 
		 * @param node
		 *            the node that is currently transformed
		 * @param t
		 *            the transformation matrix of the current node
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
