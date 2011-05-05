package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.util.Log;

import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.Util;

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

	/*
	 * public void setNode(Node node) { mNode = node; BoundingBox boundingBox =
	 * mNode.getBoundingBox();
	 * 
	 * mPointOnPlane = new Vector3f(boundingBox.getLow()); float x =
	 * boundingBox.getHigh().x; float y = boundingBox.getLow().y; float z =
	 * boundingBox.getLow().z; Vector3f firstVector = new Vector3f(x, y, z);
	 * firstVector.sub(firstVector, mPointOnPlane); y = boundingBox.getHigh().y;
	 * Vector3f secondVector = new Vector3f(x, y, z);
	 * secondVector.sub(secondVector, mPointOnPlane);
	 * 
	 * mNormal.cross(firstVector, secondVector); mNormal.normalize(); }
	 */

	public void setNode(Node node) {
		mNode = node;
	}

	public RayShapeIntersection intersect(Ray ray) {
		RayShapeIntersection intersection = new RayShapeIntersection();

		// if conditional fails, ray and plane are parallel
		float denominator = mNormal.dot(ray.getDirection());
		if (denominator != 0) {
			Vector3f dist = new Vector3f();
			dist.sub(mPointOnPlane, ray.getOrigin());
			float numerator = dist.dot(mNormal);

			float s = numerator / denominator;

			Vector3f hitPoint = new Vector3f(ray.getDirection());
			hitPoint.scale(s);
			hitPoint.add(ray.getOrigin());

			intersection.hit = true;
			intersection.hitPoint = hitPoint;
			
//			Log.d("Plane", "HitPoint: " + intersection.hitPoint.toString());
		}

		return intersection;
	}

	public RayShapeIntersection intersect2(Ray ray) {
		RayShapeIntersection intersection = new RayShapeIntersection();

		float d = -mPointOnPlane.dot(mNormal);

		float numerator = -(ray.getOrigin().dot(mNormal) + d);
		float denominator = ray.getDirection().dot(mNormal);

		if (denominator != 0) {
			float t = numerator / denominator;

			Vector3f hitPoint = new Vector3f();
			hitPoint.scaleAdd(t, ray.getDirection(), ray.getOrigin());
			intersection.hit = true;
			intersection.hitPoint = hitPoint;
		}

		return intersection;
	}

	public void update(Vector3f cur, Vector3f prev) {
		/*
		 * Vector3f x = new Vector3f(); Vector3f y = new Vector3f(); if
		 * (Math.abs(mNormal.x) < Math.abs(mNormal.y)) { if (Math.abs(mNormal.x)
		 * < Math.abs(mNormal.z)) { x.x = 1; } else { x.z = 1; } } else { if
		 * (Math.abs(mNormal.y) < Math.abs(mNormal.z)) { x.y = 1; } else { x.z =
		 * 1; } }
		 * 
		 * x.cross(mNormal, x); y.cross(mNormal, x);
		 */
		Log.d("Plane",
				"Hitpoint cur: " + cur.toString() + " prev: " + prev.toString());
		float dx = cur.x - prev.x;
		float dy = cur.y - prev.y;
		float dz = cur.z - prev.z;

		// translation vector. 3rd dimension??
		Vector3f trans = new Vector3f(dx, dy, dz);

		Matrix4f transMatrix = new Matrix4f();
		transMatrix.setTranslation(trans);
		Matrix4f t = mNode.getTranslationMatrix();
		t.add(transMatrix);
		Log.d("Plane", "Translation Matrix:\n" + t.toString());
		mNode.setTranslationMatrix(t);
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