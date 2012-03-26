package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerIterator;

/**
 * A data structure that contains a shape and its transformation.
 * Its purpose is to pass data from the scene manager to the 
 * renderer via the {@link SceneManagerIterator}.
 */
public class RenderItem {

	private Node mNode;
	
	private Matrix4f mTransformationMatrix;

	/**
	 * Instantiates a new render item.
	 * 
	 * @param shape
	 *            the node that holds the shape that needs to be drawn.
	 * @param t
	 *            the transformation matrix
	 */
	public RenderItem(Node shape, Matrix4f t)
	{
		this.mNode = shape;
		this.mTransformationMatrix = t;
	}

	/**
	 * Gets the node that stores the shape.
	 * 
	 * @return the node
	 */
	public Node getNode()
	{
		return mNode;
	}

	/**
	 * Gets the transformation matrix.
	 * 
	 * @return the transformation matrix
	 */
	public Matrix4f getT()
	{
		return mTransformationMatrix;
	}
	
}
