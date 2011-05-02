package ch.chnoch.thesis.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class VertexBuffers {

	private IntBuffer mVertexBuffer;
	private IntBuffer mColorBuffer;
	private ShortBuffer mIndexBuffer;
	private FloatBuffer mTexCoordsBuffer;
	private IntBuffer mNormalBuffer;

	public VertexBuffers() {
		super();
	}

	public IntBuffer getVertexBuffer() {
		return mVertexBuffer;
	}

	public void setVertexBuffer(int[] vertices) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asIntBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
	}

	public void setVertexBuffer(float[] vertices) {
		int[] vertexValues = new int[vertices.length];

		for (int i = 0; i < vertices.length; i++) {
			vertexValues[i] = (int) vertices[i];
		}

		setVertexBuffer(vertexValues);
	}

	public IntBuffer getColorBuffer() {
		return mColorBuffer;
	}

	public void setColorBuffer(int[] colors) {
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asIntBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
	}

	public ShortBuffer getIndexBuffer() {
		return mIndexBuffer;
	}

	public void setIndexBuffer(short[] indices) {
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
	}

	public void setIndexBuffer(int[] indices) {
		short[] shortValues = new short[indices.length];
		for (int i = 0; i < indices.length; i++) {
			shortValues[i] = Integer.valueOf(indices[i]).shortValue();
		}

		setIndexBuffer(shortValues);
	}

	public void setTexCoordsBuffer(float[] texCoordsArray) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(texCoordsArray.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mTexCoordsBuffer = vbb.asFloatBuffer();
		mTexCoordsBuffer.put(texCoordsArray);
		mTexCoordsBuffer.position(0);
	}

	public FloatBuffer getTexCoordsBuffer() {
		return mTexCoordsBuffer;
	}

	public void setNormalBuffer(int[] normals) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(normals.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mNormalBuffer = vbb.asIntBuffer();
		mNormalBuffer.put(normals);
		mNormalBuffer.position(0);
	}

	public IntBuffer getNormalBuffer() {
		return mNormalBuffer;
	}
}
