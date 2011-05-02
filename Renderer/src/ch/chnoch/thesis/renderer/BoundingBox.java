package ch.chnoch.thesis.renderer;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.util.Util;

import android.util.Log;

public class BoundingBox {

	private Point3f mLow, mHigh, mLowUpdated, mHighUpdated;
	private boolean updated;

	public BoundingBox(IntBuffer vertices) {
		updated = true;
		init(vertices);
		mLowUpdated = new Point3f(mLow);
		mHighUpdated = new Point3f(mHigh);
	}

	public BoundingBox(Point3f low, Point3f high) {
		mLow = new Point3f(low);
		mHigh = new Point3f(high);

		updated = true;

		mLowUpdated = new Point3f(mLow);
		mHighUpdated = new Point3f(mHigh);
	}

	private void init(IntBuffer vertices) {
		int x, y, z;
		float lowX, lowY, lowZ, highX, highY, highZ;
		lowX = Integer.MAX_VALUE;
		lowY = Integer.MAX_VALUE;
		lowZ = Integer.MAX_VALUE;
		highX = Integer.MIN_VALUE;
		highY = Integer.MIN_VALUE;
		highZ = Integer.MIN_VALUE;

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

		// correction because of 16/16 fixed integer-representation of fp's
		float div = 65536;
		lowX = (float) lowX / div;
		lowY = (float) lowY / div;
		lowZ = (float) lowZ / div;
		highX = (float) highX / div;
		highY = (float) highY / div;
		highZ = (float) highZ / div;

		mLow = new Point3f(lowX, lowY, lowZ);
		mHigh = new Point3f(highX, highY, highZ);
	}

	public BoundingBox clone() {
		return new BoundingBox(new Point3f(mLow), new Point3f(mHigh));
	}

	public Point3f getLow() {
		return mLow;
	}

	public Point3f getHigh() {
		return mHigh;
	}

	public List<Plane> getPlanes() {
		List<Plane> planes = new ArrayList<Plane>();

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

		Plane plane;
		Vector3f a = new Vector3f();
		Vector3f b = new Vector3f();
		
		// Create all six planes and add them to the array
		
		// First Plane
		Vector3f pointOnPlane = new Vector3f(box[0]);
		Vector3f normal = new Vector3f();
		a.sub(box[1], box[0]);
		b.sub(box[2], box[0]);
		normal.cross(a, b);
		planes.add(new Plane(pointOnPlane, normal));

		// Second plane
		pointOnPlane = new Vector3f(box[0]);
		normal = new Vector3f();
		b.sub(box[4], box[0]);
		normal.cross(a, b);
		planes.add(new Plane(pointOnPlane, normal));
		
		// Third Plane
		pointOnPlane = new Vector3f(box[0]);
		normal = new Vector3f();
		a.sub(box[2], box[0]);
		normal.cross(a, b);
		planes.add(new Plane(pointOnPlane, normal));
		
		// Fourth Plane
		pointOnPlane = new Vector3f(box[7]);
		normal = new Vector3f();
		a.sub(box[3], box[7]);
		b.sub(box[5], box[7]);
		normal.cross(a, b);
		planes.add(new Plane(pointOnPlane, normal));
		
		// Fifth Plane
		pointOnPlane = new Vector3f(box[7]);
		normal = new Vector3f();
		b.sub(box[6], box[7]);
		normal.cross(a, b);
		planes.add(new Plane(pointOnPlane, normal));
		
		//Sixth Plane
		pointOnPlane = new Vector3f(box[7]);
		normal = new Vector3f();
		a.sub(box[5], box[7]);
		normal.cross(a, b);
		planes.add(new Plane(pointOnPlane, normal));
		
		return planes;
	}

	public Point3f getCenter() {
		float xa = (mHigh.x - mLow.x) / 2f;
		float x = mLow.x + xa;

		float ya = (mHigh.y - mLow.y) / 2f;
		float y = mLow.y + ya;

		float za = (mHigh.z - mLow.z) / 2f;
		float z = mLow.z + za;
		Log.d("Bounding Box", "Center: " + x + ", " + y + ", " + z);
		return new Point3f(x, y, z);
	}

