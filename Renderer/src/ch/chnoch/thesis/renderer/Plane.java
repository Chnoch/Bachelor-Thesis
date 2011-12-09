package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import android.util.Log;

import ch.chnoch.thesis.renderer.interfaces.Node;

public class Plane {

	private Node mNode;
	private Vector3f mPointOnPlane, mNormal;
	private Camera mCamera;
	private boolean m2DMode;

	public Plane(Camera camera) {
		init(camera);
		m2DMode = false;
	}

	public Plane(Camera camera, boolean mode2d) {
		init(camera);
		m2DMode = mode2d;
	}

	private void init(Camera camera) {
		mPointOnPlane = new Vector3f();
		mNormal = new Vector3f();
		mCamera = camera;
		updateNormal();
	}

	public RayShapeIntersection intersect(Ray ray) {
		if (!m2DMode) {
			updateNormal();
		}
		RayShapeIntersection intersection = new RayShapeIntersection();

		// if conditional fails, ray and plane are parallel
		float denominator = mNormal.dot(ray.getDirection());
		if (denominator != 0 && mPointOnPlane != null) {
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
	
	public void set2DMode(boolean mode) {
		m2DMode = mode;
	}
	
	public boolean get2DMode() {
		return m2DMode;
	}
	
	private void updateNormal() {
		Vector3f centerOfProjection = mCamera.getCenterOfProjection();
		Vector3f lookAtPoint = mCamera.getLookAtPoint();

		Log.d("Plane", "COP: " + centerOfProjection.toString() + " lap: "
				+ lookAtPoint.toString());

		mNormal.x = centerOfProjection.x - lookAtPoint.x;
		mNormal.y = centerOfProjection.y - lookAtPoint.y;
		mNormal.z = centerOfProjection.z - lookAtPoint.z;

		mNormal.normalize();
	}

}