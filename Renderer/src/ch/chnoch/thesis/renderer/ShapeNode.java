package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.util.Log;

import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.Util;

public class ShapeNode extends Leaf {

	private Shape mShape;
	private BoundingBox mBoundingBox;
	private Material mMaterial;

	public ShapeNode(Shape shape) {
		super();
		mShape = shape;
		mBoundingBox = mShape.getBoundingBox().clone();
		setTransformationMatrix();
	}

	public void setShape(Shape shape) {
		this.mShape = shape;
	}

	public Shape getShape() {
		return this.mShape;
	}
	
	public void setMaterial(Material material) {
		mMaterial = material;
	}
	
	public Material getMaterial() {
		return mMaterial;
	}

	public BoundingBox getBoundingBox() {
		return mBoundingBox.update(getCompleteTransformationMatrix());
	}

	public void initTranslationMatrix(Matrix4f t) {
		mTranslationMatrix.set(t);
		setTransformationMatrix();
	}

	public void initRotationMatrix(Matrix4f t) {
		mRotationMatrix.set(t);
		setTransformationMatrix();
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
	
	public void move(Vector3f v) {
		Matrix4f t = getTranslationMatrix();
		Matrix4f move = new Matrix4f();
		move.setTranslation(v);
		t.add(move);
		Log.d("Box2dIntegration", "Translation: " + t.toString());
		setTranslationMatrix(t);
	}
	
	public void rotZ(float angle) {
		Matrix4f t = getRotationMatrix();
		t.rotZ(angle);
		setRotationMatrix(t);
		Log.d("Box2dIntegration", "Rotation: " + t.toString());
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Light getLight() {
		return null;
	}
	
	public void setLight(Light light) {
		//nothing is set, as this is no light
	}
	
	/**
	 * For now this only tests on the Shape as a cube. Will need
	 * to generalize that to work with any shape.
	 * @param ray
	 * @return the intersection
	 */
	public RayShapeIntersection intersect(Ray ray) {
		RayShapeIntersection intersection;
		
		// Test against BoundingBox for fast check
		intersection = this.getBoundingBox().hitPoint(ray);
		if (intersection.hit) {
			Log.d("ShapeNode", "Hit Bounding Box: " + getBoundingBox().toString());
			// Test against Shape if BoundingBox is hit
			intersection = mShape.intersect(ray, getCompleteTransformationMatrix());
			// if shape ist hit
			if (intersection.hit) {
				Log.d("ShapeNode", "Hit Shape at: " + intersection.hitPoint.toString());
				intersection.node = this;
			}
		} 
		
		return intersection;
	}

	/*
	 * Private Methods
	 */

	private void updateBoundingBox() {
		mBoundingBox.setUpdated();
	}

}