	public float getRadius() {
		// simple calculation based on half of width / height / depth
		return (mHigh.x - mLow.x) / 2f;
	}

	public enum Quadrant {
		LEFT, RIGHT, MIDDLE
	}

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
	 * A simple algorithm to determine whether the specified Ray hit this box.
	 * It will return a RayShapeIntersection, but only RayShapeIntersection.hit
	 * will be set. Possibly also RayShapeIntersection.node, but .hitPoint won't
	 * reveal anything. This method is mostly used to check if the box is hit.
	 * If you want the exact coordinates use BoundingBox.hitPoint(ray) instead.
	 * 
	 * @param ray
	 * @return RayShapeIntersection
	 */
	public RayShapeIntersection intersect(Ray ray) {
		float tXmin, tXmax, tYmin, tYmax, tZmin, tZmax;
		Point3f low = mLow;
		Point3f high = mHigh;
		RayShapeIntersection intersect = new RayShapeIntersection();

		if (ray.getDirection().x >= 0) {
			tXmin = (low.x - ray.getOrigin().x) / ray.getDirection().x;
			tXmax = (high.x - ray.getOrigin().x) / ray.getDirection().x;
		} else {
			tXmin = (high.x - ray.getOrigin().x) / ray.getDirection().x;
			tXmax = (low.x - ray.getOrigin().x) / ray.getDirection().x;
		}

		if (ray.getDirection().y >= 0) {
			tYmin = (low.y - ray.getOrigin().y) / ray.getDirection().y;
			tYmax = (high.y - ray.getOrigin().y) / ray.getDirection().y;
		} else {
			tYmin = (high.y - ray.getOrigin().y) / ray.getDirection().y;
			tYmax = (low.y - ray.getOrigin().y) / ray.getDirection().y;
		}

		if ((tXmin > tYmax) || (tYmin > tXmax))
			return intersect;
		if (tYmin > tXmin)
			tXmin = tYmin;
		if (tYmax < tXmax)
			tXmax = tYmax;

		if (ray.getDirection().z >= 0) {
			tZmin = (low.z - ray.getOrigin().z) / ray.getDirection().z;
			tZmax = (high.z - ray.getOrigin().z) / ray.getDirection().z;
		} else {
			tZmin = (high.z - ray.getOrigin().z) / ray.getDirection().z;
			tZmax = (low.z - ray.getOrigin().z) / ray.getDirection().z;
		}

		if ((tXmin > tZmax) || (tZmin > tXmax))
			return intersect;

		intersect.hit = true;
		return intersect;
	}

	/**
	 * This method is to be used to transform the bounding box together with the
	 * shape
	 * 
	 * @param trans
	 */
	public void transform(Matrix4f trans) {
		mLow.sub(getCenter());
		mHigh.sub(getCenter());

		Log.d("Bounding Box",
				"Pre Low: " + mLow.toString() + " High: " + mHigh.toString());
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
			trans.transform(box[i]);
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

		mLow = new Point3f(low);
		mHigh = new Point3f(high);

		Log.d("Bounding Box", "Past Low: " + mLow.toString() + " High: "
				+ mHigh.toString());
	}

	public BoundingBox update(Matrix4f rot) {
		if (updated) {
			Log.d("Bounding Box", "Pre Low: " + mLow.toString() + " High: "
					+ mHigh.toString());
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
				rot.transform(box[i]);
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
			Log.d("Bounding Box", "Past Low: " + low.toString() + " High: "
					+ high.toString());

			mLowUpdated = low;
			mHighUpdated = high;
		}
		return new BoundingBox(mLowUpdated, mHighUpdated);
	}

	public void setUpdated() {
		updated = true;
	}

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

	public String toString() {
		return "Low: " + this.mLow.toString() + " High: "
				+ this.mHigh.toString();
	}
}
