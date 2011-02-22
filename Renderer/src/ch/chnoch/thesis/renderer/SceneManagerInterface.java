package ch.chnoch.thesis.renderer;

import java.util.Iterator;

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

	/**
	 * @return an iterator to access the lights in the scene.
	 */
	public Iterator<Light> lightIterator();
	
	public Camera getCamera();
	
	public Frustum getFrustum();
}
