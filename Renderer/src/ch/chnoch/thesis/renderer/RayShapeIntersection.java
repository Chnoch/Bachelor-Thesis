package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class RayShapeIntersection.
 */
public class RayShapeIntersection {
	
	/** The hit point. */
	public Vector3f hitPoint;
	
	/** The hit. */
	public boolean hit;
	
	/** The node. */
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
	 * @param hit the hit
	 * @param hitPoint the hit point
	 * @param shape the shape
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