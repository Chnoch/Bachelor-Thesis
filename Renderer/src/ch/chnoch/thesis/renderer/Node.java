package ch.chnoch.thesis.renderer;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public interface Node {

	public Matrix4f getCompleteTransformationMatrix();
    public Matrix4f getTransformationMatrix();
    public void setTransformationMatrix(Matrix4f t);
    public Shape getShape();
    public void setShape(Shape shape);
    public BoundingBox getBoundingBox();
    public List<Node> getChildren();
    public void addChild(Node child);
    public Node getParent();
    public void setParent(Node parent);
}
