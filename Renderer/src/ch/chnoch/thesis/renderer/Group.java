package ch.chnoch.thesis.renderer;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;

import ch.chnoch.thesis.renderer.util.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class Group.
 */
public abstract class Group implements Node {

	/** The m parent. */
	protected Node mParent;
	
	/** The m children. */
	protected List<Node> mChildren;
	
	/** The m is active. */
	protected boolean mIsActive = true;

	/** The m transformation matrix. */
	protected Matrix4f mTranslationMatrix, mRotationMatrix,
			mTransformationMatrix;

	/**
	 * Instantiates a new group.
	 */
	public Group() {
		super();
		mChildren = new LinkedList<Node>();
		mTranslationMatrix = Util.getIdentityMatrix();
		mRotationMatrix = Util.getIdentityMatrix();
		setTransformationMatrix();
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getChildren()
	 */
	public List<Node> getChildren() {
		return this.mChildren;
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getShape()
	 */
	public Shape getShape() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#setShape(ch.chnoch.thesis.renderer.Shape)
	 */
	public void setShape(Shape shape) {
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getScale()
	 */
	public float getScale() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#setScale(float)
	 */
	public void setScale(float f) {
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getBoundingBox()
	 */
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

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#addChild(ch.chnoch.thesis.renderer.interfaces.Node)
	 */
	public void addChild(Node child) {
		this.mChildren.add(child);
		child.setParent(this);
	}

	/**
	 * Removes the child.
	 *
	 * @param child the child
	 */
	public void removeChild(Node child) {
		this.mChildren.remove(child);
		child.setParent(null);
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getParent()
	 */
	public Node getParent() {
		return this.mParent;
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#setParent(ch.chnoch.thesis.renderer.interfaces.Node)
	 */
	public void setParent(Node parent) {
		this.mParent = parent;
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#setActiveState(boolean)
	 */
	public void setActiveState(boolean b) {
		this.mIsActive = b;
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getTranslationMatrix()
	 */
	public Matrix4f getTranslationMatrix() {
		return this.mTranslationMatrix;
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#setTranslationMatrix(javax.vecmath.Matrix4f)
	 */
	public void setTranslationMatrix(Matrix4f t) {
		this.mTranslationMatrix = t;
		setTransformationMatrix();
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getRotationMatrix()
	 */
	public Matrix4f getRotationMatrix() {
		return this.mRotationMatrix;
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#setRotationMatrix(javax.vecmath.Matrix4f)
	 */
	public void setRotationMatrix(Matrix4f t) {
		this.mRotationMatrix = t;
		setTransformationMatrix();
	}

	/**
	 * Sets the transformation matrix.
	 */
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

	/**
	 * Inits the translation matrix.
	 *
	 * @param t the t
	 */
	public void initTranslationMatrix(Matrix4f t) {
		setTranslationMatrix(t);
	}

	/**
	 * Inits the rotation matrix.
	 *
	 * @param t the t
	 */
	public void initRotationMatrix(Matrix4f t) {
		setRotationMatrix(t);
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getTransformationMatrix()
	 */
	public Matrix4f getTransformationMatrix() {
		return this.mTransformationMatrix;
	}

	/* (non-Javadoc)
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getCompleteTransformationMatrix()
	 */
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
