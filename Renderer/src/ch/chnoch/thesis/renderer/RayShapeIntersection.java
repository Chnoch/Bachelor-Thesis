package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;

/**
 * This is a data structure to hold information about whether an intersection
 * between a ray and a shape occured and if yes where the hit point is.
 */
public class RayShapeIntersection {
	
	/** The hit point between the shape and the ray. */
	public Vector3f hitPoint;
	
	/** A boolean that indicates whether a hit actually occurred. */
	public boolean hit;
	
	/** The node that holds the associated shape. */
	public Node node;
	
	/**
	 * Instantiates a new ray shape intersection.
	 */
	public RayShapeIntersection() {
		hitPoint = null;
		hit = false;
		node = null;
	}

	/**
	 * Instantiates a new ray shape intersection.
	 * 
	 * @param hit
	 *            A boolean indicating whether a hit occurred
	 * @param hitPoint
	 *            the hit point between the ray and the shape
	 * @param shape
	 *            the node that holds the associated shape.
	 */
	public RayShapeIntersection(boolean hit, Vector3f hitPoint, Node shape) {
		this.hit = hit;
		this.hitPoint = hitPoint;
		this.node = shape;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (hit) {
			return "Hitpoint: " + hitPoint.toString() + " on Node: " + node.toString(); 
		} else {
			return "No hit";
		}
	}
}