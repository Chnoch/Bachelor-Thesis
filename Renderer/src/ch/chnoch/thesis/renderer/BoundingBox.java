package ch.chnoch.thesis.renderer;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * A BoundingBox is used for every node that has a shape associated with it. It
 * is defined by its lowest and highest point (in 3D). A bounding box completely
 * surrounds its associated object and can therefore be used for a fast test
 * whether two shapes or a ray and a shape overlap, respectively intersect.
 */
public class BoundingBox {

	private Point3f mLow, mHigh, mLowUpdated, mHighUpdated;

	private boolean updated;

	/**
	 * Instantiates a new bounding box.
	 * 
	 * @param vertices
	 *            the vertices of the shape
	 */
	public BoundingBox(FloatBuffer vertices) {
		updated = true;
		init(vertices);
		mLowUpdated = new Point3f(mLow);
		mHighUpdated = new Point3f(mHigh);
	}

	/**
	 * Instantiates a new bounding box.
	 * 
	 * @param low
	 *            the lowest point
	 * @param high
	 *            the highest point
	 */
	public BoundingBox(Point3f low, Point3f high) {
		mLow = new Point3f(low);
		mHigh = new Point3f(high);

		updated = true;

		mLowUpdated = new Point3f(mLow);
		mHighUpdated = new Point3f(mHigh);
	}

	/**
	 * Initializes the bounding box.
	 * 
	 * @param vertices
	 *            the vertices of the shape
	 */
	private void init(FloatBuffer vertices) {
		float x, y, z;
		float lowX, lowY, lowZ, highX, highY, highZ;
		lowX = Float.MAX_VALUE;
		lowY = Float.MAX_VALUE;
		lowZ = Float.MAX_VALUE;
		highX = Float.MIN_VALUE;
		highY = Float.MIN_VALUE;
		highZ = Float.MIN_VALUE;

		while (vertices.remaining() > 0) {
			x = vertices.get();
			y = vertices.get();
			z = vertices.get();

			if (x < lowX) {
				lowX = x;
			}
			if (x > highX) {
				highX = x;
			}
			if (y < lowY) {
				lowY = y;
			}
			if (y > highY) {
				highY = y;
			}
			if (z < lowZ) {
				lowZ = z;
			}
			if (z > highZ) {
				highZ = z;
			}
		}
		vertices.position(0);

		mLow = new Point3f(lowX, lowY, lowZ);
		mHigh = new Point3f(highX, highY, highZ);
	}

	/**
	 * Creates a deep copy of this bounding box.
	 * 
	 * @return a copy of the bounding box
	 */
	public BoundingBox clone() {
		return new BoundingBox(new Point3f(mLow), new Point3f(mHigh));
	}

	/**
	 * Gets the lowest point.
	 * 
	 * @return the lowest point
	 */
	public Point3f getLow() {
		return mLow;
	}

	/**
	 * Gets the highest point.
	 * 
	 * @return the highest point
	 */
	public Point3f getHigh() {
		return mHigh;
	}


	/**
	 * Gets the center of the bounding box.
	 * 
	 * @return the center of the bounding box
	 */
	public Vector3f getCenter() {
		float xa = (mHigh.x - mLow.x) / 2f;
		float x = mLow.x + xa;

		float ya = (mHigh.y - mLow.y) / 2f;
		float y = mLow.y + ya;

		float za = (mHigh.z - mLow.z) / 2f;
		float z = mLow.z + za;
//		Log.d("Bounding Box", "Center: " + x + ", " + y + ", " + z);
		return new Vector3f(x, y, z);
	}

	/**
	 * Gets the radius of the bounding box.
	 * 
	 * @return the radius of the bounding box
	 */
	public float getRadius() {
		// simple calculation based on half of width / height / depth
		return (mHigh.x - mLow.x) / 2f;
	}


