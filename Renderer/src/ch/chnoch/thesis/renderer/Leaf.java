package ch.chnoch.thesis.renderer;

import java.util.List;

import javax.vecmath.Matrix4f;

public abstract class Leaf implements Node {

    protected Matrix4f transformationMatrix;
    
    public List<Node> getChildren() {
        return null;
    }
    
    public void addChild(Node child) {
    }

    public Matrix4f getTransformationMatrix() {
        return this.transformationMatrix;
    }
    
    public void setTransformationMatrix(Matrix4f t) {
        this.transformationMatrix = t;
    }
        
}
