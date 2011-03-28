package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

public class RayBoxIntersection {
	public Vector3f hitPoint;
	public boolean hit;
	public Node node;
	
	public RayBoxIntersection() {
		hitPoint = null;
		hit = false;
		node = null;
	}
	
	public RayBoxIntersection(boolean hit, Vector3f hitPoint, Node shape) {
		this.hit = hit;
		this.hitPoint = hitPoint;
		this.node = shape;
	}
}