package ch.chnoch.thesis.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

// TODO: Auto-generated Javadoc
/**
 * The Class VertexBuffers.
 */
public class VertexBuffers {

	/** The m vertex buffer. */
	private FloatBuffer mVertexBuffer;

	/** The m color buffer. */
	private FloatBuffer mColorBuffer;

	/** The m index buffer. */
	private ShortBuffer mIndexBuffer;

	/** The m tex coords buffer. */
	private FloatBuffer mTexCoordsBuffer;

	/** The m normal buffer. */
	private FloatBuffer mNormalBuffer;

	/**
	 * Instantiates a new vertex buffers.
	 */
	public VertexBuffers() {
		super();
	}

	/**
	 * Gets the vertex buffer.
	 * 
	 * @return the vertex buffer
	 */
	public FloatBuffer getVertexBuffer() {
		return mVertexBuffer;
	}

	/**
	 * Sets the vertex buffer.
	 * 
	 * @param vertices
	 *            the new vertex buffer
	 */
	public void setVertexBuffer(float[] vertices) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
	}

	/**
	 * Sets the vertex buffer.
	 * 
	 * @param vertices
	 *            the new vertex buffer
	 */
	public void setVertexBuffer(int[] vertices) {
		int[] vertexValues = new int[vertices.length];

		for (int i = 0; i < vertices.length; i++) {
			vertexValues[i] = (int) vertices[i];
		}

		setVertexBuffer(vertexValues);
	}

	/**
	 * Gets the color buffer.
	 * 
	 * @return the color buffer
	 */
	public FloatBuffer getColorBuffer() {
		return mColorBuffer;
	}

	/**
	 * Sets the color buffer.
	 * 
	 * @param colors
	 *            the new color buffer
	 */
	public void setColorBuffer(float[] colors) {
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
	}

	/**
	 * Gets the index buffer.
	 * 
	 * @return the index buffer
	 */
	public ShortBuffer getIndexBuffer() {
		return mIndexBuffer;
	}

	/**
	 * Sets the index buffer.
	 * 
	 * @param indices
	 *            the new index buffer
	 */
	public void setIndexBuffer(short[] indices) {
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
	}

	/**
	 * Sets the index buffer.
	 * 
	 * @param indices
	 *            the new index buffer
	 */
	public void setIndexBuffer(int[] indices) {
		short[] shortValues = new short[indices.length];
		for (int i = 0; i < indices.length; i++) {
			shortValues[i] = Integer.valueOf(indices[i]).shortValue();
		}

		setIndexBuffer(shortValues);
	}

	/**
	 * Sets the tex coords buffer.
	 * 
	 * @param texCoordsArray
	 *            the new tex coords buffer
	 */
	public void setTexCoordsBuffer(float[] texCoordsArray) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(texCoordsArray.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mTexCoordsBuffer = vbb.asFloatBuffer();
		mTexCoordsBuffer.put(texCoordsArray);
		mTexCoordsBuffer.position(0);
	}

	/**
	 * Gets the tex coords buffer.
	 * 
	 * @return the tex coords buffer
	 */
	public FloatBuffer getTexCoordsBuffer() {
		return mTexCoordsBuffer;
	}

	/**
	 * Sets the normal buffer.
	 * 
	 * @param normals
	 *            the new normal buffer
	 */
	public void setNormalBuffer(float[] normals) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(normals.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mNormalBuffer = vbb.asFloatBuffer();
		mNormalBuffer.put(normals);
		mNormalBuffer.position(0);
	}

	/**
	 * Sets the normal buffer.
	 * 
	 * @param normals
	 *            the new normal buffer
	 */
	public void setNormalBuffer(int[] normals) {
		int[] normalValues = new int[normals.length];

		for (int i = 0; i < normals.length; i++) {
			normalValues[i] = (int) normals[i];
		}

		setNormalBuffer(normalValues);
	}

	/**
	 * Gets the normal buffer.
	 * 
	 * @return the normal buffer
	 */
	public FloatBuffer getNormalBuffer() {
		return mNormalBuffer;
	}
}
