package ch.chnoch.thesis.renderer.util;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import android.util.Log;

import ch.chnoch.thesis.renderer.*;

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

		vertexBuffer.setVertexBuffer(vertexArray);

		// Make a shape and add the object
		return new Shape(vertexBuffer);
	}

	public static int one = 0x10000;
	static float vertices[] = { -one, -one, -one, one, -one, -one, one, one,
			-one, -one, one, -one, -one, -one, one, one, -one, one, one, one,
			one, -one, one, one, };

	static int colors[] = { 0, 0, 0, one, one, 0, 0, one, one, one, 0, one, 0,
			one, 0, one, 0, 0, one, one, one, 0, one, one, one, one, one, one,
			0, one, one, one, };

	static short indices[] = { 0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2,
			7, 3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6, 5, 3, 0, 1, 3, 1, 2 };

	public static Ray unproject(float x, float y, RenderContext renderer) {

		Matrix4f staticMatrix = createMatrices(renderer);
		Matrix4f inverse;

		Vector3f origin = new Vector3f(x, y, 1);
		Vector3f direction = new Vector3f(x, y, -1);
		inverse = new Matrix4f(staticMatrix);
		try {
			inverse.invert();

			Util.transform(inverse, origin);
			Util.transform(inverse, direction);

			direction.sub(origin);
			return new Ray(origin, direction);

		} catch (RuntimeException exc) {
			// Matrix not invertable, therefore no action.
			Log.e("UNPROJECT", "Matrix can't be inverted");
		}

		return null;
	}

	public static RayShapeIntersection intersectRayBox(Ray ray,
			SceneManagerInterface sceneManager) {
		SceneManagerIterator it = sceneManager.iterator();
		BoundingBox box;
		while (it.hasNext()) {
			RenderItem item = it.next();
			box = item.getNode().getBoundingBox();

			RayShapeIntersection intersection = box.hitPoint(ray);
			if (intersection.hit) {
				intersection.node = item.getNode();
				return intersection;
			}
		}

		return new RayShapeIntersection();
	}

	public static Matrix4f createMatrices(RenderContext renderer) {
		SceneManagerInterface sceneManager = renderer.getSceneManager();
		Camera camera = sceneManager.getCamera();
		Frustum frustum = sceneManager.getFrustum();

		Matrix4f staticMatrix = new Matrix4f(renderer.getViewportMatrix());
		staticMatrix.mul(frustum.getProjectionMatrix());
		staticMatrix.mul(camera.getCameraMatrix());

		return staticMatrix;
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
