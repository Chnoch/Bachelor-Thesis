package ch.chnoch.thesis.renderer;

import javax.vecmath.Matrix4f;

public class TransformGroup extends Group {

    private Matrix4f transformationMatrix;
    
    public TransformGroup() {
        super();
    }
    
    public void setTransformationMatrix(Matrix4f t) {
        this.transformationMatrix = t;
    }
    
    public Matrix4f getTransformationMatrix() {
        return this.transformationMatrix;
    }
    
}
