package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import android.util.Log;
import ch.chnoch.thesis.renderer.interfaces.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class Plane.
 */
public class Plane {

	/** The m node. */
	private Node mNode;

	/** The m normal. */
	private Vector3f mPointOnPlane, mNormal;

	/** The m camera. */
	private Camera mCamera;

	/** The m2 d mode. */
	private boolean m2DMode;

	/**
	 * Instantiates a new plane.
	 * 
	 * @param camera
	 *            the camera
	 */
	public Plane(Camera camera) {
		init(camera);
		m2DMode = false;
	}

	/**
	 * Instantiates a new plane.
	 * 
	 * @param camera
	 *            the camera
	 * @param mode2d
	 *            the mode2d
	 */
	public Plane(Camera camera, boolean mode2d) {
		init(camera);
		m2DMode = mode2d;
	}

	/**
	 * Inits the.
	 * 
	 * @param camera
	 *            the camera
	 */
	private void init(Camera camera) {
		mPointOnPlane = new Vector3f();
		mNormal = new Vector3f();
		mCamera = camera;
		updateNormal();
	}

	/**
	 * Intersect.
	 * 
	 * @param ray
	 *            the ray
	 * @return the ray shape intersection
	 */
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

	/**
	 * Update.
	 * 
	 * @param cur
	 *            the cur
	 * @param prev
	 *            the prev
	 */
	public void update(Vector3f cur, Vector3f prev) {
		float dx = cur.x - prev.x;
		float dy = cur.y - prev.y;
		float dz = cur.z - prev.z;

		// translation vector. 3rd dimension??
		mNode.move(new Vector3f(dx, dy, dz));
	}

	/**
	 * Sets the node.
	 * 
	 * @param node
	 *            the new node
	 */
	public void setNode(Node node) {
		mNode = node;
	}

	/**
	 * Gets the node.
	 * 
	 * @return the node
	 */
	public Node getNode() {
		return mNode;
	}

	/**
	 * Gets the point on plane.
	 * 
	 * @return the point on plane
	 */
	public Vector3f getPointOnPlane() {
		return mPointOnPlane;
	}

	/**
	 * Sets the point on plane.
	 * 
	 * @param pointOnPlane
	 *            the new point on plane
	 */
	public void setPointOnPlane(Vector3f pointOnPlane) {
		mPointOnPlane = pointOnPlane;
	}

	/**
	 * Gets the normal.
	 * 
	 * @return the normal
	 */
	public Vector3f getNormal() {
		return mNormal;
	}

	/**
	 * Sets the normal.
	 * 
	 * @param normal
	 *            the new normal
	 */
	public void setNormal(Vector3f normal) {
		mNormal = normal;
	}
	
	/**
	 * Sets the 2 d mode.
	 * 
	 * @param mode
	 *            the new 2 d mode
	 */
	public void set2DMode(boolean mode) {
		m2DMode = mode;
	}
	
	/**
	 * Gets the 2 d mode.
	 * 
	 * @return the 2 d mode
	 */
	public boolean get2DMode() {
		return m2DMode;
	}
	
	/**
	 * Update normal.
	 */
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