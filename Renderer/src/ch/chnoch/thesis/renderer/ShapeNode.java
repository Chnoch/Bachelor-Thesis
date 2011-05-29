package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import android.util.Log;

import ch.chnoch.thesis.renderer.box2d.Box2DBody;
import ch.chnoch.thesis.renderer.box2d.Box2DShape;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.Util;

public class ShapeNode extends Leaf {

	private Shape mShape;
	private BoundingBox mBoundingBox;
	private Material mMaterial;
	private Box2DBody mBox2DBody;

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

	@Override
	public Box2DBody getPhysicsProperties() {
		return mBox2DBody;
	}

	@Override
	public void enablePhysicsProperties(Box2DWorld world) {
		Matrix4f trans = getCompleteTransformationMatrix();
		Vector2f position = new Vector2f();
		position.x = trans.m03;
		position.y = trans.m13;
		mBox2DBody = new Box2DBody(position, world);

		Box2DShape shape = mShape.enableBox2D();
		mBox2DBody.createShape(shape);
	}

	@Override
	public void updatePhysics() {
		Log.d("ShapeNode", "Updating Physics");
		Vector2f prevPos = mBox2DBody.getPreviousPosition();
		Vector2f curPos = mBox2DBody.getCurrentPosition();

		Vector3f trans = new Vector3f();
		trans.x = curPos.x - prevPos.x;
		trans.y = curPos.y - prevPos.y;
		trans.z = 0;
		
		
		
		
		
		
		// Log.d("ShapeNode", "Translation: " + trans.toString());
		move(trans);
		Log.d("ShapeNode", "Translation: " + trans.toString());
		Log.d("ShapeNode", "Rotation: " + mBox2DBody.getAngle());

		mBox2DBody.setPreviousPosition(curPos);
		// Log.d("ShapeNode", "Angle: " + mBox2DBody.getAngle());
		rotZ(mBox2DBody.getAngle());
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * For now this only tests on the Shape as a cube. Will need to generalize
	 * that to work with any shape.
	 * 
	 * @param ray
	 * @return the intersection
	 */
	public RayShapeIntersection intersect(Ray ray) {
		RayShapeIntersection intersection;

		// Test against BoundingBox for fast check
		intersection = this.getBoundingBox().hitPoint(ray);
		if (intersection.hit) {
			Log.d("ShapeNode", "Hit Bounding Box: "
					+ getBoundingBox().toString());
			// Test against Shape if BoundingBox is hit
			intersection = mShape.intersect(ray,
					getCompleteTransformationMatrix());
			// if shape ist hit
			if (intersection.hit) {
				Log.d("ShapeNode",
						"Hit Shape at: " + intersection.hitPoint.toString());
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

	private void move(Vector3f v) {
		Matrix4f t = getTranslationMatrix();
		Matrix4f move = new Matrix4f();
		move.setTranslation(v);
		t.add(move);
		setTranslationMatrix(t);
	}

	private void rotZ(float angle) {
		Matrix4f t = Util.getIdentityMatrix();
		Matrix4f rot = new Matrix4f();
		rot.rotZ(angle);
		rot.mul(t);
		setRotationMatrix(rot);
	}
}
