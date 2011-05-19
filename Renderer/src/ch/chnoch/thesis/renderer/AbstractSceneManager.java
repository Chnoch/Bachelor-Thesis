package ch.chnoch.thesis.renderer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2f;

import ch.chnoch.thesis.renderer.box2d.*;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

public abstract class AbstractSceneManager implements SceneManagerInterface {
	protected Camera mCamera;
	protected Frustum mFrustum;
	protected List<Light> mLights;
	protected Box2DWorld mWorld;
	
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
		Vector2f low = new Vector2f(-100,-100);
		Vector2f high = new Vector2f(100,100);
		Vector2f gravity = new Vector2f(0,-10);
		
		mWorld = new Box2DWorld(low, high, gravity);
		
		SceneManagerIterator it = this.iterator();
		while (it.hasNext()) {
			it.next().getNode().enablePhysicsProperties(mWorld);
		}
	}
		
	@Override
	public void updateScene() {
		float dt = 1f/60f;
		int iterations = 8;
		// update the world
		mWorld.step(dt, iterations);
		// reflect the updated values onto the Nodes
		SceneManagerIterator it = this.iterator();
		while (it.hasNext()) {
			it.next().getNode().updatePhysics();;
		}
	}

	@Override
	public Box2DWorld getPhysicsWorld() {
		return mWorld;
	}
	
	/**
	 * These methods do nothing except if the scene manager is a graph
	 */
	public void setRoot(Node root){}
	public Node getRoot(){return null;}
}
