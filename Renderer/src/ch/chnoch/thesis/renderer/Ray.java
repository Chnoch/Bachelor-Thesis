package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

// TODO: Auto-generated Javadoc
/**
 * The Class Ray.
 */
public class Ray {
	
	/** The m origin. */
	private Vector3f mOrigin;
	
	/** The m direction. */
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
	 * @param origin the origin
	 * @param direction the direction
	 */
	public Ray(Vector3f origin, Vector3f direction) {
		mOrigin = origin;
		mDirection = direction;
	}

	/**
	 * Sets the direction.
	 *
	 * @param direction the new direction
	 */
	public void setDirection(Vector3f direction) {
		mDirection = direction;
	}

	/**
	 * Gets the direction.
	 *
	 * @return the direction
	 */
	public Vector3f getDirection() {
		return mDirection;
	}

	/**
	 * Sets the origin.
	 *
	 * @param origin the new origin
	 */
	public void setOrigin(Vector3f origin) {
		mOrigin = origin;
	}

	/**
	 * Gets the origin.
	 *
	 * @return the origin
	 */
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
