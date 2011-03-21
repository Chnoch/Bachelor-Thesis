package ch.chnoch.thesis.renderer;

import javax.vecmath.Vector3f;

public class Ray {
	private Vector3f mOrigin;
	private Vector3f mDirection;
	
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
}