	/**
	 * Calculates the hit point between this bounding Box and the ray that is
	 * passed. The method will return a {@link RayShapeIntersection} that
	 * contains a boolean whether this box was hit, which node the box belongs
	 * to and the closest hit point.
	 * 
	 * @param ray
	 *            the ray, containing of a point in the 3D space and a direction
	 * @return the ray-shape-intersection, containing of a boolean whether a hit
	 *         occured, which node got hit and the coordinates of the hitpoint.
	 */
	public RayShapeIntersection hitPoint(Ray ray) {
		boolean inside = true;
		Quadrant[] quadrant = new Quadrant[3];
		float[] origin = new float[3];
		float[] direction = new float[3];
		float[] high = new float[3];
		float[] low = new float[3];
		float[] candidatePlane = new float[3];
		float[] maxT = new float[3];
		float[] coord = new float[3];
		RayShapeIntersection rayBoxIntersection = new RayShapeIntersection();

		origin[0] = ray.getOrigin().x;
		origin[1] = ray.getOrigin().y;
		origin[2] = ray.getOrigin().z;

		direction[0] = ray.getDirection().x;
		direction[1] = ray.getDirection().y;
		direction[2] = ray.getDirection().z;

		high[0] = mHigh.x;
		high[1] = mHigh.y;
		high[2] = mHigh.z;

		low[0] = mLow.x;
		low[1] = mLow.y;
		low[2] = mLow.z;

		for (int i = 0; i < 3; i++) {
			if (origin[i] < low[i]) {
				quadrant[i] = Quadrant.LEFT;
				candidatePlane[i] = low[i];
				inside = false;
			} else if (origin[i] > high[i]) {
				quadrant[i] = Quadrant.RIGHT;
				candidatePlane[i] = high[i];
				inside = false;
			} else {
				quadrant[i] = Quadrant.MIDDLE;
			}
		}

		// Ray origin inside bounding box
		if (inside) {
			rayBoxIntersection.hitPoint = ray.getOrigin();
			rayBoxIntersection.hit = true;
			return rayBoxIntersection;
		}

		// calculate T distances to candidate planes
		for (int i = 0; i < 3; i++) {
			if (quadrant[i] != Quadrant.MIDDLE && direction[i] != 0) {
				maxT[i] = (candidatePlane[i] - origin[i]) / direction[i];
			} else {
				maxT[i] = -1;
			}
		}

		// Get largest of the maxT's for final choice of intersection
		int whichPlane = 0;
		for (int i = 1; i < 3; i++) {
			if (maxT[whichPlane] < maxT[i]) {
				whichPlane = i;
			}
		}

		// Check final candidate actually inside box
		if (maxT[whichPlane] < 0) {
			return rayBoxIntersection;
		}

		for (int i = 0; i < 3; i++) {
			if (whichPlane != i) {
				coord[i] = origin[i] + maxT[whichPlane] * direction[i];
				if (coord[i] < low[i] || coord[i] > high[i]) {
					return rayBoxIntersection;
				}
			} else {
				coord[i] = candidatePlane[i];
			}
		}
		rayBoxIntersection.hit = true;
		rayBoxIntersection.hitPoint = new Vector3f(coord);
		return rayBoxIntersection;
	}

	/**
	 * Applies the transformations that are stored in the matrix to this
	 * bounding box and returns a new instance with the updated lowest and
	 * highest point. This updated version can be used for further checking.
	 * 
	 * @param transformation
	 *            the transformation matrix where all the transformations are
	 *            stored
	 * @return the transformed bounding box
	 */
	public BoundingBox update(Matrix4f transformation) {
		if (updated) {
			// create all possible points
			Point3f[] box = new Point3f[8];
			box[0] = new Point3f(mLow);
			box[1] = new Point3f(mLow.x, mHigh.y, mLow.z);
			box[2] = new Point3f(mLow.x, mLow.y, mHigh.z);
			box[3] = new Point3f(mLow.x, mHigh.y, mHigh.z);
			box[4] = new Point3f(mHigh.x, mLow.y, mLow.z);
			box[5] = new Point3f(mHigh.x, mHigh.y, mLow.z);
			box[6] = new Point3f(mHigh.x, mLow.y, mHigh.z);
			box[7] = new Point3f(mHigh);

			for (int i = 0; i < box.length; i++) {
				transformation.transform(box[i]);
			}

			Point3f low = new Point3f(box[0]);
			Point3f high = new Point3f(box[7]);

			for (int i = 0; i < box.length; i++) {
				if (box[i].x < low.x) {
					low.x = box[i].x;
				}
				if (box[i].y < low.y) {
					low.y = box[i].y;
				}
				if (box[i].z < low.z) {
					low.z = box[i].z;
				}
				if (box[i].x > high.x) {
					high.x = box[i].x;
				}
				if (box[i].y > high.y) {
					high.y = box[i].y;
				}
				if (box[i].z > high.z) {
					high.z = box[i].z;
				}
			}

			updated = false;

			mLowUpdated = low;
			mHighUpdated = high;
		}
		return new BoundingBox(mLowUpdated, mHighUpdated);
	}

	/**
	 * Sets the updated flag, to know whether this bounding box needs an update
	 * or not.
	 */
	public void setUpdated() {
		updated = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof BoundingBox) {
			BoundingBox box = (BoundingBox) obj;
			float epsilon = 0.00001f;
			return (this.mLow.epsilonEquals(box.mLow, epsilon) && this.mHigh
					.epsilonEquals(box.mHigh, epsilon));
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Low: " + this.mLow.toString() + " High: "
				+ this.mHigh.toString();
	}

	private enum Quadrant {
		LEFT, RIGHT, MIDDLE
	}
}
