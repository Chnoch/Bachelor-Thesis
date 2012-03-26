package ch.chnoch.thesis.renderer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.box2d.Box2DWorld;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerIterator;

/**
 * The AbstractSceneManager contains some common tasks and fields of a scene
 * manager. Specific implementations of a scene manager should inherit from this
 * class to take full advantage of what has already been implemented.
 */
public abstract class AbstractSceneManager implements SceneManagerInterface {

	/** The camera. */
	protected Camera mCamera;

	/** The frustum. */
	protected Frustum mFrustum;

	/** The lights. */
	protected List<Light> mLights;

	/** The world. */
	protected Box2DWorld mWorld;
	
	/**
	 * Instantiates a new abstract scene manager.
	 */
	public AbstractSceneManager() {
		mCamera = new Camera();
		mFrustum = new Frustum();
		mLights = new LinkedList<Light>();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#iterator()
	 */
	@Override
	public abstract SceneManagerIterator iterator();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#lightIterator
	 * ()
	 */
	@Override
	public Iterator<Light> lightIterator() {
		return mLights.iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#addLight(ch
	 * .chnoch.thesis.renderer.Light)
	 */
	@Override
	public void addLight(Light light) {
		if (mLights.size()<=8) {
			mLights.add(light);
		}
	}

	/**
	 * Intersects a ray with all the different shapes attached to this
	 * sceneManager.
	 * 
	 * @param ray
	 *            the ray
	 * @return a RayShapeIntersection with the hit point and the node that was
	 *         hit, if any.
	 */
	@Override
	public RayShapeIntersection intersectRayNode(Ray ray) {
		SceneManagerIterator it = this.iterator();
		RayShapeIntersection closestIntersection = new RayShapeIntersection();
		
		Node node;
		while (it.hasNext()) {
			RenderItem item = it.next();
			node = item.getNode();
			
			RayShapeIntersection intersection = node.intersect(ray);
			
			if (intersection.hit) {
				intersection.node = item.getNode();
				if (closestIntersection.hit) {
					Vector3f tempCur = new Vector3f(ray.getOrigin());
					Vector3f tempNew = new Vector3f(ray.getOrigin());
					tempNew.sub(intersection.hitPoint);
					tempCur.sub(closestIntersection.hitPoint);
					if (tempNew.length() < tempCur.length()) {
						closestIntersection = intersection;
					}
				} else {
					closestIntersection = intersection;
				}
			}
		}

		return closestIntersection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#getCamera()
	 */
	@Override
	public Camera getCamera() {
		return mCamera;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#getFrustum()
	 */
	@Override
	public Frustum getFrustum() {
		return mFrustum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#
	 * enablePhysicsEngine()
	 */
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#destroyJoints
	 * ()
	 */
	public void destroyJoints() {
		SceneManagerIterator it = this.iterator();
		while(it.hasNext()) {
			Node node = it.next().getNode();
			if (node instanceof ShapeNode) {
				((ShapeNode) node).destroyJoint();
			}
		}
	}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#updateScene()
	 */
	@Override
	public void updateScene() {
		float dt = 1f/60f;
		int velocityIterations = 8;
		int positionIterations = 3;
		// update the world
		mWorld.step(dt, velocityIterations, positionIterations);
//		Log.d("AbstractSceneManager", "Updated World, now going through scene");
		// reflect the updated values onto the Nodes
		SceneManagerIterator it = this.iterator();
		while (it.hasNext()) {
			it.next().getNode().updatePositionFromPhysic();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#getPhysicsWorld
	 * ()
	 */
	@Override
	public Box2DWorld getPhysicsWorld() {
		return mWorld;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#setPhysicsWorld
	 * (ch.chnoch.thesis.renderer.box2d.Box2DWorld)
	 */
	public void setPhysicsWorld(Box2DWorld world) {
		mWorld = world;
	}
	
	
	/**
	 * These methods do nothing except if the scene manager is a graph.
	 * 
	 * @param root
	 *            the new root
	 */
	public void setRoot(Node root){}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface#getRoot()
	 */
	public Node getRoot(){return null;}
}