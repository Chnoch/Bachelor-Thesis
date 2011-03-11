package ch.chnoch.thesis.renderer;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class Trackball {

	private float scale = 0;
	private float mWidth = 0, mHeight = 0;
	private Shape mShape;

	public Trackball(Shape shape) {
		init(shape);
	}

	public Trackball(Shape shape, int width, int height) {
		mWidth = width;
		mHeight = height;
		init(shape);
	}

	private void init(Shape shape) {
		mShape = shape;
	}

	public void setSize(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public void scalePlus() {
		scale -= 0.05f;
		if (scale < -0.25f)
			scale = -0.25f;
	}

	public void scaleMinus() {
		scale += 0.05f;
		if (scale > 3)
			scale = 3;
	}

	private Vector3f trackBallMapping(float px, float py) {
		float x = (2 * px) / mWidth - 1;
		float y = (2 * py) / mHeight - 1;
		float z2 = 1 - x * x - y * y;
		float z = (z2 > 0 ? (float) Math.sqrt(z2) : 0);
		return new Vector3f(x, y, z);
	}

	public void simpleUpdate(float x, float y, float oldX, float oldY) {

		Vector3f oldVector = this.trackBallMapping(oldX, oldY);

		Vector3f newVector = this.trackBallMapping(x, y);

		Vector3f axisVector = new Vector3f();
		axisVector.cross(oldVector, newVector);
		 axisVector.normalize();
		// newVector.negate();
		float angle = oldVector.angle(newVector);

		//AxisAngle: 
		AxisAngle4d axisAngle = new AxisAngle4d(axisVector.x, axisVector.y,
				axisVector.z, angle);

		// Quaternions:
		Quat4f quat;

		float omega, s;
		float l = (float) Math.sqrt(axisVector.x * axisVector.x + axisVector.y
				* axisVector.y + axisVector.z * axisVector.z);

		omega = -0.5f * angle;
		s = (float) Math.sin(omega) / l;

		quat = new Quat4f(s * axisVector.x, s * axisVector.y, s * axisVector.z,
				(float) Math.cos(omega));
		
		
		Matrix4f trans = new Matrix4f();
//		trans.set(axisAngle);
		trans.set(quat);
		mShape.getTransformation().mul(trans);
	}
}