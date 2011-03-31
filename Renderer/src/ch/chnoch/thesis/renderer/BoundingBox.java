package ch.chnoch.thesis.renderer;

import java.nio.IntBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import ch.chnoch.thesis.renderer.util.Util;

import android.util.Log;

public class BoundingBox {

	private Vector3f mLow, mHigh;

	public BoundingBox(IntBuffer vertices) {
		init(vertices);
	}

	public BoundingBox(Vector3f low, Vector3f high) {
		mLow = low;
		mHigh = high;
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

		mLow = new Vector3f(lowX, lowY, lowZ);
		mHigh = new Vector3f(highX, highY, highZ);
	}

	public Vector3f getLow() {
		return mLow;
	}

	public Vector3f getHigh() {
		return mHigh;
	}

	public Vector3f getCenter() {
		float xa = (mHigh.x - mLow.x) / 2f;
		float x = mLow.x + xa;

		float ya = (mHigh.y - mLow.y) / 2f;
		float y = mLow.y + ya;

		float za = (mHigh.z - mLow.z) / 2f;
		float z = mLow.z + za;

		return new Vector3f(x, y, z);
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

	public enum Quadrant {
		LEFT, RIGHT, MIDDLE
	}
	
	public RayShapeIntersection intersect(Ray ray) {
		float tXmin, tXmax, tYmin, tYmax, tZmin, tZmax;
		Vector3f low = mLow;
		Vector3f high = mHigh;
		RayShapeIntersection intersect = new RayShapeIntersection();
		Log.d("Ray", "Origin: " + ray.getOrigin().toString() + "Direction: " + ray.getDirection().toString());
		Log.d("Box", "Low: " + low.toString() + " High: " + high);
		
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
	public BoundingBox transform(Matrix4f trans) {
		Vector3f low = new Vector3f(mLow);
		Vector3f high = new Vector3f(mHigh);
		
		Util.transform(trans, low);
		Util.transform(trans, high);
		return new BoundingBox(new Vector3f(low.x, low.y, low.z), new Vector3f(high.x, high.y, high.z));
	}
}
