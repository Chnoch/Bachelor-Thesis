package ch.chnoch.thesis.renderer;
import javax.vecmath.*;

/**
 * Represents a 3D shape. The shape currently just consists
 * of its vertex data. It should later be extended to include
 * material properties, shaders, etc.
 */
public class Shape {
    
    private Material material;

	/**
	 * Make a shape from {@link VertexData}.
	 *  
	 * @param vertexData the vertices of the shape.
	 */
	public Shape(VertexData vertexData)
	{
		this.vertexData = vertexData;
		t = new Matrix4f();
		t.setIdentity();
	}
	
	public VertexData getVertexData()
	{
		return vertexData;
	}
	
	public void setTransformation(Matrix4f t)
	{
		this.t = t;
	}
	
	public Matrix4f getTransformation()
	{
		return t;
	}
	
	/**
	 * To be implemented in the "Textures and Shading" project.
	 */
	public void setMaterial(Material material)
	{
	    this.material = material;
	}

	/**
	 * To be implemented in the "Textures and Shading" project.
	 */
	public Material getMaterial()
	{
		return this.material;
	}

	private VertexData vertexData;
	private Matrix4f t;
}
