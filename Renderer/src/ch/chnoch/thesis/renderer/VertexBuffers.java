package ch.chnoch.thesis.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import android.util.Log;

public class VertexBuffers {

	private FloatBuffer mVertexBuffer;
	private FloatBuffer mColorBuffer;
	private ShortBuffer mIndexBuffer;
	private FloatBuffer mTexCoordsBuffer;
	private FloatBuffer mNormalBuffer;

	public VertexBuffers() {
		super();
	}

	public FloatBuffer getVertexBuffer() {
		return mVertexBuffer;
	}

	public void setVertexBuffer(float[] vertices) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
	}

	public void setVertexBuffer(int[] vertices) {
		int[] vertexValues = new int[vertices.length];

		for (int i = 0; i < vertices.length; i++) {
			vertexValues[i] = (int) vertices[i];
		}

		setVertexBuffer(vertexValues);
	}

	public FloatBuffer getColorBuffer() {
		return mColorBuffer;
	}

	public void setColorBuffer(float[] colors) {
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
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

	public void setNormalBuffer(float[] normals) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(normals.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mNormalBuffer = vbb.asFloatBuffer();
		mNormalBuffer.put(normals);
		mNormalBuffer.position(0);
	}

	public void setNormalBuffer(int[] normals) {
		int[] normalValues = new int[normals.length];

		for (int i = 0; i < normals.length; i++) {
			normalValues[i] = (int) normals[i];
		}

		setNormalBuffer(normalValues);
	}

	public FloatBuffer getNormalBuffer() {
		return mNormalBuffer;
	}
}
