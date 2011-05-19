package ch.chnoch.thesis.renderer.box2d;

import javax.vecmath.Vector2f;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

public class Box2DBody {
	private Body mBox2DBody;
	private BodyDef mBox2DBodyDef;
	private Vector2f mPreviousPosition;

	public Box2DBody(Vector2f position, Box2DWorld world) {
		mBox2DBodyDef = new BodyDef();
		mBox2DBodyDef.position.set(position.x, position.y);
		mPreviousPosition = position;
		mBox2DBody = world.createBody(this);
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
	
	public void setType(TType type) {
		int typeInt = 0;
		switch (type) {
		case DYNAMIC:  typeInt = Body.e_dynamicType; break;
		case STATIC: typeInt = Body.e_staticType; break;
		default: typeInt = Body.e_dynamicType; break;
		}
		mBox2DBody.m_type = typeInt;
	}
	
	public void setDensity(float density) {
		mBox2DBody.m_shapeList.m_density = density;
	}
	
	public void setFriction(float friction) {
		mBox2DBody.m_shapeList.m_friction = friction;
	}
	
	public float getAngle() {
		return mBox2DBody.getAngle();
	}
	
	public void createShape(Box2DShape shape) {
		mBox2DBody.createShape(shape.getPolygonDef());
		mBox2DBody.setMassFromShapes();
	}
	
	
	/*
	 * Package Scope
	 */

	BodyDef getDefinition() {
		return mBox2DBodyDef;
	}


	void setMassFromShapes() {
		mBox2DBody.setMassFromShapes();
	}
	
	public enum TType {
		STATIC, DYNAMIC
	}
}
