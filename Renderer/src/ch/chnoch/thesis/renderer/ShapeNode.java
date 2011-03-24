package ch.chnoch.thesis.renderer;

public class ShapeNode extends Leaf {

    private Shape mShape;
    
    public ShapeNode() {
        super();
    }
    
    public void setShape(Shape shape) {
        this.mShape = shape;
    }
    
    public Shape getShape() {
        return this.mShape;
    }

}
