package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.box2d.Box2DBody;
import ch.chnoch.thesis.renderer.box2d.Box2DShape;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;
import ch.chnoch.thesis.renderer.util.Util;

public class PhysicsGroup extends Group {

	private Box2DWorld mWorld;

	public PhysicsGroup(SceneManagerInterface sceneManager) {
		super();
		createBox2DWorld(sceneManager);
	}
	
	private void createBox2DWorld(SceneManagerInterface sceneManager) {
		Vector2f low = new Vector2f(-100, -100);
		Vector2f high = new Vector2f(100, 100);
		Vector2f gravity = new Vector2f(0, -10);

		mWorld = new Box2DWorld(low, high, gravity);
		sceneManager.setPhysicsWorld(mWorld);
	}
	
	public ShapeNode addRectangle(float width, float height, float depth, Vector2f position, boolean hasMass, boolean createJoints) {
		Box2DShape shape = new Box2DShape(width, height);
		Box2DBody body = new Box2DBody(position, mWorld, shape, hasMass, createJoints);
		ShapeNode node = new ShapeNode(body,depth);
		addChild(node);
		return node;
	}
	
	public ShapeNode addGroundBody(float width, float height, float depth, Vector2f position) {
		ShapeNode node = addRectangle(width, height, depth, position, false, false);
		mWorld.setGroundBody(node.getPhysicsProperties());
		return node;
	}

	public ShapeNode addCircle(float radius, Vector2f position) {
		Box2DShape shape = new Box2DShape(radius);
		Box2DBody body = new Box2DBody(position, mWorld, shape, true, true);
		ShapeNode node = new ShapeNode(body,1);
		addChild(node);
		return node;
	}

	@Override
	public void move(Vector3f v) {

	}

	@Override
	public Material getMaterial() {
		return null;
	}

	@Override
	public void setMaterial(Material material) {

	}

	@Override
	public void enablePhysicsProperties(Box2DWorld world) {

	}

	@Override
	public void updatePositionFromPhysic() {

	}

	@Override
	public Box2DBody getPhysicsProperties() {
		return null;
	}

	@Override
	public RayShapeIntersection intersect(Ray ray) {
		return null;
	}

}
