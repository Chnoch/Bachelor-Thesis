package ch.chnoch.thesis.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexBuffers {

	private IntBuffer mVertexBuffer;
	private IntBuffer mColorBuffer;
	private ByteBuffer mIndexBuffer;

	public VertexBuffers() {

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
		float[] vert = new float[mVertexBuffer.capacity()];
		for (int i = 0; i< mVertexBuffer.capacity(); i++) {
			vert[i] = mVertexBuffer.get();
		}
		mVertexBuffer.position(0);
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

	public ByteBuffer getIndexBuffer() {
		return mIndexBuffer;
	}

	public void setIndexBuffer(byte[] indices) {
		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
	}
}
