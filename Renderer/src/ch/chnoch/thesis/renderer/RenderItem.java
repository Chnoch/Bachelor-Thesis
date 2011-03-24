package ch.chnoch.thesis.renderer;

import javax.vecmath.*;

/**
 * A data structure that contains a shape and its transformation.
 * Its purpose is to pass data from the scene manager to the 
 * renderer via the {@link SceneManagerIterator}.
 */
public class RenderItem {

	private Shape mShape;
	private Matrix4f t;
	
	public RenderItem(Shape shape, Matrix4f t)
	{
		this.mShape = shape;
		this.t = t;
	}
	
	public Shape getShape()
	{
		return mShape;
	}
	
	public Matrix4f getT()
	{
		return t;
	}
	
}
