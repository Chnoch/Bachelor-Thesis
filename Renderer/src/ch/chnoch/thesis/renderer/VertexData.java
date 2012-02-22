package ch.chnoch.thesis.renderer;

import java.util.LinkedList;

// TODO: Auto-generated Javadoc
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
		
		/** The POSITION. */
		POSITION, 
 /** The NORMAL. */
 NORMAL, 
 /** The TEXCOORD. */
 TEXCOORD, 
 /** The COLOR. */
 COLOR
	}

	/**
	 * A vertex element is an array of floats that stores vertex attributes, 
	 * like positions, normals, or texture coordinates. The element stores
	 * the data values, its semantic, and the number of components per item
	 * (for example, a homogeneous vector has four components, or RGB colors
	 * have three components). 
	 */
	public class VertexElement {
		
		/**
		 * Gets the data.
		 *
		 * @return the data
		 */
		public float[] getData()
		{
			return data; 
		}
		
		/**
		 * Gets the semantic.
		 *
		 * @return the semantic
		 */
		public Semantic getSemantic()
		{
			return semantic;
		}
		
		/**
		 * Gets the number of components.
		 *
		 * @return the number of components
		 */
		public int getNumberOfComponents()
		{
			return nComponents;
		}
		
		/** The data. */
		private float[] data;
		
		/** The semantic. */
		private Semantic semantic;
		
		/** The n components. */
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
	
	/**
	 * Gets the number of vertices.
	 *
	 * @return the number of vertices
	 */
	public int getNumberOfVertices()
	{
		return n;
	}
	
	/**
	 * Adds the element.
	 *
	 * @param f the f
	 * @param s the s
	 * @param i the i
	 */
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
	
	/**
	 * Adds the indices.
	 *
	 * @param indices the indices
	 */
	public void addIndices(int indices[])
	{
		this.indices = indices;
	}
	
	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public LinkedList<VertexElement> getElements()
	{
		return vertexElements;
	}
	
	/**
	 * Gets the indices.
	 *
	 * @return the indices
	 */
	public int[] getIndices()
	{
		return indices;
	}
	
	/** The n. */
	private int n;
	
	/** The indices. */
	private int[] indices;
	
	/** The vertex elements. */
	private LinkedList<VertexElement> vertexElements;
}
