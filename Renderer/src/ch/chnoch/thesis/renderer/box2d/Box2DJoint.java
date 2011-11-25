package ch.chnoch.thesis.renderer.box2d;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

public class Box2DJoint {
	private Joint mJoint;
	private MouseJointDef mJointDef;

	public Box2DJoint(Box2DBody body1, Box2DWorld world) {
		mJointDef = new MouseJointDef();
		mJointDef.body2 = body1.getBody();
		mJointDef.body1 = world.getGroundBody().getBody();
		mJointDef.collideConnected = true;
		mJointDef.type = JointType.DISTANCE_JOINT;
//		mJointDef.localAnchor1 = new Vec2(body1.getCurrentPosition().x, body1.getCurrentPosition().y);
//		mJointDef.localAnchor2 = new Vec2(world.getGroundBody().getCurrentPosition().x, world.getGroundBody().getCurrentPosition().y);
//		mJointDef.length = mJointDef.localAnchor1.sub(mJointDef.localAnchor2).length();
		mJoint = world.createJoint(this);
	}

	public void update(Vec2 newTarget) {
		if (mJoint instanceof MouseJoint) {
			((MouseJoint) mJoint).setTarget(newTarget);
		}
//		mJointDef.localAnchor1 = newTarget;
	}

	JointDef getJointDef() {
		return mJointDef;
	}

}
