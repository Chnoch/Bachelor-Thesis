package ch.chnoch.thesis.renderer;

import java.nio.FloatBuffer;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.box2d.Box2DBody;
import ch.chnoch.thesis.renderer.box2d.Box2DShape;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;
import ch.chnoch.thesis.renderer.interfaces.SceneManagerInterface;

/**
 * The PhysicsGroup class extends the abstract Group class to provide an
 * interface between the actual 3D scene and its interaction with the underlying
 * physics engine. It is generally good to have the physics part and the visual
 * part of a scene separated. This is the cased with our implementation of the
 * Box2D engine. <br>
 * This class lets you set up both your physics and your visual scene with
 * several predefined methods. It is also possible to link any two different
 * shapes together. This might be useful, if the object is visually rather
 * complex which would be fairly heavy on the computation for the physics
 * engine. This is why it lets you approximate visual objects by simpler
 * representations in the physics world (e.g. a circle instead of a teapot). <br>
 * Because Box2D (the physics engine used in this library) is a 2D library,
 * there are two different ways to connect your 2D physics world and your 3D
 * scene. You can either set up your 3D scene with all the objects that you want
 * to create and let the library automatically generate the representative 2D
 * objects for you. Otherwise you can specify your 2D objects, that will then be
 * mapped two 3D objects for a visual representation. Some additional
 * information might be needed. As a third path you can create the visual and
 * the physical representation independently and then link them together. This
 * might be useful if the physical approximation of an object is not easily
 * retraceable from your 3D mesh.
 */
public class PhysicsGroup extends Group {

	private Box2DWorld mWorld;

	/**
	 * Instantiates a new physics group. You need to pass the scene manager that
	 * you are using as well as a (2D) gravity component, that will be used in
	 * the physical calculations.
	 * 
	 * @param sceneManager
	 *            the scene manager
	 * @param gravity
	 *            the gravity of your physics world
	 */
	public PhysicsGroup(SceneManagerInterface sceneManager, Vector2f gravity) {
		super();
		createBox2DWorld(sceneManager, gravity);
	}

	/**
	 * Creates and sets up the physical world.
	 * 
	 * @param sceneManager
	 *            the scene manager
	 * @param gravity
	 *            the gravity of your physics world.
	 */
	private void createBox2DWorld(SceneManagerInterface sceneManager,
			Vector2f gravity) {
		Vector2f low = new Vector2f(-100, -100);
		Vector2f high = new Vector2f(100, 100);

		mWorld = new Box2DWorld(low, high, gravity);
		sceneManager.setPhysicsWorld(mWorld);
	}

	/**
	 * Adds a rectangle both to your physics world and to the 3D world. The
	 * depth is only used for the 3D representation.
	 * 
	 * @param width
	 *            the width of the rectangle
	 * @param height
	 *            the height of the rectangle
	 * @param depth
	 *            the depth of the rectangle
	 * @param position
	 *            the position in the 2D space. Any additional 3D movement has
	 *            to be done later on.
	 * @param hasMass
	 *            A boolean specifying whether this object has a mass, i.e.
	 *            whether it will be affected by the gravity in your physics
	 *            simulation or whether it will be a fixed object.
	 * @param createJoints
	 *            A boolean specifying whether the physics engine can create
	 *            joints for this object. Joints are needed for the user
	 *            interaction with the object. If you do not want users to be
	 *            able to interact with a certain object, disable this value.
	 * @return the shape node that is created and that has a representation in
	 *         the physics world.
	 */
	public ShapeNode addRectangle(float width, float height, float depth, Vector2f position, boolean hasMass, boolean createJoints) {
		Box2DShape shape = new Box2DShape(width, height);
		Box2DBody body = new Box2DBody(position, mWorld, shape, hasMass, createJoints);
		ShapeNode node = new ShapeNode(body,depth);
		addChild(node);
		return node;
	}

	/**
	 * Adds the ground body of your physics world. This creates a rectangle that
	 * doesn't react to user interaction and to gravity. It is also set as the
	 * ground of the physics world.
	 * 
	 * @param width
	 *            the width of the ground body
	 * @param height
	 *            the height of the ground body
	 * @param depth
	 *            the depth of the ground body
	 * @param position
	 *            the position in the 2D space of the ground body
	 * @return the shape node representing this surface
	 */
	public ShapeNode addGroundBody(float width, float height, float depth, Vector2f position) {
		ShapeNode node = addRectangle(width, height, depth, position, false, false);
		mWorld.setGroundBody(node.getPhysicsProperties());
		return node;
	}

	/**
	 * Adds a circle to the physics world and returns its visual representation.
	 * 
	 * @param radius
	 *            the radius of the circle
	 * @param position
	 *            the position in the 2D space
	 * @return the shape node representing a circle
	 */
	public ShapeNode addCircle(float radius, Vector2f position) {
		Box2DShape shape = new Box2DShape(radius);
		Box2DBody body = new Box2DBody(position, mWorld, shape, true, true);
		ShapeNode node = new ShapeNode(body,1);
		addChild(node);
		return node;
	}

	/**
	 * Adds an arbitrary body to the physics world and returns a graphical
	 * representation. Be careful to submit the vertices in the correct order as
	 * it wouldn't parse correctly otherwise.
	 * 
	 * @param The
	 *            vertices
	 * @param The
	 *            2D position of the object
	 * @return a ShapeNode with all the associated attributes
	 */

	public ShapeNode addArbitraryBody(FloatBuffer vertices, Vector2f position) {
		Box2DShape shape = new Box2DShape(vertices);
		Box2DBody body = new Box2DBody(position, mWorld, shape, true, true);
		ShapeNode node = new ShapeNode(body, 1);
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
