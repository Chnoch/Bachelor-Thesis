package ch.chnoch.thesis.renderer.interfaces;

import java.util.Iterator;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.box2d.*;

// TODO: Auto-generated Javadoc
/**
 * An interface declaration for scene managers. Scene managers 
 * need to provide an iterator to traverse through all objects in the
 * scene. The interface does not specify how objects are added
 * to the scene, since this may differ based on the implementation
 * of the interface. Scene managers also need to store a {@link Camera}
 * and a view {@link Frustum}.
 */
public interface SceneManagerInterface {

	/**
	 * Iterator.
	 *
	 * @return an iterator to traverse the scene.
	 */
	public SceneManagerIterator iterator();
	
	/**
	 * Light iterator.
	 *
	 * @return the iterator
	 */
	public Iterator<Light> lightIterator();
	
	/**
	 * Adds the light.
	 *
	 * @param light the light
	 */
	public void addLight(Light light);
	
	/**
	 * Intersect ray node.
	 *
	 * @param ray the ray
	 * @return the ray shape intersection
	 */
	public RayShapeIntersection intersectRayNode(Ray ray);

	/**
	 * Gets the camera.
	 *
	 * @return the camera
	 */
	public Camera getCamera();
	
	/**
	 * Gets the frustum.
	 *
	 * @return the frustum
	 */
	public Frustum getFrustum();
	
	/**
	 * Enable physics engine.
	 */
	public void enablePhysicsEngine();
	
	/**
	 * Gets the physics world.
	 *
	 * @return the physics world
	 */
	public Box2DWorld getPhysicsWorld();
	
	/**
	 * Sets the physics world.
	 *
	 * @param world the new physics world
	 */
	public void setPhysicsWorld(Box2DWorld world);
	
	/**
	 * Destroy joints.
	 */
	public void destroyJoints();

	/**
	 * Update scene.
	 */
	public void updateScene();
	
	/**
	 * These methods will only be used, if the SceneManager is a Graph.
	 *
	 * @param root the new root
	 */
	public void setRoot(Node root);
	
	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public Node getRoot();
}