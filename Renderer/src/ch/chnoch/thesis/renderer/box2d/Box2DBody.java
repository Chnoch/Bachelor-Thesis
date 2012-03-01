package ch.chnoch.thesis.renderer.box2d;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2f;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

// TODO: Auto-generated Javadoc
/**
 * The Class Box2DBody.
 */
public class Box2DBody {

	/** The m body. */
	private Body mBody;

	/** The m body def. */
	private BodyDef mBodyDef;

	/** The m previous position. */
	private Vector2f mPreviousPosition;

	/** The m world. */
	private Box2DWorld mWorld;

	/** The m joint list. */
	private List<Box2DJoint> mJointList;

	/** The m joint. */
	private Box2DJoint mJoint;

	/** The m shape. */
	private Box2DShape mShape;

	/** The m create joint. */
	private boolean mCreateJoint;

	/**
	 * Instantiates a new box2 d body.
	 * 
	 * @param position
	 *            the position
	 * @param world
	 *            the world
	 * @param shape
	 *            the shape
	 * @param hasMass
	 *            the has mass
	 * @param createJoint
	 *            the create joint
	 */
	public Box2DBody(Vector2f position, Box2DWorld world, Box2DShape shape,
			boolean hasMass, boolean createJoint) {
		mPreviousPosition = position;
		mCreateJoint = createJoint;
		mWorld = world;
		mShape = shape;
		mJointList = new ArrayList<Box2DJoint>();

		mBodyDef = new BodyDef();
		mBodyDef.position.set(position.x, position.y);
		mBodyDef.linearDamping = 0.4f;
		if (hasMass) {
			mBodyDef.type = BodyType.DYNAMIC;
		} else {
			mBodyDef.type = BodyType.STATIC;
		}
		mBody = world.createBody(this);
		createShape(shape, hasMass);
	}

	/**
	 * Gets the current position.
	 * 
	 * @return the current position
	 */
	public Vector2f getCurrentPosition() {
		Vec2 pos = mBody.getPosition();
		return new Vector2f(pos.x, pos.y);
	}

	/**
	 * Gets the previous position.
	 * 
	 * @return the previous position
	 */
	public Vector2f getPreviousPosition() {
		return new Vector2f(mPreviousPosition);
	}

	/**
	 * Sets the previous position.
	 * 
	 * @param pos
	 *            the new previous position
	 */
	public void setPreviousPosition(Vector2f pos) {
		mPreviousPosition.set(pos);
	}

	/**
	 * Move.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
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

	/**
	 * Removes the joint.
	 */
	public void removeJoint() {
		for (Box2DJoint joint : mJointList) {
			joint.remove();
		}
		mJointList.clear();
		mJoint = null;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
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

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
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

	/**
	 * Sets the linear velocity.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void setLinearVelocity(float x, float y) {
		mBody.setLinearVelocity(new Vec2(x, y));
	}

	/**
	 * Gets the angle.
	 * 
	 * @return the angle
	 */
	public float getAngle() {
		return mBody.getAngle();
	}

	/**
	 * Creates the shape.
	 * 
	 * @param shape
	 *            the shape
	 * @param hasMass
	 *            the has mass
	 */
	public void createShape(Box2DShape shape, boolean hasMass) {
		if (hasMass) {
			mBody.createFixture(shape.getFixtureDef());
		} else {
			mBody.createFixture(shape.getShape(), 0);
		}
	}
	
	/**
	 * Gets the shape.
	 * 
	 * @return the shape
	 */
	public Box2DShape getShape() {
		return mShape;
	}

	/*
	 * Package Scope
	 */

	/**
	 * Gets the definition.
	 * 
	 * @return the definition
	 */
	BodyDef getDefinition() {
		return mBodyDef;
	}

	/**
	 * Gets the body.
	 * 
	 * @return the body
	 */
	Body getBody() {
		return mBody;
	}

	/**
	 * The Enum TType.
	 */
	public enum TType {

		/** The STATIC. */
		STATIC,
		/** The DYNAMIC. */
		DYNAMIC
	}
}
