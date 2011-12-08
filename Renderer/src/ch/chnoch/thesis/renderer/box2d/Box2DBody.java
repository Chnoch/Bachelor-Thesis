package ch.chnoch.thesis.renderer.box2d;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2f;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import android.util.Log;

public class Box2DBody {
	private Body mBody;
	private BodyDef mBodyDef;
	private Vector2f mPreviousPosition;

	private Box2DWorld mWorld;
	private List<Box2DJoint> mJointList;
	private Box2DJoint mJoint;
	private Box2DShape mShape;

	private boolean mCreateJoint;

	public Box2DBody(Vector2f position, Box2DWorld world, Box2DShape shape,
			boolean hasMass, boolean createJoint) {
		mPreviousPosition = position;
		mCreateJoint = createJoint;
		mWorld = world;
		mShape = shape;
		mJointList = new ArrayList<Box2DJoint>();

		mBodyDef = new BodyDef();
		mBodyDef.position.set(position.x, position.y);
		if (hasMass) {
			mBodyDef.type = BodyType.DYNAMIC;
		} else {
			mBodyDef.type = BodyType.STATIC;
		}
		mBody = world.createBody(this);
		createShape(shape, hasMass);
	}

	public Vector2f getCurrentPosition() {
		Vec2 pos = mBody.getPosition();
		return new Vector2f(pos.x, pos.y);
	}

	public Vector2f getPreviousPosition() {
		return new Vector2f(mPreviousPosition);
	}

	public void setPreviousPosition(Vector2f pos) {
		mPreviousPosition.set(pos);
	}

	public void move(float x, float y) {
		if (mCreateJoint) {
			if (mJoint == null) {
				float targetX = getCurrentPosition().x + x;
				float targetY = getCurrentPosition().y + y;
				Vec2 target = new Vec2(targetX, targetY);
				mJoint = new Box2DJoint(this, mWorld, target);
				mJointList.add(mJoint);
			} else {
				mJoint.update(x, y);
			}
		}
	}

	public void removeJoint() {
		for (Box2DJoint joint : mJointList) {
			joint.remove();
		}
		mJointList.clear();
		mJoint = null;
	}

	public void setType(TType type) {
		BodyType typeInt;
		switch (type) {
		case DYNAMIC:
			typeInt = BodyType.DYNAMIC;
			break;
		case STATIC:
			typeInt = BodyType.STATIC;
			break;
		default:
			typeInt = BodyType.DYNAMIC;
			break;
		}
		mBody.m_type = typeInt;
	}

	public TType getType() {
		switch (mBody.m_type) {
		case DYNAMIC:
			return TType.DYNAMIC;
		case STATIC:
			return TType.STATIC;
		default:
			return TType.DYNAMIC;
		}
	}

	public void setLinearVelocity(float x, float y) {
		mBody.setLinearVelocity(new Vec2(x, y));
	}

	public float getAngle() {
		return mBody.getAngle();
	}

	public void createShape(Box2DShape shape, boolean hasMass) {
		if (hasMass) {
			mBody.createFixture(shape.getFixtureDef());
		} else {
			mBody.createFixture(shape.getShape(), 0);
		}
	}
	
	public Box2DShape getShape() {
		return mShape;
	}

	/*
	 * Package Scope
	 */

	BodyDef getDefinition() {
		return mBodyDef;
	}

	Body getBody() {
		return mBody;
	}

	public enum TType {
		STATIC, DYNAMIC
	}
}
