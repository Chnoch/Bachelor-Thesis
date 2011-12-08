package ch.chnoch.thesis.renderer.box2d;

import javax.vecmath.Vector2f;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

public class Box2DJoint {
	private MouseJoint mJoint;
	private MouseJointDef mJointDef;
	private Box2DWorld mWorld;

	public Box2DJoint(Box2DBody body1, Box2DWorld world, Vec2 target) {
		mWorld = world;

		mJointDef = new MouseJointDef();
		mJointDef.bodyB = body1.getBody();
		mJointDef.bodyA = world.getGroundBody().getBody();
		mJointDef.collideConnected = true;
		mJointDef.type = JointType.MOUSE;
		mJointDef.target.set(target);
		mJointDef.maxForce = 1000 * body1.getBody().m_mass;
		int i = 0;
		while (mJoint == null) {
			mJoint = (MouseJoint) world.createJoint(this);
			i++;
			if (mJoint == null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void update(float x, float y) {
		if (mJoint instanceof MouseJoint) {
			Vec2 target = mJoint.getTarget();
			Vec2 t = new Vec2(target);
			target.x += x;
			target.y += y;
			mJoint.setTarget(target);
		}
	}

	public void remove() {
		if (mJoint != null) {
			mWorld.destroyJoint(this);
		}
	}

	public Vector2f getTarget() {
		return new Vector2f(mJoint.getTarget().x, mJoint.getTarget().y);
	}

	/*
	 * PACKAGE SCOPE
	 */

	JointDef getJointDef() {
		return mJointDef;
	}

	Joint getJoint() {
		return mJoint;
	}

}
