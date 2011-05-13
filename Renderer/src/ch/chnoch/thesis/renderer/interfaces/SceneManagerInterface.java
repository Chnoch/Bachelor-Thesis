package ch.chnoch.thesis.renderer.interfaces;

import java.util.Iterator;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.box2d.*;

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
	 * @return an iterator to traverse the scene.
	 */
	public SceneManagerIterator iterator();
	
	public Iterator<Light> lightIterator();
	
	public void addLight(Light light);
	
	public RayShapeIntersection intersectRayNode(Ray ray);

	public Camera getCamera();
	
	public Frustum getFrustum();
	
	public void enablePhysicsEngine();
	
	public Box2DWorld getPhysicsWorld();
	
	/**
	 * These methods will only be used, if the SceneManager is a Graph.
	 * @param root
	 */
	public void setRoot(Node root);
	public Node getRoot();
}