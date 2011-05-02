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

	public Leaf() {
		super();
		mTransformationMatrix = Util.getIdentityMatrix();
		mRotationMatrix = Util.getIdentityMatrix();
		mTranslationMatrix = Util.getIdentityMatrix();
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

	protected void setTransformationMatrix() {
		Matrix4f trans = new Matrix4f(mRotationMatrix);
		Vector3f translation = new Vector3f();
		mTranslationMatrix.get(translation);
		trans.setTranslation(translation);

		if (mTransformationMatrix == null) {
			mTransformationMatrix = Util.getIdentityMatrix();
		}
		mTransformationMatrix.set(trans);
	}

	public Node getParent() {
		return this.parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

}
