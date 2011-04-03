package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.util.Util;

public class ShapeNode extends Leaf {

	private Shape mShape;
	private BoundingBox mBoundingBox;

	public ShapeNode() {
		super();
	}

	public void setShape(Shape shape) {
		this.mShape = shape;
		setBoundingBox();
	}

	public Shape getShape() {
		return this.mShape;
	}

	private void setBoundingBox() {
		if (mShape != null && transformationMatrix != null) {
			Matrix4f transform = new Matrix4f(transformationMatrix);
			Matrix4f temp = Util.getIdentityMatrix();

			Node current = this;
			while (current.getParent() != null) {
				parent = current.getParent();
				temp.set(parent.getTransformationMatrix());
				temp.mul(transform);
				transform.set(temp);
			}
			mBoundingBox = mShape.getBoundingBox().clone();
			mBoundingBox.transform(transform);
		}
	}

	public BoundingBox getBoundingBox() {
		return mBoundingBox;
	}

	public void setTransformationMatrix(Matrix4f t) {
		if (transformationMatrix == null) {
			transformationMatrix = new Matrix4f();
		}
		transformationMatrix.set(t);
		setBoundingBox();
	}
}
