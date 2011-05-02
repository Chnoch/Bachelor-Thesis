package ch.chnoch.thesis.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.vecmath.Matrix4f;

import android.util.Log;

import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.util.Util;

public class GraphSceneManager implements SceneManagerInterface {

	private Node mRoot;
	private Camera mCamera;
	private Frustum mFrustum;
	private List<Light> mLights;

	public GraphSceneManager() {
		super();
		this.mCamera = new Camera();
		this.mFrustum = new Frustum();
		mLights = new ArrayList<Light>();
	}

	public Camera getCamera() {
		return this.mCamera;
	}

	public Frustum getFrustum() {
		return this.mFrustum;
	}

	public SceneManagerIterator iterator() {
		return new GraphSceneIterator(this);
	}
	
	public Iterator<Light> lightIterator() {
		return mLights.iterator();
	}

	public void setRoot(Node root) {
		this.mRoot = root;
	}

	public Node getRoot() {
		return this.mRoot;
	}
	
	public void addLight(Light light) {
		if (mLights.size()<=8) {
			mLights.add(light);
		}
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
	
	public RayShapeIntersection intersectRayNode(Ray ray) {
		SceneManagerIterator it = this.iterator();
		Node node;
		while (it.hasNext()) {
			RenderItem item = it.next();
			node = item.getNode();

			RayShapeIntersection intersection = node.intersect(ray);
			if (intersection.hit) {
				intersection.node = item.getNode();
				return intersection;
			}
		}

		return new RayShapeIntersection();
	}

}
