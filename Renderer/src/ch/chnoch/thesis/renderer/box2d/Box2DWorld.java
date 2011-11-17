package ch.chnoch.thesis.renderer.box2d;

import javax.vecmath.Vector2f;

import org.jbox2d.collision.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.Joint;

import ch.chnoch.thesis.renderer.box2d.Box2DBody.TType;
import ch.chnoch.thesis.renderer.interfaces.*;

public class Box2DWorld {

	private World mBox2DWorld;
	private AABB mBox2DsurroundingBox;
	private SceneManagerInterface mSceneManager;
	private Box2DBody mGroundBody;
	private Box2DBody mTopBody;
	
	
	/**
	 * Creates a World, where all the elements will live in.
	 * @param low the lowest point (x, y) of the world
	 * @param high the highest point (x,y) of the world
	 */
	public Box2DWorld(Vector2f low, Vector2f high, Vector2f gravity) {
		mBox2DsurroundingBox = new AABB(new Vec2(low.x, low.y), new Vec2(high.x, high.y));
		mBox2DWorld = new World(mBox2DsurroundingBox, new Vec2(gravity.x, gravity.y), true);
		
		createGroundBody();
//		createTopBody();
	}
	
	public void step(float dt, int iterations){
		mBox2DWorld.step(dt, iterations);
	}
	
	
	/*
	 * Package Scope
	 */
	
	Body createBody(Box2DBody body) {
		return mBox2DWorld.createBody(body.getDefinition());
	}
	
	Joint createJoint(Box2DJoint joint) {
		return mBox2DWorld.createJoint(joint.getJointDef());
	}
	
	Box2DBody getGroundBody() {
		return mGroundBody;
	}
	
	Box2DBody getTopBody() {
		return mTopBody;
	}
	
	
	/*
	 * Private Methods
	 */
	
	private void createGroundBody() {
		Box2DShape groundShape = new Box2DShape();
		groundShape.setAsBox(50,5);
		mGroundBody = new Box2DBody(new Vector2f(-25,-10), this, groundShape, false, false);
//		mGroundBody.setType(TType.STATIC);
		
//		groundBody.setMassFromShapes();
	}
	
	private void createTopBody() {
		Box2DShape topShape = new Box2DShape();
		topShape.setAsBox(10, 1);
		mTopBody = new Box2DBody(new Vector2f(8, 4), this, topShape, false, false);
		mTopBody.createShape(topShape, false);
		mTopBody.setType(TType.STATIC);
	}
}
