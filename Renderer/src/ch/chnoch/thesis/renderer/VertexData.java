package ch.chnoch.thesis.renderer;

import java.util.LinkedList;

/**
 * Provides functionality to specify 3D geometry in the form
 * of triangle meshes. {@link VertexData} consists of a list of 
 * {@link VertexElement}s.
 */
public class VertexData {

	/**
	 * Vertex data semantic can be position, normal, or texture coordinates. 
	 */
	public enum Semantic
	{
		POSITION, NORMAL, TEXCOORD, COLOR
	}

	/**
	 * A vertex element is an array of floats that stores vertex attributes, 
	 * like positions, normals, or texture coordinates. The element stores
	 * the data values, its semantic, and the number of components per item
	 * (for example, a homogeneous vector has four components, or RGB colors
	 * have three components). 
	 */
	public class VertexElement {
		
		public float[] getData()
		{
			return data; 
		}
		
		public Semantic getSemantic()
		{
			return semantic;
		}
		
		public int getNumberOfComponents()
		{
			return nComponents;
		}
		
		private float[] data;
		private Semantic semantic;
		private int nComponents;
	}

	/**
	 * Vertex data consists of a list of vertex elements, and an index array.
	 * The index array contains indices into the vertex data. The indices specify 
	 * how vertices are connected to triangles. I.e., the first three indices
	 * define the three vertices of the first triangle, the second three indices 
	 * the second triangle, etc.  
	 * 
	 * @param n the number of vertices.
	 */
	public VertexData(int n)
	{
		this.n = n;
		vertexElements = new LinkedList<VertexElement>();
	}
	
	public int getNumberOfVertices()
	{
		return n;
	}
	
	public void addElement(float f[], Semantic s, int i)
	{
		if(f.length==n*i) 
		{
			VertexElement vertexElement = new VertexElement();
			vertexElement.data = f;
			vertexElement.semantic = s;
			vertexElement.nComponents = i;
			
			// Make sure POSITION is the last element in the list. This guarantees
			// that rendering works as expected (i.e., vertex attributes are set
			// before the vertex is rendered).
			if(s == Semantic.POSITION)
			{
				vertexElements.addLast(vertexElement);
			} else
			{
				vertexElements.addFirst(vertexElement);
			}
		}	
	}
	
	public void addIndices(int indices[])
	{
		this.indices = indices;
	}
	
	public LinkedList<VertexElement> getElements()
	{
		return vertexElements;
	}
	
	public int[] getIndices()
	{
		return indices;
	}
	
	private int n;
	private int[] indices;
	private LinkedList<VertexElement> vertexElements;
}
