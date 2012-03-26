package ch.chnoch.thesis.renderer.interfaces;

import java.util.Iterator;

import ch.chnoch.thesis.renderer.Camera;
import ch.chnoch.thesis.renderer.Frustum;
import ch.chnoch.thesis.renderer.Light;
import ch.chnoch.thesis.renderer.Ray;
import ch.chnoch.thesis.renderer.RayShapeIntersection;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;

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
	 * Creates a {@link SceneManagerIterator} that lets you traverse the scene.
	 * 
	 * @return an iterator to traverse the scene.
	 */
	public SceneManagerIterator iterator();

	/**
	 * Creates an iterator that contains all the lights in the scene, since they
	 * need to be accessed differently.
	 * 
	 * @return the iterator containing the lights.
	 */
	public Iterator<Light> lightIterator();

	/**
	 * Adds a {@link Light} to the scene.
	 * 
	 * @param light
	 *            the light
	 */
	public void addLight(Light light);

	/**
	 * Intersects a {@link Ray} with all the different {@link Node}s in the
	 * scene manager.
	 * 
	 * @param ray
	 *            the ray
	 * @return the data structure that represents a hit point.
	 */
	public RayShapeIntersection intersectRayNode(Ray ray);

	public Camera getCamera();
	
	public Frustum getFrustum();

	/**
	 * Enables the physics engine for the whole scene.
	 */
	public void enablePhysicsEngine();

	/**
	 * Gets the object representing the physics world.
	 * 
	 * @return the physics world
	 */
	public Box2DWorld getPhysicsWorld();

	/**
	 * Sets the object representing the physics world.
	 * 
	 * @param world
	 *            the new physics world
	 */
	public void setPhysicsWorld(Box2DWorld world);

	/**
	 * Destroy all the physics joints that are associated within the different
	 * nodes.
	 */
	public void destroyJoints();

	/**
	 * Updates the whole scene.
	 */
	public void updateScene();

	/**
	 * This method will only be used, if the SceneManager is a Graph. <br>
	 * Sets the root of the graph.
	 * 
	 * @param root
	 *            the new root
	 */
	public void setRoot(Node root);

	/**
	 * This method will only be used, if the SceneManager is a Graph. <br>
	 * Gets the root of the graph
	 * 
	 * @return the root
	 */
	public Node getRoot();
}