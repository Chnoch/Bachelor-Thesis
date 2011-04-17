package ch.chnoch.thesis.renderer.interfaces;

import java.util.List;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.Node;

import ch.chnoch.thesis.renderer.BoundingBox;
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
    public BoundingBox getBoundingBox();
    public List<Node> getChildren();
    public void addChild(Node child);
    public Node getParent();
    public void setParent(Node parent);
}
