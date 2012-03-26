package ch.chnoch.thesis.renderer;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.box2d.Box2DShape;

/**
 * This class represents a 3D shape. A shape is made up of triangles. The data
 * for this is stored in a {@link VertexBuffers}, where all the vertices, the
 * normals, the texture coordinates, the indices and the color coordinates are
 * stored. These are all used by OpenGL to print a triangle onto the screen. A
 * shape can also have a transformation matrix that can already scale, rotate or
 * translate the shape. <br>
 * <br>
 * A single shape can belong to several nodes. This is used to make the library
 * more efficient. In this case the transformation for a single node should be
 * stored in the node. The transformations are all concatenated before anything
 * is drawn.
 */
public class Shape {

	private VertexBuffers mVertexBuffers;

	private Matrix4f mTransformationMatrix;

	private BoundingBox mBox;

	private Vector3f mZeroVector;

	private float mEpsilon;

	/**
	 * Instantiates a new shape from the data stored in the VertexBuffer.
	 * 
	 * @param vertexBuffers
	 *            the data structure where all the data about the shape is
	 *            stored.
	 */
	public Shape(VertexBuffers vertexBuffers) {
		mVertexBuffers = vertexBuffers;
		init();
	}

	/**
	 * Initializes a new shape from the data in the vertex buffer.
	 */
	private void init() {
		mBox = new BoundingBox(mVertexBuffers.getVertexBuffer());
		mTransformationMatrix = new Matrix4f();
		mTransformationMatrix.setIdentity();
		mZeroVector = new Vector3f(0, 0, 0);
		mEpsilon = 0.001f;
	}

	public VertexBuffers getVertexBuffers() {
		return mVertexBuffers;
	}

	public void setTransformation(Matrix4f transformationMatrix) {
		this.mTransformationMatrix = transformationMatrix;
	}

	public Matrix4f getTransformation() {
		return mTransformationMatrix;
	}

	public BoundingBox getBoundingBox() {
		return mBox.clone();
	}

	/**
	 * Let's you enable the Box2D physics engine for this shape. This creates
	 * the corresponding shape in the Box2D world.
	 * 
	 * @return the physics shape from Box2D
	 */
	Box2DShape enableBox2D() {
		return new Box2DShape(mVertexBuffers.getVertexBuffer());
	}

	/**
	 * Intersect the given Ray with this shape. Tests for every triangle of the
	 * shape. Speed depends hence on the complexity of the shape.
	 * 
	 * @param ray
	 *            the ray
	 * @param transformation
	 *            the transformation matrix for the shape. Depends on the node
	 *            it is stored in and all the parents of this node.
	 * @return a RayShapeIntersection with the coordinates of the HitPoint if
	 *         any.
	 */
	RayShapeIntersection intersect(Ray ray, Matrix4f transformation) {
		RayShapeIntersection intersection = new RayShapeIntersection();
		List<RayShapeIntersection> hitTriangles = new ArrayList<RayShapeIntersection>();

		// get all triangles
		Iterator<Triangle> it = getTriangles().iterator();
		Triangle triangle;
		// calculate intersection of ray and triangle
		while (it.hasNext()) {
			triangle = it.next();
			triangle.transform(transformation);
			intersection = calculateIntersection(ray, triangle);
			if (intersection.hit) {
				hitTriangles.add(intersection);
			}
		}

		Vector3f distance = new Vector3f();
		float shortestDist = Float.MAX_VALUE;
		for (RayShapeIntersection in : hitTriangles) {
			distance.sub(ray.getOrigin(), in.hitPoint);
			if (distance.length() < shortestDist) {
				intersection = in;
				shortestDist = distance.length();
			}
		}

		// return the intersected point if any
		return intersection;
	}

	/**
	 * Gets the triangles of the shape.
	 * 
	 * @return a list of {@link Triangle}
	 */
	private List<Triangle> getTriangles() {
		List<Triangle> triangles = new ArrayList<Triangle>();

		FloatBuffer verticesBuffer = mVertexBuffers.getVertexBuffer();
		float[] verticesInt = new float[verticesBuffer.capacity()];

		for (int i = 0; i < verticesBuffer.capacity(); i++) {
			verticesInt[i] = verticesBuffer.get(i);
		}

		// Fixed Point Conversion
		float[] vertices = new float[verticesInt.length];
		for (int i = 0; i < verticesInt.length; i++) {
			vertices[i] = (float) verticesInt[i];
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

	/*
	 * Private Methods
	 */

	/**
	 * Helper method to calculate the intersection between a ray and a
	 * triangle..
	 * 
	 * @param ray
	 *            the ray
	 * @param triangle
	 *            the triangle
	 * @return the ray shape intersection
	 */
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

	/**
	 * A triangle is represented by three points x, y and z.
	 */
	private class Triangle {

		/** The m z. */
		public Point3f mX, mY, mZ;

		/**
		 * Transforms the triangle with the transformation stored in the matrix.
		 * 
		 * @param transformationMatrix
		 *            the transformation matrix
		 */
		public void transform(Matrix4f transformationMatrix) {
			transformationMatrix.transform(mX);
			transformationMatrix.transform(mY);
			transformationMatrix.transform(mZ);
		}
	}

}
