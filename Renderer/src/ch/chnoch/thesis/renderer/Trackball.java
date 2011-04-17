package ch.chnoch.thesis.renderer;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import android.util.Log;

import ch.chnoch.thesis.renderer.interfaces.Node;

public class Trackball {

	private Node mNode;
	private Point3f mCenter;
	private float mRadius;

	public Trackball() {
	}

	public void setNode(Node node) {
		mNode = node;

		mCenter = mNode.getBoundingBox().getCenter();

		mRadius = mNode.getBoundingBox().getRadius();
		
//		float width = mViewer.width();
//		float height = mViewer.height();
//		
//		mCenter = new Vector3f(width/2, height/2, 0);
//		mRadius = (float) Math.sqrt(height*height/4 + width*width/4);
	}
	

	public Node getNode() {
		return mNode;
	}

	public void update(Vector3f cur, Vector3f prev, float factor) {
		Matrix4f t = mNode.getRotationMatrix();
//		t.transform(cur);
//		t.transform(prev);
		
		cur.sub(mCenter);
		prev.sub(mCenter);
		
		Vector3f axisVector = new Vector3f();
		
		
		axisVector.cross(cur, prev);
		axisVector.normalize();
		float angle = prev.angle(cur) * factor;
		Log.d("Trackball", "Axis: " + axisVector.toString());
		Log.d("Trackball", "Angle: " + angle);
		AxisAngle4f axisAngle = new AxisAngle4f(axisVector, angle);
		Matrix4f rot = new Matrix4f();
		rot.set(axisAngle);
//		Log.d("Trackball", "NodeRotation: " + t.toString());
//		Log.d("Trackball", "Rotation: " + rot.toString());
		rot.mul(t);
		mNode.setRotationMatrix(rot);
		
		mCenter = mNode.getBoundingBox().getCenter();
		mRadius = mNode.getBoundingBox().getRadius();
	}
	
	public RayShapeIntersection intersect2(Ray ray) {
		Log.d("Trackball", "Intersect2 called");
		RayShapeIntersection intersection = new RayShapeIntersection();
		intersection.node = mNode;
		Vector3f diff = new Vector3f(ray.getOrigin());
		diff.sub(mCenter);
		Vector3f dir = new Vector3f(ray.getDirection());
		dir.normalize();
		float v = diff.dot(dir);
		
		float disc = mRadius * mRadius - (diff.dot(diff) - v*v);
		
		if (disc < 0) {
			intersection.hit = false;
		} else {
			float d = (float) Math.sqrt(disc);
			dir.scale(v-d);
			Vector3f point = new Vector3f();
			point.add(ray.getOrigin(), dir);
			intersection.hit = true;
			intersection.hitPoint = point;
		}
		
		if (!intersection.hit) {
			Log.d("Trackball", "No hit; Projecting on Trackball");
			intersection = projectOnTrackball(ray);
		}
		
		Log.d("Trackball", "Hit: " +  intersection.hit);
		Log.d("Trackball", "Hitpoint: " + intersection.hitPoint.toString());
		return intersection;
	}
	
	public RayShapeIntersection projectOnTrackball(Ray ray) {

		Vector3f center = new Vector3f(mCenter);
		Log.d("Trackball", "Center: " + center.toString());
		Vector3f dir = new Vector3f(ray.getDirection());
		Log.d("Trackball", "direction: " + dir.toString());
		Vector3f denom = new Vector3f(dir);
		center.sub(ray.getOrigin());
		float denominator = denom.dot(dir);
		Log.d("Trackball", "Denominator: " + denominator);
		float t = dir.dot(center)/denominator;
		Log.d("Trackball", "t: " + t);
		
		Vector3f closestPoint = new Vector3f(ray.getDirection());
		closestPoint.scale(t);
		closestPoint.add(ray.getOrigin());
			
		
		Vector3f newDirection = new Vector3f(mCenter);
		newDirection.sub(closestPoint);
		newDirection.normalize();
		Ray newRay = new Ray(closestPoint, newDirection);
		Log.d("Trackball", "New Ray: " + closestPoint.toString() + ", dir: " + newDirection.toString());
		
		return intersect(newRay);
	}
	

	/**
	 * Intersects this trackball (sphere) with the given ray and returns either
	 * a RayShapeIntersection with .hit set to false or one with the .hitPoint
	 * specified as the point where the ray hits the trackball.
	 * 
	 * @param ray
	 * @return the RayShapeIntersection object
	 */
	public RayShapeIntersection intersect(Ray ray) {
		
		RayShapeIntersection intersection = intersectHelper(ray);
		if (!intersection.hit) {
			Log.d("Trackball", "No hit; Projecting on Trackball");
			intersection = projectOnTrackball(ray);
		}
		return intersection;
	}
	
	private RayShapeIntersection intersectHelper(Ray ray) {
		RayShapeIntersection intersection = new RayShapeIntersection();
		intersection.node = mNode;
		Vector3f diff = new Vector3f(ray.getOrigin());
		diff.sub(mCenter);
		float a0 = diff.dot(diff) - mRadius * mRadius;
		float a1, discr, root, rayParm, rayParm2;

		if (a0 <= 0) {
			// P is inside sphere
			a1 = ray.getDirection().dot(diff);
			discr = a1 * a1 - a0;
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

		discr = a1 * a1 - a0;
		if (discr < 0) {
			Log.d("Trackball", "Below Zero");
			return intersection;
		} else if (discr > 0.001f) { // zero tolerance
			Log.d("Trackball", "Above zero Tolerance");
			root = (float) Math.sqrt(discr);
			rayParm = -a1 - root;
			rayParm2 = -a1 + root;
			intersection.hitPoint = new Vector3f();
			// which one to use??? i don't know...
			if (rayParm < rayParm2) {
				ray.getDirection().scale(rayParm);
			} else {
				ray.getDirection().scale(rayParm2);
			}
			intersection.hitPoint.add(ray.getOrigin(), ray.getDirection());
			intersection.hit = true;
		} else {
			Log.d("Trackball", "Within zero Tolerance");
			rayParm = -a1;
			intersection.hitPoint = new Vector3f();
			ray.getDirection().scale(rayParm);
			intersection.hitPoint.add(ray.getOrigin(), ray.getDirection());
			intersection.hit = true;
		}
		Log.d("Trackball", "IntersectionPoint: " + intersection.hitPoint.toString());
		return intersection;
	}

}