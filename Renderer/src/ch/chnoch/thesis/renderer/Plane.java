package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;


import ch.chnoch.thesis.renderer.interfaces.Node;

public class Plane {

	private Node mNode;
	private Vector3f mPointOnPlane, mNormal;

	public Plane() {
		mPointOnPlane = new Vector3f();
		mNormal = new Vector3f();
	}

	public Plane(Vector3f pointOnPlane, Vector3f normal) {
		mPointOnPlane = pointOnPlane;
		normal.normalize();
		mNormal = normal;
	}


	public RayShapeIntersection intersect(Ray ray) {
		RayShapeIntersection intersection = new RayShapeIntersection();

		// if conditional fails, ray and plane are parallel
		float denominator = mNormal.dot(ray.getDirection());
		if (denominator != 0 && mPointOnPlane!=null) {
			Vector3f dist = new Vector3f();
			dist.sub(mPointOnPlane, ray.getOrigin());
			float numerator = dist.dot(mNormal);

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
		float dx = cur.x - prev.x;
		float dy = cur.y - prev.y;
		float dz = cur.z - prev.z;

		// translation vector. 3rd dimension??
		mNode.move(new Vector3f(dx, dy, dz));
	}

	public void setNode(Node node) {
		mNode = node;
	}
	
	public Node getNode() {
		return mNode;
	}
	
	public Vector3f getPointOnPlane() {
		return mPointOnPlane;
	}

	public void setPointOnPlane(Vector3f pointOnPlane) {
		mPointOnPlane = pointOnPlane;
	}

	public Vector3f getNormal() {
		return mNormal;
	}

	public void setNormal(Vector3f normal) {
		mNormal = normal;
	}
}