package ch.chnoch.thesis.renderer;

import java.util.List;
import java.util.Stack;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.util.Util;

public class GraphSceneManager implements SceneManagerInterface {

	private Node root;
	private Camera camera;
	private Frustum frustum;

	public GraphSceneManager() {
		this.camera = new Camera();
		this.frustum = new Frustum();
	}

	public Camera getCamera() {
		return this.camera;
	}

	public Frustum getFrustum() {
		return this.frustum;
	}

	public SceneManagerIterator iterator() {
		return new GraphSceneIterator(this);
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public Node getRoot() {
		return this.root;
	}

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
				stack.push(new RenderItem(node.getShape(), t));
			}

			if (node.getChildren() != null) {
				List<Node> children = node.getChildren();

				for (Node child : children) {
//					Matrix4f t = new Matrix4f(node.getTransformationMatrix());
					Matrix4f mat = new Matrix4f(t);
					mat.mul(child.getTransformationMatrix());
//					child.setTransformationMatrix(t);

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
