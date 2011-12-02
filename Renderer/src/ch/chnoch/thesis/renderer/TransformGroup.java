package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.box2d.Box2DBody;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.Util;

public class TransformGroup extends Group {

	public TransformGroup() {
		super();
	}

	public Material getMaterial() {
		return null;
	}

	public void setMaterial(Material material) {
		
	}
	
	public RayShapeIntersection intersect(Ray ray) {
		return null;
	}

	public void move(Vector3f v) {
		Matrix4f t = getTranslationMatrix();
		Matrix4f move = new Matrix4f();
		move.setTranslation(v);
		t.add(move);
		setTranslationMatrix(t);
	}

	public void rotZ(float angle) {	}

	@Override
	public void enablePhysicsProperties(Box2DWorld world) {
	}

	@Override
	public void updatePositionFromPhysic() {
	}

	@Override
	public Box2DBody getPhysicsProperties() {
		return null;
	}
	
}
