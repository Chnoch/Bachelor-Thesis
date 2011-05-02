package ch.chnoch.thesis.renderer;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.*;

/**
 * Represents a 3D shape. The shape currently just consists of its vertex data.
 * It should later be extended to include material properties, shaders, etc.
 */
public class Shape {

	private Material mMaterial;
	private VertexBuffers mVertexBuffers;
	private Matrix4f t;
	private BoundingBox mBox;
	private Vector3f mZeroVector;
	private float mEpsilon;

	public Shape(VertexBuffers vertexBuffers) {
		mVertexBuffers = vertexBuffers;
		init();
	}

	private void init() {
		mBox = new BoundingBox(mVertexBuffers.getVertexBuffer());
		t = new Matrix4f();
		t.setIdentity();
		mZeroVector = new Vector3f(0, 0, 0);
		mEpsilon = 0.001f;
	}

	public VertexBuffers getVertexBuffers() {
		return mVertexBuffers;
	}

	public void setTransformation(Matrix4f t) {
		this.t = t;
	}

	public Matrix4f getTransformation() {
		return t;
	}

	public BoundingBox getBoundingBox() {
		return mBox;
	}

	public void setMaterial(Material material) {
		this.mMaterial = material;
	}

	public Material getMaterial() {
		return this.mMaterial;
	}

	public RayShapeIntersection intersect(Ray ray, Matrix4f transformation) {
		RayShapeIntersection intersection = new RayShapeIntersection();

		// get all triangles
		Iterator<Triangle> it = getTriangles().iterator();
		Triangle triangle;
		// calculate intersection of ray and triangle
		while (it.hasNext() && !intersection.hit) {
			triangle = it.next();
			triangle.transform(transformation);
			intersection = calculateIntersection(ray, triangle);
		}

		// return the intersected point if any
		return intersection;
	}

	private List<Triangle> getTriangles() {
		List<Triangle> triangles = new ArrayList<Triangle>();

		IntBuffer verticesBuffer = mVertexBuffers.getVertexBuffer();
		int[] verticesInt = new int[verticesBuffer.capacity()];

		for (int i = 0; i < verticesBuffer.capacity(); i++) {
			verticesInt[i] = verticesBuffer.get(i);
		}

		// Fixed Point Conversion
		float[] vertices = new float[verticesInt.length];
		for (int i = 0; i < verticesInt.length; i++) {
			vertices[i] = (float) verticesInt[i] / 65536;
		}

		ShortBuffer indicesBuffer = mVertexBuffers.getIndexBuffer();
		short[] indices = new short[indicesBuffer.capacity()];
		for (int i = 0; i < indicesBuffer.capacity(); i++) {
			indices[i] = indicesBuffer.get(i);
		}

		Triangle triangle;
		Point3f vec;
		for (int i = 0; i < indices.length; i++) {
			triangle = new Triangle();
			vec = new Point3f();
			vec.x = vertices[indices[i] * 3];
			vec.y = vertices[indices[i] * 3 + 1];
			vec.z = vertices[indices[i] * 3 + 2];
			triangle.mX = vec;
			i++;
			vec = new Point3f();
			vec.x = vertices[indices[i] * 3];
			vec.y = vertices[indices[i] * 3 + 1];
			vec.z = vertices[indices[i] * 3 + 2];
			triangle.mY = vec;
			i++;
			vec = new Point3f();
			vec.x = vertices[indices[i] * 3];
			vec.y = vertices[indices[i] * 3 + 1];
			vec.z = vertices[indices[i] * 3 + 2];
			triangle.mZ = vec;

			triangles.add(triangle);
		}

		return triangles;
	}

	private RayShapeIntersection calculateIntersection(Ray ray,
			Triangle triangle) {
		RayShapeIntersection intersection = new RayShapeIntersection();
		Vector3f u = new Vector3f();
		Vector3f v = new Vector3f();
		u.sub(triangle.mY, triangle.mX);
		v.sub(triangle.mZ, triangle.mX);

		Vector3f n = new Vector3f();
		n.cross(u, v);

		if (n.epsilonEquals(mZeroVector, mEpsilon)) {
			// Triangle is either a point or a line (degenerate)
			// Don't deal with this case
			return intersection;
		}

		Vector3f w0 = new Vector3f();
		w0.sub(ray.getOrigin(), triangle.mX);

		float a = -n.dot(w0);
		float b = n.dot(ray.getDirection());
		if (Math.abs(b) < mEpsilon) { // ray is parallel to triangle plane
			if (a == 0) {
				// ray lies in triangle plane
				return intersection;
			} else {
				// ray disjoint from plane
				return intersection;
			}
		}

		float r = a / b;
		if (r < 0.f) {
			// ray goes away from triangle
			return intersection;
		}

		intersection.hit = true;
		intersection.hitPoint = new Vector3f();
		intersection.hitPoint.scaleAdd(r, ray.getDirection(), ray.getOrigin());

		// Is intersection inside the triangle?
		Vector3f w = new Vector3f();
		w.sub(intersection.hitPoint, triangle.mX);
		float uu, uv, vv, wu, wv, D;
		uu = u.dot(u);
		uv = u.dot(v);
		vv = v.dot(v);
		wu = w.dot(u);
		wv = w.dot(v);
		D = uv * uv - uu * vv;

		// get and test parametric coordinates
		float s, t;
		s = (uv * wv - vv * wu) / D;
		if (s < 0.f || s > 1) {
			// I is outside T
			intersection.hit = false;
			intersection.hitPoint = null;
			return intersection;
		}
		
		t = (uv * wu - uu * wv) / D;
		if (t < 0.0 || (s + t) > 1.0) {
			// I is outside T
			intersection.hit = false;
			intersection.hitPoint = null;
			return intersection;
		}

		return intersection;

	}

	private class Triangle {
		public Point3f mX, mY, mZ;

		public void transform(Matrix4f t) {
			t.transform(mX);
			t.transform(mY);
			t.transform(mZ);
		}
	}

}
