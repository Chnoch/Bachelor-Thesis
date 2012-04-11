package ch.chnoch.thesis.renderer.interfaces;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.BoundingBox;
import ch.chnoch.thesis.renderer.GraphSceneManager;
import ch.chnoch.thesis.renderer.Leaf;
import ch.chnoch.thesis.renderer.Material;
import ch.chnoch.thesis.renderer.Ray;
import ch.chnoch.thesis.renderer.RayShapeIntersection;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.box2d.Box2DBody;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;

/**
 * A Node is an element in a {@link GraphSceneManager}. The scene manager can
 * contain several nodes. There are two different types, the {@link Group} and
 * {@link Leaf}. Every group can contain several child nodes. Every leaf should
 * contain some element that can be used in rendering the scene manager. <br>
 * Not all the methods defined in this interface are necessarily implemented in
 * every class that implement it. Some of the methods only make sense for groups
 * and other for leaves. Some are only needed if a physics engine is active.
 */
public interface Node {

	/**
	 * Gets the complete transformation matrix that is calculated by multiplying
	 * all the transformation matrices you get when traveling the graph from the
	 * root to the current node.
	 * 
	 * @return the complete transformation matrix for a node.
	 */
	public Matrix4f getCompleteTransformationMatrix();

	/**
	 * Gets the translation matrix of the individual node.
	 * 
	 * @return the translation matrix of the node
	 */
    public Matrix4f getTranslationMatrix();

	/**
	 * Sets the translation matrix of the individual node.
	 * 
	 * @param t
	 *            the new translation matrix
	 */
    public void setTranslationMatrix(Matrix4f t);

	/**
	 * Gets the rotation matrix of the individual node.
	 * 
	 * @return the rotation matrix of the node
	 */
    public Matrix4f getRotationMatrix();

	/**
	 * Sets the rotation matrix of the individual node.
	 * 
	 * @param t
	 *            the new rotation matrix
	 */
    public void setRotationMatrix(Matrix4f t);

	/**
	 * Gets the scale of the individual node.
	 * 
	 * @return the scale of the node
	 */
	public float getScale();

	/**
	 * Sets the scale of the individual node.
	 * 
	 * @param f
	 *            the new scale
	 */
    public void setScale(float f);

	/**
	 * Moves an individual node by a 3D vector.
	 * 
	 * @param v
	 *            the vector indicating how the node will be moved.
	 */
    public void move(Vector3f v);

	/**
	 * Gets the transformation matrix of the individual node.
	 * 
	 * @return the transformation matrix of the node
	 */
    public Matrix4f getTransformationMatrix();

	/**
	 * Gets the center of the individual node.
	 * 
	 * @return the center of the node
	 */
	public Vector3f getCenter();

	/**
	 * Sets a flag indicating whether this node should be activated for user
	 * interaction. If it is not, it can't be selected and manipulated by the
	 * user.
	 * 
	 * @param b
	 *            the active state
	 */
    public void setActiveState(boolean b);
    
	/**
	 * Gets the bounding box of the node. If the node is a {@link Group}, it
	 * will create a bounding box that contains all the children.
	 * 
	 * @return the bounding box
	 */
	public BoundingBox getBoundingBox();

	/*
	 * LEAF METHODS
	 */

	/**
	 * Gets the {@link Shape} associated to a node. This method is only used in
	 * classes extending {@link Leaf}.
	 * 
	 * @return the shape
	 */
    public Shape getShape();

	/**
	 * Sets the {@link Shape} associated to a node. This method is only used in
	 * classes extending {@link Leaf}.
	 * 
	 * @param shape
	 *            the new shape
	 */
    public void setShape(Shape shape);

	/**
	 * Gets the {@link Material} associated to a node. This method is only used
	 * in classes extending {@link Leaf}.
	 * 
	 * @return the material
	 */
    public Material getMaterial();

	/**
	 * Sets the {@link Material} associated to a node. This method is only used
	 * in classes extending {@link Leaf}.
	 * 
	 * @param material
	 *            the new material
	 */
    public void setMaterial(Material material);
    

	/**
	 * Gets all the children of a node. This is only used in classes extending
	 * {@link Group}.
	 * 
	 * @return the children of the node
	 */
    public List<Node> getChildren();

	/**
	 * Adds a child to the node. This is only used in classes extending
	 * {@link Group}.
	 * 
	 * @param child
	 *            the child to be added
	 */
    public void addChild(Node child);

	/**
	 * Removes a child to the node. This is only used in classes extending
	 * {@link Group}.
	 * 
	 * @param child
	 *            the child to be removed
	 * 
	 * @return returns true if the child could be removed successfully and false
	 *         if not
	 */
	public boolean removeChild(Node child);

	/**
	 * Gets the parent of the node.
	 * 
	 * @return the parent node
	 */
    public Node getParent();

	/**
	 * Sets the parent of the node.
	 * 
	 * @param parent
	 *            the new parent node
	 */
    public void setParent(Node parent);

	/**
	 * Intersect a node with a given ray.
	 * 
	 * @param ray
	 *            the ray that needs to be intersected
	 * @return the {@link RayShapeIntersection} that contains information about
	 *         if and where a hit between the node and the ray occurred
	 */
    public RayShapeIntersection intersect(Ray ray);

	/**
	 * Enables the physics properties for this node.
	 * 
	 * @param world
	 *            the object that represents the physics world.
	 */
	public void enablePhysicsProperties(Box2DWorld world);

	/**
	 * Reflects the position change of the node back from the physics world.
	 * This is used to keep the position of the physics world and the visual
	 * world in synchronization.
	 */
	public void updatePositionFromPhysic();

	/**
	 * Gets the physics properties of this node.
	 * 
	 * @return the physics representation of this object
	 */
	public Box2DBody getPhysicsProperties();
}
