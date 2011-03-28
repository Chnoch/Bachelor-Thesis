package ch.chnoch.thesis.renderer;

import java.util.List;

import javax.vecmath.Matrix4f;

public interface Node {
    
    public Matrix4f getTransformationMatrix();
    public void setTransformationMatrix(Matrix4f t);
    public Shape getShape();
    public void setShape(Shape shape);
    public List<Node> getChildren();
    public void addChild(Node child);
}
