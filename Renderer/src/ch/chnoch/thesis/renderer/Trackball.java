package ch.chnoch.thesis.renderer;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.Node;

/**
 * A Trackball is used to intuitively rotate an arbitrary object in the world.
 * The trackball is of the approximate size of the corresponding {@link Node}
 * (more precisely of its {@link BoundingBox}. Whenever a user interacts with
 * the screen these interactions are mapped to the trackball that needs to be
 * rotated accordingly. These rotations are then mapped back to the node to
 * produce a realistic and intuitive rotation.
 */
public class Trackball {

	protected Node mNode;

	protected Vector3f mCenter;

	protected float mRadius;

	protected Camera mCamera;

	/**
	 * Instantiates a new trackball.
	 */
	public Trackball() {}

	/**
	 * Sets the corresponding node. Only one Trackball is generally needed at
	 * one time (except if you want to rotate several objects simultaneously
	 * with different angles.
	 * 
	 * @param node
	 *            the new corresponding node
	 */
	public void setNode(Node node) {
		mNode = node;
		BoundingBox box = mNode.getBoundingBox();
		mCenter = box.getCenter();
		mRadius = box.getRadius();
	}

	/**
	 * Gets the corresponding node.
	 * 
	 * @return the node
	 */
	public Node getNode() {
		return mNode;
	}

	/**
	 * Updates the node with a new rotation based on the two last hitpoints of
	 * the trackball.
	 * 
	 * @param target
	 *            the target position on the trackball
	 * @param current
	 *            the current position on the trackball
	 * @param factor
	 *            the factor
	 * @return true, if successful
	 */
	public boolean update(Vector3f target, Vector3f current) {
		if (!target.epsilonEquals(current, 0.005f)) {
			Matrix4f t = mNode.getRotationMatrix();
			AxisAngle4f axisAngle = getAxisAngle(target, current, 1);
			Matrix4f rot = new Matrix4f();
			rot.set(axisAngle);
			rot.mul(t);
			mNode.setRotationMatrix(rot);

			// Update the center and radius
			setNode(mNode);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * A helper method to get the axis and the angle of the rotation.
	 * 
	 * @param target
	 *            the target position of the trackball
	 * @param current
	 *            the current position of the trackball
	 * @param factor
	 *            a factor for the angle
	 * @return the axis angle representation
	 */
	protected AxisAngle4f getAxisAngle(Vector3f target, Vector3f current,
			float factor) {
		target.sub(mCenter);
		current.sub(mCenter);

		Vector3f axisVector = new Vector3f();
		axisVector.cross(target, current);
		axisVector.normalize();
		Log.d("WorldTrackball", "Axis Vector: " + axisVector.toString());

		float angle = current.angle(target) * factor;

		return new AxisAngle4f(axisVector, angle);
	}

	/**
	 * A helper method to project a ray onto the trackball that doesn't directly
	 * intersect it.
	 * 
	 * @param ray
	 *            the ray
	 * @return the ray shape intersection
	 */
	protected RayShapeIntersection projectOnTrackball(Ray ray) {

		Vector3f center = new Vector3f(mCenter);
		Vector3f dir = new Vector3f(ray.getDirection());
		Vector3f denom = new Vector3f(dir);
		center.sub(ray.getOrigin());
		float denominator = denom.dot(dir);
		float t = dir.dot(center) / denominator;

		Vector3f closestPoint = new Vector3f(ray.getDirection());
		closestPoint.scale(t);
		closestPoint.add(ray.getOrigin());

		Vector3f newDirection = new Vector3f(mCenter);
		newDirection.sub(closestPoint);
		newDirection.normalize();
		Ray newRay = new Ray(closestPoint, newDirection);

		return intersect(newRay);
	}

	/**
	 * Intersects this trackball (sphere) with the given ray and returns either
	 * a RayShapeIntersection with .hit set to false or one with the .hitPoint
	 * specified as the point where the ray hits the trackball. If the ray
	 * doesn't directly hit the trackball the closest intersection between the
	 * ray and the trackball is calculated and returned as hit point.
	 * 
	 * @param ray
	 *            the ray
	 * @return the RayShapeIntersection object
	 */
	public RayShapeIntersection intersect(Ray ray) {

		RayShapeIntersection intersection = intersectHelper(ray);
		if (!intersection.hit) {
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
			return intersection;
		} else if (discr > 0.001f) { // zero tolerance
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
			rayParm = -a1;
			intersection.hitPoint = new Vector3f();
			ray.getDirection().scale(rayParm);
			intersection.hitPoint.add(ray.getOrigin(), ray.getDirection());
			intersection.hit = true;
		}
		return intersection;
	}

}