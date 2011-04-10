package ch.chnoch.thesis.renderer;

import java.math.MathContext;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import android.util.Log;

public class Trackball {

	private float mWidth = 0, mHeight = 0;
	private Node mNode;
	private Vector3f mCenter;
	private float mRadius;

	public Trackball() {
	}

	public void setNode(Node node) {
		mNode = node;
		
		mCenter = mNode.getBoundingBox().getCenter();
		
		Vector3f high = mNode.getBoundingBox().getHigh();
		Vector3f low= mNode.getBoundingBox().getLow();
		float midX = high.x - low.x;
		float midY = high.y - low.y;
		float midZ = high.z - low.z;
		
		mRadius = (float) Math.sqrt(midX * midX + midY * midY + midZ * midZ);
	}

	public Node getNode() {
		return mNode;
	}

	public void setSize(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public void update(Vector3f cur, Vector3f prev, float factor) {
		Vector3f axisVector = new Vector3f();
		axisVector.cross(prev, cur);
		axisVector.normalize();
		float angle = prev.angle(cur) * factor;

		AxisAngle4f axisAngle = new AxisAngle4f(axisVector, angle);
		Quat4f quat = new Quat4f();
		quat.set(axisAngle);
		Matrix4f t = new Matrix4f();
		t = mNode.getTransformationMatrix();
		Matrix4f rot = new Matrix4f();
		rot.set(quat);
		t.mul(rot);
		mNode.setTransformationMatrix(t);
	}

	
	/**
	 * Intersects this trackball (sphere) with the given ray and returns either a 
	 * RayShapeIntersection with .hit set to false or one with the .hitPoint 
	 * specified as the point where the ray hits the trackball.
	 * @param ray
	 * @return the RayShapeIntersection object
	 */
	public RayShapeIntersection intersect(Ray ray) {
		RayShapeIntersection intersection = new RayShapeIntersection();
		intersection.node = mNode;
		Vector3f diff = new Vector3f(ray.getOrigin());
		diff.sub(mCenter);
		float a0 = diff.dot(diff) - mRadius*mRadius;
		float a1, discr, root, rayParm, rayParm2;
		
		if (a0 <= 0) {
			//P is inside sphere
			a1 = ray.getDirection().dot(diff);
			discr = a1*a1 -a0;
			root = (float) Math.sqrt(discr);
			rayParm = -a1 + root;
			intersection.hitPoint = new Vector3f();
			ray.getDirection().scale(rayParm);
			intersection.hitPoint.add(ray.getOrigin(), ray.getDirection());
			intersection.hit = true;
			return intersection;
		}
		
		a1 = ray.getDirection().dot(diff);
		if (a1 > 0) {
			return intersection;
		} 
		
		discr = a1*a1 -a0;
		if (discr < 0) {
			return intersection;
		} else if (discr > 0.001f) { // zero tolerance
			root = (float) Math.sqrt(discr);
			rayParm = -a1 - root;
			rayParm2 = -a1 + root;
			intersection.hitPoint = new Vector3f();
			// which one to use??? i don't know...
			ray.getDirection().scale(rayParm2);
			intersection.hitPoint.add(ray.getOrigin(), ray.getDirection());
			intersection.hit = true;
		}
		return intersection;
	}

}