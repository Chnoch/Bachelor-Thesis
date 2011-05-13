package ch.chnoch.thesis.renderer.box2d;

import javax.vecmath.Vector2f;

import org.jbox2d.collision.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import ch.chnoch.thesis.renderer.interfaces.*;

public class Box2DWorld {

	World mBox2DWorld;
	AABB mBox2DsurroundingBox;
	SceneManagerInterface mSceneManager;
	
	/**
	 * Creates a World, where all the elements will live in.
	 * @param low the lowest point (x, y) of the world
	 * @param high the highest point (x,y) of the world
	 */
	public Box2DWorld(Vector2f low, Vector2f high, Vector2f gravity) {
		mBox2DsurroundingBox = new AABB(new Vec2(low.x, low.y), new Vec2(high.x, high.y));
		mBox2DWorld = new World(mBox2DsurroundingBox, new Vec2(gravity.x, gravity.y), true);
		
		createGroundBody();
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
	
	
	/*
	 * Private Methods
	 */
	
	private void createGroundBody() {
		Box2DBody groundBody = new Box2DBody(new Vector2f(0,-10), this);
		Box2DShape groundShape = new Box2DShape();
		groundShape.setAsBox(50,1);
		groundBody.createShape(groundShape);
		
		groundBody.setMassFromShapes();
	}
}
