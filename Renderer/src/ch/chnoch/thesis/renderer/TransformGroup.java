package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.box2d.Box2DBody;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;

/**
 * A TransformGroup is a {@link Group} that has a transformation stored. It can
 * therefore be used to store multiple elements that need to apply the same
 * transformation.
 */
public class TransformGroup extends Group {

	/**
	 * Instantiates a new transform group.
	 */
	public TransformGroup() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getMaterial()
	 */
	public Material getMaterial() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#setMaterial(ch.chnoch.thesis
	 * .renderer.Material)
	 */
	public void setMaterial(Material material) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#intersect(ch.chnoch.thesis.
	 * renderer.Ray)
	 */
	public RayShapeIntersection intersect(Ray ray) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#move(javax.vecmath.Vector3f)
	 */
	public void move(Vector3f v) {
		Matrix4f t = getTranslationMatrix();
		Matrix4f move = new Matrix4f();
		move.setTranslation(v);
		t.add(move);
		setTranslationMatrix(t);
	}

	/**
	 * Rotates the group around the z-axis.
	 * 
	 * @param angle
	 *            the angle
	 */
	public void rotZ(float angle) {
		Matrix4f curRot = getRotationMatrix();
		Matrix4f rot = new Matrix4f();
		rot.rotZ(angle);
		curRot.mul(rot);
		setRotationMatrix(curRot);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#enablePhysicsProperties(ch.
	 * chnoch.thesis.renderer.box2d.Box2DWorld)
	 */
	@Override
	public void enablePhysicsProperties(Box2DWorld world) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#updatePositionFromPhysic()
	 */
	@Override
	public void updatePositionFromPhysic() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getPhysicsProperties()
	 */
	@Override
	public Box2DBody getPhysicsProperties() {
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getCenter()
	 */
	@Override
	public Vector3f getCenter() {
		return null;
	}

}
