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

/**
 * A ShapeNode is a leaf in the GraphSceneManager. This is a node that contains
 * an actual {@link Shape} that will be drawn on screen. The shape can also be
 * associated with other ShapeNodes. In addition a ShapeNode stores the
 * {@link Material} that contains information about the color and texture that
 * the shape has. It also has a {@link BoundingBox} that is mostly used for fast
 * testing for intersection.<br>
 * If a physics engine is attached to the scene manager a ShapeNode stores
 * information about the corresponding physics node. It contains a
 * {@link Box2DBody}, that consists of a physics shape and information about its
 * properties and its position.
 * 
 */
public class ShapeNode extends Leaf {

	private Shape mShape;

	private BoundingBox mBoundingBox;

	private Material mMaterial;

	private Box2DBody mBox2DBody;
	private boolean mPhysicsEnabled = false;

	/**
	 * Instantiates a new shape node with a given shape.
	 * 
	 * @param shape
	 *            the shape that will be used for drawing the shape node on the
	 *            screen
	 */
	public ShapeNode(Shape shape) {
		super();
		mShape = shape;
		mBoundingBox = mShape.getBoundingBox();
		setTransformationMatrix();
	}

	/**
	 * Instantiates a new shape node if a physics node is passed. It extracts
	 * information about the shape type that the physics node contains and
	 * builds a 3D representation of it.
	 * 
	 * @param body
	 *            the corresponding Box2DBody
	 * @param depth
	 *            the depth that might be used if the shape is a box.
	 */
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

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#setShape(ch.chnoch.thesis.renderer
	 * .Shape)
	 */
	public void setShape(Shape shape) {
		this.mShape = shape;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getShape()
	 */
	public Shape getShape() {
		return this.mShape;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#setMaterial(ch.chnoch.thesis
	 * .renderer.Material)
	 */
	public void setMaterial(Material material) {
		mMaterial = material;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getMaterial()
	 */
	public Material getMaterial() {
		return mMaterial;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getBoundingBox()
	 */
	public BoundingBox getBoundingBox() {
		return mBoundingBox.update(getCompleteTransformationMatrix());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.Leaf#setTranslationMatrix(javax.vecmath.Matrix4f
	 * )
	 */
	public void setTranslationMatrix(Matrix4f t) {
		super.setTranslationMatrix(t);
		updateBoundingBox();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.Leaf#setRotationMatrix(javax.vecmath.Matrix4f)
	 */
	public void setRotationMatrix(Matrix4f t) {
		super.setRotationMatrix(t);
		updateBoundingBox();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.Leaf#setScale(float)
	 */
	public void setScale(float scale) {
		super.setScale(scale);
		updateBoundingBox();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#move(javax.vecmath.Vector3f)
	 */
	public void move(Vector3f v) {

		if (mPhysicsEnabled) {
			mBox2DBody.move(v.x, v.y);
		} else {
			Matrix4f t = getTranslationMatrix();
			Matrix4f move = new Matrix4f();
			move.setTranslation(v);
			t.add(move);
			setTranslationMatrix(t);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getPhysicsProperties()
	 */
	@Override
	public Box2DBody getPhysicsProperties() {
		return mBox2DBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#getCenter()
	 */
	@Override
	public Vector3f getCenter() {
		return this.getBoundingBox().getCenter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.interfaces.Node#enablePhysicsProperties(ch.
	 * chnoch.thesis.renderer.box2d.Box2DWorld)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.chnoch.thesis.renderer.interfaces.Node#updatePositionFromPhysic()
	 */
	public void updatePositionFromPhysic() {
		if (mPhysicsEnabled) {
			// Temporarily disable the automatic translation of the
			// graphical model back to the physical one.
			mPhysicsEnabled = false;

			Vector2f curPos = mBox2DBody.getCurrentPosition();

			Matrix4f trans = getTranslationMatrix();
			trans.setTranslation(new Vector3f(curPos.x, curPos.y, 0));

			mBox2DBody.setPreviousPosition(curPos);

			rotZ(mBox2DBody.getAngle());

			mPhysicsEnabled = true;
		}
	}

	/**
	 * Destroy all the joints that the corresponding physics node still has.
	 */
	public void destroyJoint() {
		if (mPhysicsEnabled) {
			mBox2DBody.removeJoint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.chnoch.thesis.renderer.Leaf#setParent(ch.chnoch.thesis.renderer.interfaces
	 * .Node)
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * Intersects the given ray with the node. Returns an empty
	 * RayShapeIntersection if the Node is inactive. Otherwise tests the
	 * Bounding Box of the shape for quick access and if it's hit tests against
	 * the actual shape.
	 * 
	 * @param ray
	 *            the ray
	 * @return the intersection that stores information about whether and if
	 *         where a hit occurred
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

	/**
	 * Set the flag in the bounding box that it needs an update.
	 */
	private void updateBoundingBox() {
		mBoundingBox.setUpdated();
	}

	/**
	 * Rotates the node around the z-axis
	 * 
	 * @param angle
	 *            the rotation angle
	 */
	private void rotZ(float angle) {
		Matrix4f t = Util.getIdentityMatrix();
		Matrix4f rot = new Matrix4f();
		rot.rotZ(angle);
		rot.mul(t);
		setRotationMatrix(rot);
	}

	/**
	 * A helper method that loads a box from a given {@link Box2DShape} with a
	 * certain depth.
	 * 
	 * @param shape
	 *            the corresponding physics shape
	 * @param depth
	 *            the depth
	 * @return the 3D shape
	 */
	private Shape loadBox(Box2DShape shape, float depth) {
		List<Vector2f> coord = shape.getCoordinates();
		// As found in PolygonShape.class
		Vector2f topRight = coord.get(2);
		return Util.loadCuboid(topRight.x, topRight.y, depth);
	}

	/**
	 * A helper method that loads a circle from a given {@link Box2DShape}
	 * 
	 * @param shape
	 *            the corresponding physics shape
	 * @return the 3D shape
	 */
	private Shape loadCircle(Box2DShape shape) {
		float radius = shape.getRadius();
		return Util.loadSphere(20, 20, radius);
	}
}
