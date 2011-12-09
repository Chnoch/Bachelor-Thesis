package ch.chnoch.thesis.renderer;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import android.util.Log;

import ch.chnoch.thesis.renderer.box2d.Box2DBody;
import ch.chnoch.thesis.renderer.box2d.Box2DShape;
import ch.chnoch.thesis.renderer.box2d.Box2DShape.Box2DShapeType;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.Util;

public class ShapeNode extends Leaf {

	private Shape mShape;
	private BoundingBox mBoundingBox;
	private Material mMaterial;
	private Box2DBody mBox2DBody;
	private boolean mPhysicsEnabled = false;

	private static final float PTM_RATIO = 1;

	public ShapeNode(Shape shape) {
		super();
		mShape = shape;
		mBoundingBox = mShape.getBoundingBox();
		setTransformationMatrix();
	}
	
	public ShapeNode(Box2DBody body, float depth) {
		mBox2DBody = body;
		
		// create a shape from the Box2DBody
		Box2DShape shape = mBox2DBody.getShape();
		Box2DShapeType type = shape.getType();
		switch (type) {
		case BOX:
			mShape = loadBox(shape, depth);
			break;
		case CIRCLE:
			mShape = loadCircle(shape);
			break;
		default:
			mShape= Util.loadCube(1);
			break;
		}
		
		mBoundingBox = mShape.getBoundingBox();
		move(new Vector3f(body.getCurrentPosition().x, body.getCurrentPosition().y, 0));
		mPhysicsEnabled = true;
		setTransformationMatrix();
	}
	
	private Shape loadBox(Box2DShape shape, float depth) {
		List<Vector2f> coord =  shape.getCoordinates();
		// As found in PolygonShape.class
		Vector2f bottomLeft = coord.get(0);
		Vector2f bottomRight = coord.get(1);
		Vector2f topLeft = coord.get(3);
		
		Vector2f topRight = coord.get(2);
		
		float width = bottomRight.x - bottomLeft.x;
		float height = topLeft.y - bottomLeft.y;
		Log.d("ShapeNode", "width: " + width + " height: " + height);
		
//		return Util.loadCuboid(width, height, depth);
		return Util.loadCuboid(topRight.x, topRight.y, depth);
	}
	
	private Shape loadCircle(Box2DShape shape) {
		float radius = shape.getRadius();
		return Util.loadSphere(20, 20, radius);
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

	public void setTranslationMatrix(Matrix4f t) {
		super.setTranslationMatrix(t);
		updateBoundingBox();
	}

	public void setRotationMatrix(Matrix4f t) {
		super.setRotationMatrix(t);
		updateBoundingBox();
	}

	public void setScale(float scale) {
		super.setScale(scale);
		updateBoundingBox();
	}
	
	public void move(Vector3f v) {

		if (mPhysicsEnabled) {
			Log.d("PhysicsTouchHandler", "Moved ShapeNode with enabledPhysics: x: " + v.x
					+ ", y: " + v.y);
			mBox2DBody.move(v.x, v.y);
		} else {
			Matrix4f t = getTranslationMatrix();
			Matrix4f move = new Matrix4f();
			move.setTranslation(v);
			t.add(move);
			setTranslationMatrix(t);
		}
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
		mBox2DBody = new Box2DBody(position, world, mShape.enableBox2D(), mIsActive,
				mIsActive);

		mPhysicsEnabled = true;
	}

	public void updatePositionFromPhysic() {
		if (mPhysicsEnabled) {
			// Temporarily disable the automatic translation of the
			// graphical model back to the physical one.
			mPhysicsEnabled = false;

//			Vector2f prevPos = mBox2DBody.getPreviousPosition();
			Vector2f curPos = mBox2DBody.getCurrentPosition();

//			Vector3f trans = new Vector3f();
//			trans.x = curPos.x - prevPos.x;
//			trans.y = curPos.y - prevPos.y;
//			trans.z = 0;

//			move(trans);
			Matrix4f trans = getTranslationMatrix();
			trans.setTranslation(new Vector3f(curPos.x, curPos.y, 0));

			mBox2DBody.setPreviousPosition(curPos);

			rotZ(mBox2DBody.getAngle());

			mPhysicsEnabled = true;
		}
	}

	public void destroyJoint() {
		if (mPhysicsEnabled) {
			mBox2DBody.removeJoint();
		}
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * Intersects the given ray with the node. Returns an empty RayShapeIntersection if
	 * the Node is inactive. Otherwise tests the Bounding Box of the shape for quick access
	 * and if it's hit tests against the actual shape.
	 * 
	 * @param ray
	 * @return the intersection
	 */
	public RayShapeIntersection intersect(Ray ray) {
		RayShapeIntersection intersection = new RayShapeIntersection();
		// only test on active nodes
		if (mIsActive) {

			// Test against BoundingBox for fast check
			intersection = this.getBoundingBox().hitPoint(ray);
			if (intersection.hit) {
				Log.d("ShapeNode", "Hit Bounding Box: "
						+ getBoundingBox().toString());
				// Test against Shape if BoundingBox is hit
				intersection = mShape.intersect(ray,
						getCompleteTransformationMatrix());
				// if shape is hit
				if (intersection.hit) {
					Log.d("ShapeNode",
							"Hit Shape at: " + intersection.hitPoint.toString());
					intersection.node = this;
				}
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

	private void rotZ(float angle) {
		Matrix4f t = Util.getIdentityMatrix();
		Matrix4f rot = new Matrix4f();
		rot.rotZ(angle);
		rot.mul(t);
		setRotationMatrix(rot);
	}

}
