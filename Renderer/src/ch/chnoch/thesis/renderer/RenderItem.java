package ch.chnoch.thesis.renderer;

import javax.vecmath.*;

/**
 * A data structure that contains a shape and its transformation.
 * Its purpose is to pass data from the scene manager to the 
 * renderer via the {@link SceneManagerIterator}.
 */
public class RenderItem {

	public RenderItem(Shape shape, Matrix4f t)
	{
		this.shape = shape;
		this.t = t;
	}
	
	public Shape getShape()
	{
		return shape;
	}
	
	public Matrix4f getT()
	{
		return t;
	}
	
	private Shape shape;
	private Matrix4f t;
}
