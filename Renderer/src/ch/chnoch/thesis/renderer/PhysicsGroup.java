package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.box2d.Box2DBody;
import ch.chnoch.thesis.renderer.box2d.Box2DShape;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

// TODO: Auto-generated Javadoc
/**
 * The Class PhysicsGroup.
 */
public class PhysicsGroup extends Group {

	/** The m world. */
	private Box2DWorld mWorld;

	/**
	 * Instantiates a new physics group.
	 * 
	 * @param sceneManager
	 *            the scene manager
	 * @param gravity
	 *            the gravity
	 */
	public PhysicsGroup(SceneManagerInterface sceneManager, Vector2f gravity) {
		super();
		createBox2DWorld(sceneManager, gravity);
	}
	
	/**
	 * Creates the box2 d world.
	 * 
	 * @param sceneManager
	 *            the scene manager
	 * @param gravity
	 *            the gravity
	 */
	private void createBox2DWorld(SceneManagerInterface sceneManager,
			Vector2f gravity) {
		Vector2f low = new Vector2f(-100, -100);
		Vector2f high = new Vector2f(100, 100);

		mWorld = new Box2DWorld(low, high, gravity);
		sceneManager.setPhysicsWorld(mWorld);
	}
	
	/**
	 * Adds the rectangle.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param depth
	 *            the depth
	 * @param position
	 *            the position
	 * @param hasMass
	 *            the has mass
	 * @param createJoints
	 *            the create joints
	 * @return the shape node
	 */
	public ShapeNode addRectangle(float width, float height, float depth, Vector2f position, boolean hasMass, boolean createJoints) {
		Box2DShape shape = new Box2DShape(width, height);
		Box2DBody body = new Box2DBody(position, mWorld, shape, hasMass, createJoints);
		ShapeNode node = new ShapeNode(body,depth);
		addChild(node);
		return node;
	}
	
	/**
	 * Adds the ground body.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param depth
	 *            the depth
	 * @param position
	 *            the position
	 * @return the shape node
	 */
	public ShapeNode addGroundBody(float width, float height, float depth, Vector2f position) {
		ShapeNode node = addRectangle(width, height, depth, position, false, false);
		mWorld.setGroundBody(node.getPhysicsProperties());
		return node;
	}

	/**
	 * Adds the circle.
	 * 
	 * @param radius
	 *            the radius
	 * @param position
	 *            the position
	 * @return the shape node
	 */
	public ShapeNode addCircle(float radius, Vector2f position) {
		Box2DShape shape = new Box2DShape(radius);
		Box2DBody body = new Box2DBody(position, mWorld, shape, true, true);
		ShapeNode node = new ShapeNode(body,1);
		addChild(node);
		return node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#move(javax.vecmath.Vector3f)
	 */
	@Override
	public void move(Vector3f v) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getCenter()
	 */
	@Override
	public Vector3f getCenter() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getMaterial()
	 */
	@Override
	public Material getMaterial() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#setMaterial(ch.chnoch.thesis
	 * .renderer.Material)
	 */
	@Override
	public void setMaterial(Material material) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#enablePhysicsProperties(ch.
	 * chnoch.thesis.renderer.box2d.Box2DWorld)
	 */
	@Override
	public void enablePhysicsProperties(Box2DWorld world) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#updatePositionFromPhysic()
	 */
	@Override
	public void updatePositionFromPhysic() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getPhysicsProperties()
	 */
	@Override
	public Box2DBody getPhysicsProperties() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#intersect(ch.chnoch.thesis.
	 * renderer.Ray)
	 */
	@Override
	public RayShapeIntersection intersect(Ray ray) {
		return null;
	}

}
