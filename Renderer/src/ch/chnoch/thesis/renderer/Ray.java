package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

/**
 * A ray is used to find the object that lies in the 3D-space exactly below the
 * 2D point that is pressed by the user on the screen. The ray is created by
 * extracting two points in the 3D scene that lie below the finger and then use
 * one of these points as the base point and the difference as the direction. <br>
 * A ray can then be intersected with all the different objects to locate the
 * closest one that intersects the ray. This object will then be further
 * manipulated by moving the finger etc. <br>
 * <br>
 * A ray is defined by an origin and a direction.
 */
public class Ray {
	
	private Vector3f mOrigin;
	
	private Vector3f mDirection;
	
	/**
	 * Instantiates a new ray.
	 */
	public Ray() {
		mOrigin = new Vector3f();
		mDirection = new Vector3f();
	}

	/**
	 * Instantiates a new ray.
	 * 
	 * @param origin
	 *            the origin of the ray
	 * @param direction
	 *            the direction of the ray
	 */
	public Ray(Vector3f origin, Vector3f direction) {
		mOrigin = origin;
		mDirection = direction;
	}

	public void setDirection(Vector3f direction) {
		mDirection = direction;
	}

	public Vector3f getDirection() {
		return mDirection;
	}

	public void setOrigin(Vector3f origin) {
		mOrigin = origin;
	}

	public Vector3f getOrigin() {
		return mOrigin;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Origin: " + mOrigin.toString() + " Direction: " + mDirection.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object ray) {
		if (ray instanceof Ray) {
			return this.mOrigin.equals(((Ray)ray).mOrigin) && this.mDirection.equals(((Ray) ray).mDirection);
		} else {
			return false;
		}
	}
}
