package ch.chnoch.thesis.renderer.interfaces;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.BoundingBox;
import ch.chnoch.thesis.renderer.Material;
import ch.chnoch.thesis.renderer.Ray;
import ch.chnoch.thesis.renderer.RayShapeIntersection;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.box2d.Box2DBody;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;

// TODO: Auto-generated Javadoc
/**
 * The Interface Node.
 */
public interface Node {

	/**
	 * Gets the complete transformation matrix.
	 * 
	 * @return the complete transformation matrix
	 */
	public Matrix4f getCompleteTransformationMatrix();

	/**
	 * Gets the translation matrix.
	 * 
	 * @return the translation matrix
	 */
    public Matrix4f getTranslationMatrix();

	/**
	 * Sets the translation matrix.
	 * 
	 * @param t
	 *            the new translation matrix
	 */
    public void setTranslationMatrix(Matrix4f t);

	/**
	 * Gets the rotation matrix.
	 * 
	 * @return the rotation matrix
	 */
    public Matrix4f getRotationMatrix();

	/**
	 * Sets the rotation matrix.
	 * 
	 * @param t
	 *            the new rotation matrix
	 */
    public void setRotationMatrix(Matrix4f t);

	/**
	 * Sets the scale.
	 * 
	 * @param f
	 *            the new scale
	 */
    public void setScale(float f);

	/**
	 * Move.
	 * 
	 * @param v
	 *            the v
	 */
    public void move(Vector3f v);

	/**
	 * Gets the scale.
	 * 
	 * @return the scale
	 */
    public float getScale();

	/**
	 * Gets the transformation matrix.
	 * 
	 * @return the transformation matrix
	 */
    public Matrix4f getTransformationMatrix();

	/**
	 * Gets the center.
	 * 
	 * @return the center
	 */
	public Vector3f getCenter();
    
	/**
	 * Sets the active state.
	 * 
	 * @param b
	 *            the new active state
	 */
    public void setActiveState(boolean b);
    
	/**
	 * Gets the shape.
	 * 
	 * @return the shape
	 */
    public Shape getShape();

	/**
	 * Sets the shape.
	 * 
	 * @param shape
	 *            the new shape
	 */
    public void setShape(Shape shape);

	/**
	 * Gets the material.
	 * 
	 * @return the material
	 */
    public Material getMaterial();

	/**
	 * Sets the material.
	 * 
	 * @param material
	 *            the new material
	 */
    public void setMaterial(Material material);
    
	/**
	 * Gets the bounding box.
	 * 
	 * @return the bounding box
	 */
    public BoundingBox getBoundingBox();

	/**
	 * Gets the children.
	 * 
	 * @return the children
	 */
    public List<Node> getChildren();

	/**
	 * Adds the child.
	 * 
	 * @param child
	 *            the child
	 */
    public void addChild(Node child);

	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
    public Node getParent();

	/**
	 * Sets the parent.
	 * 
	 * @param parent
	 *            the new parent
	 */
    public void setParent(Node parent);
    
	/**
	 * Intersect.
	 * 
	 * @param ray
	 *            the ray
	 * @return the ray shape intersection
	 */
    public RayShapeIntersection intersect(Ray ray);

	/**
	 * Enable physics properties.
	 * 
	 * @param world
	 *            the world
	 */
	public void enablePhysicsProperties(Box2DWorld world);

	/**
	 * Update position from physic.
	 */
	public void updatePositionFromPhysic();

	/**
	 * Gets the physics properties.
	 * 
	 * @return the physics properties
	 */
	public Box2DBody getPhysicsProperties();
}
