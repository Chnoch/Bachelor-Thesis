package ch.chnoch.thesis.renderer.box2d;

import javax.vecmath.Vector2f;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.joints.Joint;

import android.util.Log;

public class Box2DBody {
	private Body mBox2DBody;
	private BodyDef mBox2DBodyDef;
	private Vector2f mPreviousPosition;
	private Box2DJoint mJoint;

	public Box2DBody(Vector2f position, Box2DWorld world, Box2DShape shape, boolean hasMass, boolean createJoint) {
		mBox2DBodyDef = new BodyDef();
		mBox2DBodyDef.position.set(position.x, position.y);
		mPreviousPosition = position;
		mBox2DBody = world.createBody(this);
		createShape(shape, hasMass);
		if (createJoint) {
			mJoint = new Box2DJoint(this, world);
		}
	}

	public Vector2f getForce() {
		Vec2 force = mBox2DBody.m_force;
		return new Vector2f(force.x, force.y);
	}

	public XForm getTransformation() {
		return mBox2DBody.getXForm();
	}

	public Vector2f getCurrentPosition() {
		Vec2 pos = mBox2DBody.getPosition();
		return new Vector2f(pos.x, pos.y);
	}

	public Vector2f getPreviousPosition() {
		return new Vector2f(mPreviousPosition);
	}

	public void setPreviousPosition(Vector2f pos) {
		mPreviousPosition.set(pos);
	}

	public void move(float x, float y) {
		// mBox2DBody.setLinearVelocity(new Vec2(x,y));
		Log.d("Box2DBody", "move: x: " + x + " y: " + y);
		Log.d("Box2DBody", "CurrentPosition: " + getCurrentPosition().toString());
		mJoint.update(new Vec2(getCurrentPosition().x + x,
				getCurrentPosition().y + y));
	}

	public void setType(TType type) {
		int typeInt = 0;
		switch (type) {
		case DYNAMIC:
			typeInt = Body.e_dynamicType;
			break;
		case STATIC:
			typeInt = Body.e_staticType;
			break;
		default:
			typeInt = Body.e_dynamicType;
			break;
		}
		mBox2DBody.m_type = typeInt;
	}

	public TType getType() {
		switch (mBox2DBody.m_type) {
		case Body.e_dynamicType:
			return TType.DYNAMIC;
		case Body.e_staticType:
			return TType.STATIC;
		default:
			return TType.DYNAMIC;
		}
	}

	public void setDensity(float density) {
		mBox2DBody.m_shapeList.m_density = density;
	}

	public void setFriction(float friction) {
		mBox2DBody.m_shapeList.m_friction = friction;
	}
	
	public void setLinearVelocity(float x, float y) {
		mBox2DBody.setLinearVelocity(new Vec2(x,y));
	}

	public float getAngle() {
		return mBox2DBody.getAngle();
	}

	public void createShape(Box2DShape shape, boolean hasMass) {
		mBox2DBody.createShape(shape.getPolygonDef());
		if (hasMass) {
			setMassFromShapes();
		}
	}

	/*
	 * Package Scope
	 */

	BodyDef getDefinition() {
		return mBox2DBodyDef;
	}

	Body getBody() {
		return mBox2DBody;
	}

	void setMassFromShapes() {
		mBox2DBody.setMassFromShapes();
	}

	public enum TType {
		STATIC, DYNAMIC
	}
}
