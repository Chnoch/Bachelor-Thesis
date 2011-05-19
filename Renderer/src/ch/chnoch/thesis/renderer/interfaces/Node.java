package ch.chnoch.thesis.renderer.interfaces;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.box2d.Box2DBody;
import ch.chnoch.thesis.renderer.box2d.Box2DWorld;
import ch.chnoch.thesis.renderer.interfaces.Node;

import ch.chnoch.thesis.renderer.BoundingBox;
import ch.chnoch.thesis.renderer.Light;
import ch.chnoch.thesis.renderer.Material;
import ch.chnoch.thesis.renderer.Ray;
import ch.chnoch.thesis.renderer.RayShapeIntersection;
import ch.chnoch.thesis.renderer.Shape;

import ch.chnoch.thesis.renderer.interfaces.Node;

public interface Node {

	public Matrix4f getCompleteTransformationMatrix();
    public Matrix4f getTranslationMatrix();
    public void initTranslationMatrix(Matrix4f t);
    public void setTranslationMatrix(Matrix4f t);
    public Matrix4f getRotationMatrix();
    public void initRotationMatrix(Matrix4f t);
    public void setRotationMatrix(Matrix4f t);
    public Matrix4f getTransformationMatrix();
    
    public Shape getShape();
    public void setShape(Shape shape);
    public Material getMaterial();
    public void setMaterial(Material material);
    
    public BoundingBox getBoundingBox();
    public List<Node> getChildren();
    public void addChild(Node child);
    public Node getParent();
    public void setParent(Node parent);
    
    public RayShapeIntersection intersect(Ray ray);
	public void enablePhysicsProperties(Box2DWorld world);
	public void updatePhysics();
	public Box2DBody getPhysicsProperties();
}
