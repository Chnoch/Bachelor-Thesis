package ch.chnoch.thesis.renderer.box2d;

import javax.vecmath.Vector2f;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;

/**
 * This is an abstraction for the interaction of the graphics library with the
 * physics engine. The physics engine that is used in the proof of concept
 * applications is Box2D, a 2D physics engine that is independent from any form
 * of rendering. <br>
 * This class represents the physical world, with a ground body, i.e. a ground
 * surface.
 */
public class Box2DWorld {

	/** The Box2D world object. */
	private World mWorld;

	/** The Box2D ground body. */
	private Box2DBody mGroundBody;

	/**
	 * Creates a World, where all the elements will live in.
	 * 
	 * @param low
	 *            the lowest point (x, y) of the world
	 * @param high
	 *            the highest point (x,y) of the world
	 * @param gravity
	 *            the gravity of the world
	 */
	public Box2DWorld(Vector2f low, Vector2f high, Vector2f gravity) {
		mWorld = new World(new Vec2(gravity.x, gravity.y), false);
	}

	/**
	 * Executes a step in the physics simulation. This is done several times a
	 * second to have a constant flow in the simulation. All the new positions
	 * are calculated, taking into account collisions that occur, joints that
	 * have been made, etc.s
	 * 
	 * @param dt
	 *            the amount of time to simulate. Should be constant.
	 * @param velocityIterations
	 *            for the velocity constraint solver
	 * @param positionIterations
	 *            for the position constraint solver
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
	 * Creates a Box2D body and adds it to the world.
	 * 
	 * @param body
	 *            the body
	 * @return the created body
	 */
	Body createBody(Box2DBody body) {
		return mWorld.createBody(body.getDefinition());
	}

	/**
	 * Creates a Box2D joint from the information passed in the Box2DJoint
	 * object.
	 * 
	 * @param joint
	 *            the joint
	 * @return the joint
	 */
	Joint createJoint(Box2DJoint joint) {
		return mWorld.createJoint(joint.getJointDef());
	}

	/**
	 * Destroys a joint in the physics world.
	 * 
	 * @param joint
	 *            the joint to destroy
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
}
