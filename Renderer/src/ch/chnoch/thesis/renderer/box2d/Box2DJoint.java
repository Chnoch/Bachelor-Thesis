package ch.chnoch.thesis.renderer.box2d;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import android.util.Log;

public class Box2DJoint {
	private Joint mJoint;
	private MouseJointDef mJointDef;

	public Box2DJoint(Box2DBody body1, Box2DWorld world) {
		mJointDef = new MouseJointDef();
		mJointDef.body2 = body1.getBody();
		mJointDef.body1 = world.getGroundBody().getBody();
		mJointDef.collideConnected = true;
		mJointDef.type = JointType.MOUSE_JOINT;
		mJointDef.maxForce = 300 * body1.getBody().m_mass;
		mJointDef.target = new Vec2(body1.getCurrentPosition().x, body1.getCurrentPosition().y);
		mJoint = world.createJoint(this);
	}

	public void update(Vec2 newTarget) {
		if (mJoint instanceof MouseJoint) {
			Log.d("Box2DJoint", "new Target: " +newTarget.toString());
			((MouseJoint) mJoint).setTarget(newTarget);
		}
	}

	JointDef getJointDef() {
		return mJointDef;
	}

}
