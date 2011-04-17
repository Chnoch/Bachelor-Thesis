package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.Util;

public class ShapeNode extends Leaf {

	private GraphSceneManager mSceneManager;
	private Shape mShape;
	private BoundingBox mBoundingBox;

	public ShapeNode(Shape shape, GraphSceneManager sceneManager) {
		super();
		mRotationMatrix = Util.getIdentityMatrix();
		mTranslationMatrix = Util.getIdentityMatrix();
		mShape = shape;
		mSceneManager = sceneManager;
		setTransformationMatrix();
		initBoundingBox();
	}

	public void setShape(Shape shape) {
		this.mShape = shape;
		initBoundingBox();
	}

	public Shape getShape() {
		return this.mShape;
	}

	public BoundingBox getBoundingBox() {
		return mBoundingBox.update(getRotationMatrix());
	}

	public void initTranslationMatrix(Matrix4f t) {
		mTranslationMatrix.set(t);
		setTransformationMatrix();
		initBoundingBox();
	}

	public void initRotationMatrix(Matrix4f t) {
		mRotationMatrix.set(t);
		setTransformationMatrix();
		initBoundingBox();
	}

	public void setTranslationMatrix(Matrix4f t) {
		mTranslationMatrix.set(t);
		setTransformationMatrix();
		updateBoundingBox();
	}

	public void setRotationMatrix(Matrix4f t) {
		mRotationMatrix.set(t);
		setTransformationMatrix();
		updateBoundingBox();
	}

	public Matrix4f getCompleteTransformationMatrix() {
		Matrix4f transform = new Matrix4f(getTransformationMatrix());
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

	public void setParent(Node parent) {
		this.parent = parent;
		initBoundingBox();
	}

	/*
	 * Private Methods
	 */

	private void updateBoundingBox() {
		// The bounding box must be set
		assert mBoundingBox != null;
//		mBoundingBox.transform(getRotationMatrix());
		mBoundingBox.setUpdated();
	}

	/**
	 * This method is only used upon initialization or if something drastic has
	 * changed (e.g. the underlying shape or the parent). Do not use this to re
	 * calculate the Bounding box after a rotation or translation.
	 */
	private void initBoundingBox() {
		if (mShape != null && getTransformationMatrix() != null
				&& (getParent() != null || mSceneManager.getRoot() == this)) {
			Matrix4f transform = getCompleteTransformationMatrix();

			if (mBoundingBox == null) {
				mBoundingBox = mShape.getBoundingBox().clone();
			}
			mBoundingBox.transform(transform);
		}
	}
}
