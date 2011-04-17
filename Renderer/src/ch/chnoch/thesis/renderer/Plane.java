package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.Util;

public class Plane {

	private Node mNode;
	private Vector3f pointOnPlane, normal;

	public Plane() {
		pointOnPlane = new Vector3f();
		normal = new Vector3f();
	}

	public void setNode(Node node) {
		mNode = node;
		BoundingBox boundingBox = mNode.getBoundingBox();

		pointOnPlane = new Vector3f(boundingBox.getLow());
		float x = boundingBox.getHigh().x;
		float y = boundingBox.getLow().y;
		float z = boundingBox.getHigh().z;
		Vector3f firstVector = new Vector3f(x, y, z);
		firstVector.sub(firstVector, pointOnPlane);
		y = boundingBox.getHigh().y;
		Vector3f secondVector = new Vector3f(x, y, z);
		secondVector.sub(secondVector, pointOnPlane);

		normal.cross(firstVector, secondVector);
		normal.normalize();
	}

	public RayShapeIntersection intersect(Ray ray) {
		RayShapeIntersection intersection = new RayShapeIntersection();

		// if conditional fails, ray and plane are parallel
		if (normal.dot(ray.getDirection()) != 0) {
			Vector3f dist = new Vector3f();
			dist.sub(pointOnPlane, ray.getOrigin());
			float numerator = normal.dot(dist);
			float denominator = normal.dot(ray.getDirection());

			float s = numerator / denominator;

			Vector3f hitPoint = new Vector3f(ray.getDirection());
			hitPoint.scale(s);
			hitPoint.add(ray.getOrigin());
			
			intersection.hit = true;
			intersection.hitPoint = hitPoint;
		}

		return intersection;
	}

	public void update(Vector3f cur, Vector3f prev) {
		float dx = (cur.x - prev.x);
		float dy = (cur.y - prev.y);

		// translation vector. 3rd dimension??
		Vector3f trans = new Vector3f(dx, dy, 0);

		Matrix4f transMatrix = Util.getIdentityMatrix();
		transMatrix.setTranslation(trans);
		Matrix4f t = mNode.getTranslationMatrix();
		t.mul(transMatrix);
		mNode.setTranslationMatrix(t);
	}
}
