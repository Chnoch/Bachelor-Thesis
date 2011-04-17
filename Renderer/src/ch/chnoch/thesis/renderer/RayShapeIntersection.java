package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;

import ch.chnoch.thesis.renderer.interfaces.Node;

import ch.chnoch.thesis.renderer.interfaces.Node;

public class RayShapeIntersection {
	public Vector3f hitPoint;
	public boolean hit;
	public Node node;
	
	public RayShapeIntersection() {
		hitPoint = null;
		hit = false;
		node = null;
	}
	
	public RayShapeIntersection(boolean hit, Vector3f hitPoint, Node shape) {
		this.hit = hit;
		this.hitPoint = hitPoint;
		this.node = shape;
	}
}