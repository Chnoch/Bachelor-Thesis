package ch.chnoch.thesis.renderer.box2d;

import javax.vecmath.Vector2f;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import android.util.Log;

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
		mJoint = (MouseJoint) world.createJoint(this);
	}

	public void update(float x, float y) {
		if (mJoint instanceof MouseJoint) {
			Vec2 target = mJoint.getTarget();
			target.x += x;
			target.y += y;
			mJoint.setTarget(target);
		}
	}

	public void remove() {
		Log.d("Box2DBody", "Called remove Method of Box2DJoint");
		if (mJoint != null) {
			Log.d("Box2DBody", "Joint is not null");
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
