package ch.chnoch.thesis.renderer.box2d;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2f;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.FixtureDef;

/**
 * The Class Box2DShape is an abstraction for a Box2D
 * {@link org.jbox2d.collision.shapes.Shape}. This is the physical equivalent of
 * a {@link ch.chnoch.thesis.renderer.Shape} in the graphical domain. It
 * contains information about the friction, the density and obviously the shape
 * of the physical object.
 */
public class Box2DShape {

	private FixtureDef mFixtureDef;
	private Shape mShape;

	private Box2DShapeType mType;

	/**
	 * Instantiates a new box2d shape.
	 */
	public Box2DShape() {
		init();
	}

	/**
	 * Instantiates a new box2d shape that has the form of a box.
	 * 
	 * @param width
	 *            the width of the box
	 * @param height
	 *            the height of the box
	 */
	public Box2DShape(float width, float height){
		mType = Box2DShapeType.BOX;
		init();
		setAsBox(width, height);
	}

	/**
	 * Instantiates a new box2d shape that has the form of a circle.
	 * 
	 * @param radius
	 *            the radius of the circle
	 */
	public Box2DShape(float radius) {
		mType = Box2DShapeType.CIRCLE;
		init();
		setAsCircle(radius);
	}

	/**
	 * Instantiates a new box2d shape from an arbitrary shape. This will just
	 * assume that the object is a box and basically create a bounding box as a
	 * physical representation. It is therefore not very practical for usage.
	 * 
	 * @param vertices
	 *            the vertices of the object
	 */
	public Box2DShape(FloatBuffer vertices) {
		init();

		// TODO: Check for correct order of vertices
		// First approach: We assume cubes. We just take the smallest and
		// biggest value
		// and generate a box out of it.
		float xmin = Float.MAX_VALUE;
		float xmax = Float.MIN_VALUE;
		float ymin = Float.MAX_VALUE;
		float ymax = Float.MIN_VALUE;
		for (Vector2f coord : getCoordinates(vertices)) {
			if (coord.x < xmin) {
				xmin = coord.x;
			}
			if (coord.x > xmax) {
				xmax = coord.x;
			}
			if (coord.y < ymin) {
				ymin = coord.y;
			}
			if (coord.y > ymax) {
				ymax = coord.y;
			}

		}
		setAsBox((xmax - xmin) / 2, (ymax - ymin) / 2);
	}

	/**
	 * Inits the Box2D shape with default or passed values.
	 */
	private void init() {
		mFixtureDef = new FixtureDef();
		// default values
		mFixtureDef.density = 10;
		mFixtureDef.friction = 0.3f;
		mFixtureDef.restitution = 0.5f;
		
		switch (mType) {
		case CIRCLE:
			mShape = new CircleShape();
			break;
		default:
			mShape = new PolygonShape();
		}
	}

	/**
	 * Sets the density of the shape.
	 * 
	 * @param dens
	 *            the new density
	 */
	public void setDensity(float dens) {
		mFixtureDef.density = dens;
	}
	
	/**
	 * Sets the friction.
	 * 
	 * @param friction
	 *            the new friction
	 */
	public void setFriction(float friction) {
		mFixtureDef.friction = friction;
	}

	/**
	 * Gets the coordinates of the shape.
	 * 
	 * @return the coordinates
	 */
	public List<Vector2f> getCoordinates() {
		List<Vector2f> vectorList = new ArrayList<Vector2f>();
		Vec2[] vertices = ((PolygonShape)mShape).getVertices();
		for (Vec2 vertex : vertices) {
			vectorList.add(new Vector2f(vertex.x, vertex.y));
		}
		return vectorList;
	}

	/**
	 * Gets the radius of the shape if it is a circle.
	 * 
	 * @return the radius
	 */
	public float getRadius() {
		return mShape.m_radius;
	}

	/**
	 * Gets the {@link Box2DShapeType} of the shape.
	 * 
	 * @return the type
	 */
	public Box2DShapeType getType() {
		return mType;
	}
	

	/*
	 * Package Scope
	 */

	/**
	 * Sets the shape as a box.
	 * 
	 * @param x
	 *            the half-width
	 * @param y
	 *            the half-height
	 */
	void setAsBox(float x, float y) {
		mShape.m_type = ShapeType.POLYGON;
		mType = Box2DShapeType.BOX;
		((PolygonShape)mShape).setAsBox(x, y);
	}

	/**
	 * Sets the shape as a circle.
	 * 
	 * @param radius
	 *            the radius
	 */
	void setAsCircle(float radius) {
		mShape.m_type = ShapeType.CIRCLE;
		mType = Box2DShapeType.CIRCLE;
		mShape.m_radius = radius;
		
	}

	/**
	 * Gets the definition of the shape.
	 * 
	 * @return the fixture definition
	 */
	FixtureDef getFixtureDef() {
		mFixtureDef.shape = mShape;
		return mFixtureDef;
	}
	
	/**
	 * Gets the shape.
	 * 
	 * @return the shape
	 */
	Shape getShape() {
		return mShape;
	}

	/*
	 * Private Methods
	 */

	/**
	 * Gets all x,y coordinates, that have the lowest z-value, therefore making
	 * an appropriate measure for a 3D->2D conversion
	 * 
	 * @param verticesBuffer
	 *            the vertices buffer
	 * @return the coordinates
	 */
	private List<Vector2f> getCoordinates(FloatBuffer verticesBuffer) {

		// get Coordinates into an array
		float[] vertices = new float[verticesBuffer.capacity()];

		for (int i = 0; i < verticesBuffer.capacity(); i++) {
			vertices[i] = verticesBuffer.get(i);
		}
		// read closest points based on their z-value

		// find smallest z-value
		float z = Float.MAX_VALUE;
		for (int i = 2; i < vertices.length; i += 3) {
			if (vertices[i] < z) {
				z = vertices[i];
			}
		}

		// add all points with a z-value that are equals
		Vector2f vertex;
		List<Vector2f> finalList = new ArrayList<Vector2f>();
		for (int i = 2; i < vertices.length; i += 9) {
			if (vertices[i] == z) {
				vertex = new Vector2f();
				vertex.x = vertices[i - 2];
				vertex.y = vertices[i - 1];
				finalList.add(vertex);
			}
		}

		return finalList;
	}

	/**
	 * The different possibly types of shapes.
	 */
	public enum Box2DShapeType {

		BOX,
		CIRCLE,
		POLYGON
	}
}
