package ch.chnoch.thesis.renderer.box2d;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2f;

import org.jbox2d.collision.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.FixtureDef;

public class Box2DShape {

	private FixtureDef mFixtureDef;
	private PolygonShape mPolygonShape;

	public Box2DShape() {
		init();
	}

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

	private void init() {
		mFixtureDef = new FixtureDef();
		// Some random default values
		mFixtureDef.density = 1;
		mFixtureDef.friction = 0.3f;
		
		mPolygonShape = new PolygonShape();
	}

	void setDensity(float dens) {
		mFixtureDef.density = dens;
	}

	void setFriction(float friction) {
		mFixtureDef.friction = friction;
	}

	/*
	 * Package Scope
	 */

	void setAsBox(float x, float y) {
		mPolygonShape.setAsBox(x, y);
	}

	FixtureDef getFixtureDef() {
		mFixtureDef.shape = mPolygonShape;
		return mFixtureDef;
	}
	
	PolygonShape getPolygonShape() {
		return mPolygonShape;
	}

	/*
	 * Private Methods
	 */

	/*
	 * Gets all x,y coordinates, that have the lowest z-value, therefore making
	 * an appropriate measure for a 3D->2D conversion
	 */
	private List<Vector2f> getCoordinates(FloatBuffer verticesBuffer) {

		// get Coordinates into an array
		float[] vertices = new float[verticesBuffer.capacity()];

		for (int i = 0; i < verticesBuffer.capacity(); i++) {
			vertices[i] = verticesBuffer.get(i);
		}
		// Fixed Point Conversion
		// float[] vertices = new float[verticesInt.length];
		// for (int i = 0; i < verticesInt.length; i++) {
		// vertices[i] = (float) verticesInt[i] / 65536;
		// }

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
}
