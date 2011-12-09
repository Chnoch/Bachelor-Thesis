package ch.chnoch.thesis.renderer;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;

import ch.chnoch.thesis.renderer.util.Util;

public abstract class Group implements Node {

	protected Node mParent;
	protected List<Node> mChildren;
	protected boolean mIsActive = true;

	protected Matrix4f mTranslationMatrix, mRotationMatrix,
			mTransformationMatrix;

	public Group() {
		super();
		mChildren = new LinkedList<Node>();
		mTranslationMatrix = Util.getIdentityMatrix();
		mRotationMatrix = Util.getIdentityMatrix();
		setTransformationMatrix();
	}

	public List<Node> getChildren() {
		return this.mChildren;
	}

	public Shape getShape() {
		return null;
	}

	public void setShape(Shape shape) {
	}

	public float getScale() {
		return 1;
	}

	public void setScale(float f) {
	}

	public BoundingBox getBoundingBox() {
		float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE, maxZ = Float.MIN_VALUE;
		for (Node child : mChildren) {
			BoundingBox box = child.getBoundingBox();
			Point3f low = box.getLow();
			Point3f high = box.getHigh();

			if (low.x < minX) minX = low.x;
			if (low.y < minY) minY = low.y;
			if (low.z < minZ) minZ = low.z;
			if (high.x > maxX) maxX = high.x;			
			if (high.y > maxY) maxY = high.y;			
			if (high.z > maxZ) maxZ = high.z;
		}
		Point3f lowPoint = new Point3f(minX, minY, minZ);
		Point3f highPoint = new Point3f(maxX, maxY, maxZ);
		return new BoundingBox(lowPoint, highPoint);
	}

	public void addChild(Node child) {
		this.mChildren.add(child);
		child.setParent(this);
	}

	public void removeChild(Node child) {
		this.mChildren.remove(child);
		child.setParent(null);
	}

	public Node getParent() {
		return this.mParent;
	}

	public void setParent(Node parent) {
		this.mParent = parent;
	}

	public void setActiveState(boolean b) {
		this.mIsActive = b;
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
