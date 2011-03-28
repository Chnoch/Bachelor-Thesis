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
		return new Matrix4f(1, 0, 0, 0, 
							0, 1, 0, 0, 
							0, 0, 1, 0, 
							0, 0, 0, 1);
	}

	public static Shape loadCube(float scale) {
		VertexBuffers vertexBuffer = new VertexBuffers();
		vertexBuffer.setColorBuffer(colors);
		vertexBuffer.setIndexBuffer(indices);
		float vertexArray[] = new float[vertices.length];
		
		for (int i = 0; i < vertexArray.length; i++) {
			vertexArray[i] = vertices[i]*scale;
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

	
	
	
	public static RayBoxIntersection unproject(float x, float y,
			RenderContext renderer) {
		
		Matrix4f staticMatrix = createMatrices(renderer);
		SceneManagerInterface sceneManager = renderer.getSceneManager();
		RenderItem item;
		Matrix4f inverse;

		SceneManagerIterator it = sceneManager.iterator();
		while (it.hasNext()) {
			Point3f w1 = new Point3f(x, y, 1);
			Point3f w2 = new Point3f(x, y, -1);
			item = it.next();
			inverse = new Matrix4f(staticMatrix);
			inverse.mul(item.getT());
			try {
				inverse.invert();

				Util.transform(inverse, w1);
				Util.transform(inverse, w2);
				RayBoxIntersection inter = intersectRay(w1, w2, item);
				if (inter.hit) {
					return inter;
				}
			} catch (RuntimeException exc) {
				// Matrix not invertable, therefore no action.
				Log.e("UNPROJECT", "Matrix can't be inverted");
			}
		}

		return new RayBoxIntersection();
	}


	public static RayBoxIntersection unprojectOnTrackball(float x, float y,
			Trackball mTrackball, RenderContext mRenderer) {
		Log.d("Util", "unprojectOnTrackball called");

		Vector3f vector = new Vector3f(x, y, 1);
		Matrix4f matrix = createMatrices(mRenderer);

		matrix.transform(vector);

		vector = mTrackball.projectOnTrackball(vector);

		return new RayBoxIntersection(true, vector, mTrackball.getNode());
	}

	private static RayBoxIntersection intersectRay(Point3f w1, Point3f w2,
			RenderItem item) {
		Node node = item.getNode();
		Matrix4f trans = item.getT();
		trans.invert();
		
		Vector3f origin = new Vector3f(w1);
		Vector3f direction = new Vector3f(w2);
		direction.sub(origin);
		Ray ray = new Ray(origin, direction);
//		Log.d("RayOrigin", origin.toString());
//		Log.d("RayDirection", direction.toString());
		
		BoundingBox box = node.getBoundingBox();
		
//		Log.d("BoundingBox", "low: " + box.getLow().toString() + " high: " + box.getHigh().toString());
//		BoundingBox box = node.getBoundingBox().transform(trans);
		
		
		RayBoxIntersection intersection = box.intersect(ray);
		if (intersection.hit) {
//			Log.d("intersectRay", "Hit Box with BB Low: " + box.getLow().toString() + " and High: " + box.getHigh().toString());
			intersection.node = node;
			return intersection;
		} else {
//			Log.d("intersectRay", "No Box hit");
			return intersection;
		}
	}

	private static Matrix4f createMatrices(RenderContext renderer) {
		SceneManagerInterface sceneManager = renderer.getSceneManager();
		Camera camera = sceneManager.getCamera();
		Frustum frustum = sceneManager.getFrustum();

		Log.d("Util", "Viewport: " + renderer.getViewportMatrix().toString());
		Log.d("Util", "Frustum: " + frustum.getProjectionMatrix().toString());
		Log.d("Util", "Camera: " + camera.getCameraMatrix().toString());
		Matrix4f staticMatrix = new Matrix4f(renderer.getViewportMatrix());
		staticMatrix.mul(frustum.getProjectionMatrix());
		staticMatrix.mul(camera.getCameraMatrix());
		
		return staticMatrix;
	}
	
	public static void transform(Matrix4f m, Point3f point)
    {
        float  x, y, z, w;
        x = m.m00*point.x + m.m01*point.y + m.m02*point.z + m.m03;
        y = m.m10*point.x + m.m11*point.y + m.m12*point.z + m.m13;
        z = m.m20*point.x + m.m21*point.y + m.m22*point.z + m.m23;
        w = m.m30*point.x + m.m31*point.y + m.m32*point.z + m.m33;
        point.x = x/w;
        point.y = y/w;
        point.z = z/w;
        
    }
}
