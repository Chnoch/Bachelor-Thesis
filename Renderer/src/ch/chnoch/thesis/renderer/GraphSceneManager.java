package ch.chnoch.thesis.renderer;

import java.util.List;
import java.util.Stack;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.box2d.*;
import ch.chnoch.thesis.renderer.interfaces.*;

public class GraphSceneManager extends AbstractSceneManager {

	private Node mRoot;
	
	public GraphSceneManager() {
		super();
	}

	public SceneManagerIterator iterator() {
		return new GraphSceneIterator(this);
	}
	
	@Override
	public void setRoot(Node root) {
		this.mRoot = root;
	}

	@Override
	public Node getRoot() {
		return this.mRoot;
	}
	
	/*
	 * The Iterator for the scene.
	 */

	private class GraphSceneIterator implements SceneManagerIterator {

		private Node root;
		private Stack<RenderItem> stack;

		public GraphSceneIterator(GraphSceneManager manager) {
			root = manager.getRoot();
			stack = new Stack<RenderItem>();
			init(root, root.getTransformationMatrix());
		}

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

		public boolean hasNext() {
			return !stack.empty();
		}

		public RenderItem next() {
			return stack.pop();
		}

	}
}
