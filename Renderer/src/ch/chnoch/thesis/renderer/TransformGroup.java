package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.util.Util;

public class TransformGroup extends Group {

	private Matrix4f transformationMatrix;

	public TransformGroup() {
		super();
	}

	public void setTransformationMatrix(Matrix4f t) {
		this.transformationMatrix = t;
	}

	public Matrix4f getTransformationMatrix() {
		return this.transformationMatrix;
	}

	public Matrix4f getCompleteTransformationMatrix() {
		Matrix4f transform = new Matrix4f(transformationMatrix);
		Matrix4f temp = Util.getIdentityMatrix();
		Node current = this;
		while (current.getParent() != null) {
			current = current.getParent();
			temp.set(current.getTransformationMatrix());
			temp.mul(transform);
			transform.set(temp);
		}

		return transform;
	}

}
