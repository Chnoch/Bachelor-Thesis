package ch.chnoch.thesis.renderer;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;

import ch.chnoch.thesis.renderer.util.Util;

public abstract class Leaf implements Node {

	protected Node parent;
	protected Matrix4f mTranslationMatrix, mRotationMatrix,
			mTransformationMatrix;
	protected float mScale;
	protected boolean mIsActive = true;

	public Leaf() {
		super();
		mTransformationMatrix = Util.getIdentityMatrix();
		mRotationMatrix = Util.getIdentityMatrix();
		mTranslationMatrix = Util.getIdentityMatrix();
		mScale = 1;
	}

	public List<Node> getChildren() {
		return null;
	}

	public void addChild(Node child) {
	}

	public Matrix4f getTransformationMatrix() {
		return mTransformationMatrix;
	}

	public Matrix4f getTranslationMatrix() {
		return this.mTranslationMatrix;
	}

	public Matrix4f getRotationMatrix() {
		return this.mRotationMatrix;
	}

	public void setTranslationMatrix(Matrix4f t) {
		this.mTranslationMatrix = t;
		setTransformationMatrix();
	}

	public void setRotationMatrix(Matrix4f t) {
		this.mRotationMatrix = t;
		setTransformationMatrix();
	}
	
	public float getScale() {
		return this.mScale;
	}
	
	public void setScale(float f) {
		this.mScale = f;
		setTransformationMatrix();
	}

	protected void setTransformationMatrix() {
		Matrix4f trans = new Matrix4f(mRotationMatrix);
		Vector3f translation = new Vector3f();
		mTranslationMatrix.get(translation);
		trans.setTranslation(translation);
		trans.setScale(mScale);

		if (mTransformationMatrix == null) {
			mTransformationMatrix = Util.getIdentityMatrix();
		}
		mTransformationMatrix.set(trans);
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

	public Node getParent() {
		return this.parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public void setActiveState(boolean b) {
		this.mIsActive = b;
	}

}
