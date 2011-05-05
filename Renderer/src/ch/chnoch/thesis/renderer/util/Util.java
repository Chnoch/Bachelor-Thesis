package ch.chnoch.thesis.renderer.util;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import android.util.Log;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.RenderContext;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

public class Util {

	/**
	 * Creates a primitive type float array from a List of reference Float type
	 * values
	 * 
	 * @param list
	 *            The List with the Float values
	 * @return a float array
	 */
	public static float[] floatListToArray(List<Float> list) {
		float[] floatArray = new float[list.size()];
		for (int i = 0; i < list.size(); i++) {
			floatArray[i] = list.get(i);
		}

		return floatArray;
	}

	/**
	 * Creates a primitive type float array from a List of reference Float type
	 * values
	 * 
	 * @param list
	 *            The List with the Float values
	 * @return a float array
	 */
	public static int[] intListToArray(List<Integer> list) {
		int[] floatArray = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			floatArray[i] = list.get(i);
		}

		return floatArray;
	}

	/**
	 * Creates a new Identity Matrix.
	 * 
	 * @return an identity Matrix.
	 */
	public static Matrix4f getIdentityMatrix() {
		return new Matrix4f(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	public static Shape loadCube(float scale) {
		VertexBuffers vertexBuffer = new VertexBuffers();
		vertexBuffer.setColorBuffer(colors);
		vertexBuffer.setIndexBuffer(indices);
		float vertexArray[] = new float[vertices.length];

		for (int i = 0; i < vertexArray.length; i++) {
			vertexArray[i] = vertices[i] * scale;
		}

		float texCoordsArray[] = new float[vertices.length];
		for (int i = 0; i < vertexArray.length; i++) {
			texCoordsArray[i] = vertices[i];
		}

		vertexBuffer.setTexCoordsBuffer(texCoordsArray);
		vertexBuffer.setVertexBuffer(vertexArray);
		vertexBuffer.setNormalBuffer(normals);

		// Make a shape and add the object
		return new Shape(vertexBuffer);
	}

	public static int one = 0x10000;
	static float vertices[] = { -one, -one, -one,
								-one, -one, -one,
								-one, -one, -one,
								
								one, -one, -one,
								one, -one, -one, 
								one, -one, -one,
								
								one, one, -one, 
								one, one, -one,
								one, one, -one,
								
								-one, one, -one, 
								-one, one, -one,
								-one, one, -one,
								
								-one, -one, one, 
								-one, -one, one,
								-one, -one, one,
								
								one, -one, one, 
								one, -one, one,
								one, -one, one,
								
								one, one, one, 
								one, one, one,
								one, one, one,
								
								-one, one, one,
								-one, one, one,
								-one, one, one};

	static int colors[] = { 0, 0, 0, one, one, 0, 0, one, one, one, 0, one, 0,
			one, 0, one, 0, 0, one, one, one, 0, one, one, one, one, one, one,
			0, one, one, one, };

	static int indices_old[] = { 0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7,
			3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6, 5, 3, 0, 1, 3, 1, 2 };

	static int indices[] = { 0, 12, 15, 0, 15, 3, 4, 17, 19, 4, 19, 8, 6, 18, 21, 6, 21,
		9, 10, 22, 13, 10, 13, 2, 14, 23, 20, 14, 20, 16, 11, 1, 5, 11, 5, 7 };
	
	static int normals[] = { 0, -one, 0, 
							0, 0, -one, 
							-one, 0,0,
							
							0, -one, 0,
							one, 0,0,
							0, 0, -one,
							
							0,one,0,
							0,0,-one,
							one,0,0,
							
							0, one, 0,
							-one, 0,0,
							0,0,-one,
							
							0,-one,0,
							-one,0,0,
							0,0,one,
							
							0,-one,0,
							0,0,one,
							one,0,0,
							
							0,one,0,
							one,0,0,
							0,0,one,
							
							0,one,0,
							-one,0,0,
							0,0,one
	};

	public static Ray unproject(float x, float y, RenderContext renderer) {

		Matrix4f staticMatrix = renderer.createMatrices();
		Matrix4f inverse;

		Vector3f origin = new Vector3f(x, y, 1);
		Vector3f direction = new Vector3f(x, y, -1);
		inverse = new Matrix4f(staticMatrix);
		try {
			inverse.invert();

			Util.transform(inverse, origin);
			Util.transform(inverse, direction);

			direction.sub(origin);
			direction.normalize();

			return new Ray(origin, direction);

		} catch (RuntimeException exc) {
			// Matrix not invertable, therefore no action.
			Log.e("UNPROJECT", "Matrix can't be inverted");
		}

		return null;
	}

	public static void transform(Matrix4f m, Vector3f point) {
		float x, y, z, w;
		x = m.m00 * point.x + m.m01 * point.y + m.m02 * point.z + m.m03;
		y = m.m10 * point.x + m.m11 * point.y + m.m12 * point.z + m.m13;
		z = m.m20 * point.x + m.m21 * point.y + m.m22 * point.z + m.m23;
		w = m.m30 * point.x + m.m31 * point.y + m.m32 * point.z + m.m33;
		point.x = x / w;
		point.y = y / w;
		point.z = z / w;

	}
}
