package ch.chnoch.thesis.renderer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.chnoch.thesis.renderer.box2d.Box2DWorld;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

public abstract class AbstractSceneManager implements SceneManagerInterface {
	protected Camera mCamera;
	protected Frustum mFrustum;
	protected List<Light> mLights;
	
	public AbstractSceneManager() {
		mCamera = new Camera();
		mFrustum = new Frustum();
		mLights = new LinkedList<Light>();
	}
	
	@Override
	public abstract SceneManagerIterator iterator();

	@Override
	public Iterator<Light> lightIterator() {
		return mLights.iterator();
	}
	
	@Override
	public void addLight(Light light) {
		if (mLights.size()<=8) {
			mLights.add(light);
		}
	}

	/**
	 * Intersects a ray with all the different shapes attached to 
	 * this sceneManager
	 * @param Ray ray, the ray that needs to be intersected
	 * @return a RayShapeIntersection with the hit point and the hit node, if any.
	 */
	@Override
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

	@Override
	public Camera getCamera() {
		return mCamera;
	}

	@Override
	public Frustum getFrustum() {
		return mFrustum;
	}

	@Override
	public void enablePhysicsEngine() {

	}

	@Override
	public Box2DWorld getPhysicsWorld() {
		return null;
	}
	
	/**
	 * These methods do nothing except if the scene manager is a graph
	 */
	public void setRoot(Node root){}
	public Node getRoot(){return null;}
}
