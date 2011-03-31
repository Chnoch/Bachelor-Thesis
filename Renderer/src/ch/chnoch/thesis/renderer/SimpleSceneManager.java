package ch.chnoch.thesis.renderer;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

/**
 * A simple scene manager that stores objects in a linked list.
 */
public class SimpleSceneManager implements SceneManagerInterface {

	private LinkedList<Shape> shapes;
	private List<Light> lights;
	private Camera camera;
	private Frustum frustum;
	
	public SimpleSceneManager()
	{
		shapes = new LinkedList<Shape>();
		camera = new Camera();
		frustum = new Frustum();
		lights = new LinkedList<Light>();
	}
	
	public Camera getCamera()
	{
		return camera;
	}
	
	public Frustum getFrustum()
	{
		return frustum;
	}
	
	public void addShape(Shape shape)
	{
		shapes.add(shape);
	}
	
	public SceneManagerIterator iterator()
	{
		return new SimpleSceneManagerItr(this);
	}
	
	/**
	 * To be implemented in the "Textures and Shading" project.
	 */
	public Iterator<Light> lightIterator()
	{
		return lights.iterator();
	}
	
	public void addLight(Light light) {
		if (!(lights.size()>=7)) {
			this.lights.add(light);
		}
	}

	private class SimpleSceneManagerItr implements SceneManagerIterator {
		
		public SimpleSceneManagerItr(SimpleSceneManager sceneManager)
		{
			itr = sceneManager.shapes.listIterator(0);
		}
		
		public boolean hasNext()
		{
			return itr.hasNext();
		}
		
		public RenderItem next()
		{
			Shape shape = itr.next();
			// Here the transformation in the RenderItem is simply the 
			// transformation matrix of the shape. More sophisticated 
			// scene managers will set the transformation for the 
			// RenderItem differently.
			
			//don't use that anymore!!!!!
			return new RenderItem(null, shape.getTransformation());
		}
		
		ListIterator<Shape> itr;
	}
}
