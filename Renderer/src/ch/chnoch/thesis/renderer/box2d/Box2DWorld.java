package ch.chnoch.thesis.renderer.box2d;

import javax.vecmath.Vector2f;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;

import ch.chnoch.thesis.renderer.box2d.Box2DBody.TType;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

// TODO: Auto-generated Javadoc
/**
 * The Class Box2DWorld.
 */
public class Box2DWorld {

	/** The m world. */
	private World mWorld;

	/** The m box2 dsurrounding box. */
	private AABB mBox2DsurroundingBox;

	/** The m scene manager. */
	private SceneManagerInterface mSceneManager;

	/** The m ground body. */
	private Box2DBody mGroundBody;

	/** The m top body. */
	private Box2DBody mTopBody;

	/**
	 * Creates a World, where all the elements will live in.
	 * 
	 * @param low
	 *            the lowest point (x, y) of the world
	 * @param high
	 *            the highest point (x,y) of the world
	 * @param gravity
	 *            the gravity
	 */
	public Box2DWorld(Vector2f low, Vector2f high, Vector2f gravity) {
		mWorld = new World(new Vec2(gravity.x, gravity.y), false);
		// createTopBody();
	}

	/**
	 * Step.
	 * 
	 * @param dt
	 *            the dt
	 * @param velocityIterations
	 *            the velocity iterations
	 * @param positionIterations
	 *            the position iterations
	 */
	public void step(float dt, int velocityIterations, int positionIterations) {
		synchronized (this) {
			mWorld.step(dt, velocityIterations, positionIterations);
		}
	}
	
	/**
	 * Sets the ground body.
	 * 
	 * @param body
	 *            the new ground body
	 */
	public void setGroundBody(Box2DBody body) {
		mGroundBody = body;
	}

	/*
	 * Package Scope
	 */

	/**
	 * Creates the body.
	 * 
	 * @param body
	 *            the body
	 * @return the body
	 */
	Body createBody(Box2DBody body) {
		return mWorld.createBody(body.getDefinition());
	}

	/**
	 * Creates the joint.
	 * 
	 * @param joint
	 *            the joint
	 * @return the joint
	 */
	Joint createJoint(Box2DJoint joint) {
		return mWorld.createJoint(joint.getJointDef());
	}

	/**
	 * Destroy joint.
	 * 
	 * @param joint
	 *            the joint
	 */
	void destroyJoint(Box2DJoint joint) {
		synchronized (this) {
			mWorld.destroyJoint(joint.getJoint());
		}
	}

	/**
	 * Gets the ground body.
	 * 
	 * @return the ground body
	 */
	Box2DBody getGroundBody() {
		return mGroundBody;
	}

	/**
	 * Gets the top body.
	 * 
	 * @return the top body
	 */
	Box2DBody getTopBody() {
		return mTopBody;
	}

	/*
	 * Private Methods
	 */

	/**
	 * Creates the top body.
	 */
	private void createTopBody() {
		Box2DShape topShape = new Box2DShape();
		topShape.setAsBox(10, 1);
		mTopBody = new Box2DBody(new Vector2f(8, 4), this, topShape, false,
				false);
		mTopBody.createShape(topShape, false);
		mTopBody.setType(TType.STATIC);
	}
}
