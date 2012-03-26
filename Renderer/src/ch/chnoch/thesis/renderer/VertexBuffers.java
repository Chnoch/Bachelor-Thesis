package ch.chnoch.thesis.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * This class contains information about the properties of a {@link Shape}. All
 * the shapes have one VertexBuffers object that stores the vertices, indices,
 * color coordinates, normals and texture coordinates of this shape. These
 * information can either be generated manually or read from external sources
 * (e.g. from .obj files). <br>
 * The data is stored as Buffers because they can be passed directly to OpenGL.
 */
public class VertexBuffers {

	private FloatBuffer mVertexBuffer;
	private FloatBuffer mColorBuffer;
	private FloatBuffer mNormalBuffer;
	private FloatBuffer mTexCoordsBuffer;

	private ShortBuffer mIndexBuffer;

	/**
	 * Instantiates a new vertex buffers.
	 */
	public VertexBuffers() {
		super();
	}

	/**
	 * Gets the vertices.
	 * 
	 * @return the vertex buffer
	 */
	public FloatBuffer getVertexBuffer() {
		return mVertexBuffer;
	}

	/**
	 * Sets the buffer for the vertices.
	 * 
	 * @param vertices
	 *            an array of floats containing the vertices
	 */
	public void setVertexBuffer(float[] vertices) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
	}

	/**
	 * Sets the buffer for the vertices.
	 * 
	 * @param vertices
	 *            an array of integers containing the vertices
	 */
	public void setVertexBuffer(int[] vertices) {
		int[] vertexValues = new int[vertices.length];

		for (int i = 0; i < vertices.length; i++) {
			vertexValues[i] = (int) vertices[i];
		}

		setVertexBuffer(vertexValues);
	}

	/**
	 * Gets the buffer containing the color coordinates.
	 * 
	 * @return the color buffer
	 */
	public FloatBuffer getColorBuffer() {
		return mColorBuffer;
	}

	/**
	 * Sets the buffer containing the color coordinates.
	 * 
	 * @param colors
	 *            an array containing the color coordinates
	 */
	public void setColorBuffer(float[] colors) {
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
	}

	/**
	 * Gets the buffer containing the indices.
	 * 
	 * @return the index buffer
	 */
	public ShortBuffer getIndexBuffer() {
		return mIndexBuffer;
	}

	/**
	 * Sets the buffer containing the indices.
	 * 
	 * @param indices
	 *            an array containing the indices
	 */
	public void setIndexBuffer(short[] indices) {
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
	}

	/**
	 * Sets the buffer containing the indices.
	 * 
	 * @param indices
	 *            an array containing the indices
	 */
	public void setIndexBuffer(int[] indices) {
		short[] shortValues = new short[indices.length];
		for (int i = 0; i < indices.length; i++) {
			shortValues[i] = Integer.valueOf(indices[i]).shortValue();
		}

		setIndexBuffer(shortValues);
	}

	/**
	 * Sets the buffer containing the texture coordinates.
	 * 
	 * @param texCoordsArray
	 *            an array containing the texture coordinates
	 */
	public void setTexCoordsBuffer(float[] texCoordsArray) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(texCoordsArray.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mTexCoordsBuffer = vbb.asFloatBuffer();
		mTexCoordsBuffer.put(texCoordsArray);
		mTexCoordsBuffer.position(0);
	}

	/**
	 * Gets the buffer containing the texture coordinates.
	 * 
	 * @return the buffer containing the texture coordinates
	 */
	public FloatBuffer getTexCoordsBuffer() {
		return mTexCoordsBuffer;
	}

	/**
	 * Sets the buffer containing the normals.
	 * 
	 * @param normals
	 *            an array containing the normals
	 */
	public void setNormalBuffer(float[] normals) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(normals.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mNormalBuffer = vbb.asFloatBuffer();
		mNormalBuffer.put(normals);
		mNormalBuffer.position(0);
	}

	/**
	 * Sets the buffer containing the normals.
	 * 
	 * @param normals
	 *            an array containing the normals
	 */
	public void setNormalBuffer(int[] normals) {
		int[] normalValues = new int[normals.length];

		for (int i = 0; i < normals.length; i++) {
			normalValues[i] = (int) normals[i];
		}

		setNormalBuffer(normalValues);
	}

	/**
	 * Gets the buffer containing the color coordinates.
	 * 
	 * @return the buffer containing the normals
	 */
	public FloatBuffer getNormalBuffer() {
		return mNormalBuffer;
	}
}
