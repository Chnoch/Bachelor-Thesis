package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.Util;

public class TransformGroup extends Group {

	private Matrix4f mTranslationMatrix, mRotationMatrix,
			mTransformationMatrix;

	public TransformGroup() {
		super();
		mTranslationMatrix = Util.getIdentityMatrix();
		mRotationMatrix = Util.getIdentityMatrix();
		setTransformationMatrix();
	}

	public Matrix4f getTranslationMatrix() {
		return this.mTranslationMatrix;
	}

	public void setTranslationMatrix(Matrix4f t) {
		this.mTranslationMatrix = t;
		setTransformationMatrix();
	}

	public Matrix4f getRotationMatrix() {
		return this.mRotationMatrix;
	}

	public void setRotationMatrix(Matrix4f t) {
		this.mRotationMatrix = t;
		setTransformationMatrix();
	}

	protected void setTransformationMatrix() {
		if (mRotationMatrix != null && mTranslationMatrix != null) {
			Matrix4f trans = new Matrix4f(mRotationMatrix);
			Vector3f translation = new Vector3f();
			mTranslationMatrix.get(translation);
			trans.setTranslation(translation);

			if (mTransformationMatrix == null) {
				mTransformationMatrix = Util.getIdentityMatrix();
			}
			mTransformationMatrix.set(trans);
		}
	}

	public void initTranslationMatrix(Matrix4f t) {
		setTranslationMatrix(t);
	}

	public void initRotationMatrix(Matrix4f t) {
		setRotationMatrix(t);
	}

	public Matrix4f getTransformationMatrix() {
		return this.mTransformationMatrix;
	}

	public Matrix4f getCompleteTransformationMatrix() {
		Matrix4f transform = new Matrix4f(mTransformationMatrix);
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
