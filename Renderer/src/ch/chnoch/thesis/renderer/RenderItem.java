package ch.chnoch.thesis.renderer;

import javax.vecmath.*;

import ch.chnoch.thesis.renderer.interfaces.Node;

// TODO: Auto-generated Javadoc
/**
 * A data structure that contains a shape and its transformation.
 * Its purpose is to pass data from the scene manager to the 
 * renderer via the {@link SceneManagerIterator}.
 */
public class RenderItem {

	/** The m shape. */
	private Node mShape;
	
	/** The t. */
	private Matrix4f t;
	
	/**
	 * Instantiates a new render item.
	 *
	 * @param shape the shape
	 * @param t the t
	 */
	public RenderItem(Node shape, Matrix4f t)
	{
		this.mShape = shape;
		this.t = t;
	}
	
	/**
	 * Gets the node.
	 *
	 * @return the node
	 */
	public Node getNode()
	{
		return mShape;
	}
	
	/**
	 * Gets the t.
	 *
	 * @return the t
	 */
	public Matrix4f getT()
	{
		return t;
	}
	
}
