package ch.chnoch.thesis.renderer.box2d;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2f;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import ch.chnoch.thesis.renderer.ShapeNode;

/**
 * This class represents a body in the Box2D physics world. It contains a
 * {@link Body} object, which is a rigid body as well as a {@link BodyDef}
 * object, that holds all the information necessary to create a rigid body. This
 * is the physical equivalent of a {@link ShapeNode} in the library. If the
 * physics simulation is used, every ShapeNode has an associated Box2DBody.
 */
public class Box2DBody {

	private Body mBody;
	private BodyDef mBodyDef;

	private Vector2f mPreviousPosition;

	private Box2DWorld mWorld;

	/** A list of all the non-destroyed joints. */
	private List<Box2DJoint> mJointList;

	/** The currently active joint */
	private Box2DJoint mJoint;

	private Box2DShape mShape;

	private boolean mCreateJoint;

	/**
	 * Instantiates a new box2 d body.
	 * 
	 * @param position
	 *            the position of the object
	 * @param world
	 *            a reference to the {@link Box2DWorld}
	 * @param shape
	 *            the corresponding {@link Box2DShape} of this body
	 * @param hasMass
	 *            a boolean indicating whether this body has a mass, i.e.
	 *            whether it is affected by gravity
	 * @param createJoint
	 *            Joints are used for user interactions. Disable if a body
	 *            shouldn't be moved.
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
	 * Moves the joint to a new position. This is updated when the user moves a
	 * finger on the screen.
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
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
