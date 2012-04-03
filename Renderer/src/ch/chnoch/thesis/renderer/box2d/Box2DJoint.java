package ch.chnoch.thesis.renderer.box2d;

import javax.vecmath.Vector2f;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

/**
 * This class is an abstraction object holding information about a
 * {@link MouseJoint} and its corresponding definition ({@link MouseJointDef}). <br>
 * A joint is used to let users interact with objects. if a joint is created,
 * the object is attracted towards the point where the joint is (i.e. where the
 * user pressed the touchscreen). Once it is released, the joint is destroyed
 * and the objects fall according to gravity.
 */
public class Box2DJoint {

	private MouseJoint mJoint;
	private MouseJointDef mJointDef;

	private Box2DWorld mWorld;

	/**
	 * Instantiates a new box2d joint.
	 * 
	 * @param body
	 *            the body for which a joint needs to be created
	 * @param world
	 *            a reference to the physics world
	 * @param target
	 *            the target position in 2d
	 */
	public Box2DJoint(Box2DBody body, Box2DWorld world, Vec2 target) {
		mWorld = world;

		mJointDef = new MouseJointDef();
		mJointDef.bodyB = body.getBody();
		mJointDef.bodyA = world.getGroundBody().getBody();
		mJointDef.collideConnected = true;
		mJointDef.type = JointType.MOUSE;
		mJointDef.target.set(target);
		mJointDef.maxForce = 1000 * body.getBody().m_mass;

		while (mJoint == null) {
			mJoint = (MouseJoint) world.createJoint(this);
			if (mJoint == null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	/**
	 * Updates the joint to a new target position.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 */
	public void update(float x, float y) {
		if (mJoint instanceof MouseJoint) {
			Vec2 target = mJoint.getTarget();
			target.x += x;
			target.y += y;
			mJoint.setTarget(target);
		}
	}

	/**
	 * Removes the joint from the physics world and the body.
	 */
	public void remove() {
		if (mJoint != null) {
			mWorld.destroyJoint(this);
		}
	}

	/**
	 * Gets the target position of the joint.
	 * 
	 * @return the target
	 */
	public Vector2f getTarget() {
		return new Vector2f(mJoint.getTarget().x, mJoint.getTarget().y);
	}

	/*
	 * PACKAGE SCOPE
	 */

	/**
	 * Gets the joint definition. Includes all the information needed to create
	 * a joint.
	 * 
	 * @return the joint definiton
	 */
	JointDef getJointDef() {
		return mJointDef;
	}

	/**
	 * Gets the joint.
	 * 
	 * @return the joint
	 */
	Joint getJoint() {
		return mJoint;
	}

}
