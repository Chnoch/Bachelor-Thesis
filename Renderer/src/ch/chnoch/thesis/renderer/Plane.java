package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;

/**
 * The class Plane is used as a user interaction to move objects. It is only
 * possible to move the objects in two dimensions. The plane is used to
 * appropriately manipulate the position of the objects. They are always updated
 * in respect of the camera, i.e. that the camera specifies how the 2D plane
 * lies in the 3D space. <br>
 * A plane is defined by a point in the 3D space that lies on the plane and by
 * the normal that is associated with the plane.
 */
public class Plane {

	private Node mNode;

	private Vector3f mPointOnPlane, mNormal;

	private Camera mCamera;

	private boolean m2DMode;

	/**
	 * Instantiates a new plane.
	 * 
	 * @param camera
	 *            the camera that specifies how the plane lies in the 3D space
	 */
	public Plane(Camera camera) {
		init(camera);
		m2DMode = false;
	}

	/**
	 * Instantiates a new plane.
	 * 
	 * @param camera
	 *            the camera that specifies how the plane lies in the 3D space
	 * @param mode2d
	 *            specifies whether the plane can be placed arbitrarily in the
	 *            3D space or only on the xy-axis. This is used for the physics
	 *            simulation, where all the objects need to be aligned in one
	 *            plane.
	 */
	public Plane(Camera camera, boolean mode2d) {
		init(camera);
		m2DMode = mode2d;
	}

	/**
	 * Initializes the plane with a point on the plane and a normal associated
	 * to the plane.
	 * 
	 * @param camera
	 *            the camera that specifies how the plane lies in the 3D space
	 */
	private void init(Camera camera) {
		mPointOnPlane = new Vector3f();
		mNormal = new Vector3f();
		mCamera = camera;
		updateNormal();
	}

	/**
	 * Intersects the plane with a ray. This is used to determine how much an
	 * object needs to be translated. There will always be two different rays
	 * shot through the 3D-scene and the intersections with the plane completely
	 * specify the translation.
	 * 
	 * @param ray
	 *            the ray that is to be shot through the scene.
	 * @return a RayShapeIntersecion, a data structure that holds information
	 *         about where a hit between a ray and an object occurred.
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
	 * Updates the node that is currently set for this plane in respect to the
	 * two different hit points that were found by intersecting the rays with
	 * this plane.
	 * 
	 * @param cur
	 *            the target position
	 * @param prev
	 *            the current position
	 */
	public void update(Vector3f target, Vector3f current) {
		float dx = target.x - current.x;
		float dy = target.y - current.y;
		float dz = target.z - current.z;

		mNode.move(new Vector3f(dx, dy, dz));
	}

	/**
	 * Sets the node that this plane will manipulate.
	 * 
	 * @param node
	 *            the new node
	 */
	public void setNode(Node node) {
		mNode = node;
	}

	/**
	 * Gets the node that is currently manipulated.
	 * 
	 * @return the node
	 */
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

	/**
	 * Updates the normal of the plane. This only needs to be done if the 2D
	 * mode is disabled.
	 */
	private void updateNormal() {
		Vector3f centerOfProjection = mCamera.getCenterOfProjection();
		Vector3f lookAtPoint = mCamera.getLookAtPoint();

		mNormal.x = centerOfProjection.x - lookAtPoint.x;
		mNormal.y = centerOfProjection.y - lookAtPoint.y;
		mNormal.z = centerOfProjection.z - lookAtPoint.z;

		mNormal.normalize();
	}

}