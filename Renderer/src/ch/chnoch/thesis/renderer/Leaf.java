package ch.chnoch.thesis.renderer;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.Util;

// TODO: Auto-generated Javadoc
/**
 * The Class Leaf.
 */
public abstract class Leaf implements Node {

	/** The parent. */
	protected Node parent;

	/** The m transformation matrix. */
	protected Matrix4f mTranslationMatrix, mRotationMatrix,
			mTransformationMatrix;

	/** The m scale. */
	protected float mScale;

	/** The m is active. */
	protected boolean mIsActive = true;

	/**
	 * Instantiates a new leaf.
	 */
	public Leaf() {
		super();
		mTransformationMatrix = Util.getIdentityMatrix();
		mRotationMatrix = Util.getIdentityMatrix();
		mTranslationMatrix = Util.getIdentityMatrix();
		mScale = 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getChildren()
	 */
	public List<Node> getChildren() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#addChild(ch.chnoch.thesis.renderer
	 * .interfaces.Node)
	 */
	public void addChild(Node child) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getTransformationMatrix()
	 */
	public Matrix4f getTransformationMatrix() {
		return mTransformationMatrix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getTranslationMatrix()
	 */
	public Matrix4f getTranslationMatrix() {
		return this.mTranslationMatrix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getRotationMatrix()
	 */
	public Matrix4f getRotationMatrix() {
		return this.mRotationMatrix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#setTranslationMatrix(javax.
	 * vecmath.Matrix4f)
	 */
	public void setTranslationMatrix(Matrix4f t) {
		this.mTranslationMatrix = t;
		setTransformationMatrix();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#setRotationMatrix(javax.vecmath
	 * .Matrix4f)
	 */
	public void setRotationMatrix(Matrix4f t) {
		this.mRotationMatrix = t;
		setTransformationMatrix();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getScale()
	 */
	public float getScale() {
		return this.mScale;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#setScale(float)
	 */
	public void setScale(float f) {
		this.mScale = f;
		setTransformationMatrix();
	}

	/**
	 * Sets the transformation matrix.
	 */
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#getCompleteTransformationMatrix
	 * ()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getParent()
	 */
	public Node getParent() {
		return this.parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#setParent(ch.chnoch.thesis.
	 * renderer.interfaces.Node)
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#setActiveState(boolean)
	 */
	public void setActiveState(boolean b) {
		this.mIsActive = b;
	}

}
