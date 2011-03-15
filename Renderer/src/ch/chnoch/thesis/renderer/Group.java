package ch.chnoch.thesis.renderer;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Matrix4f;

public abstract class Group implements Node {

    protected List<Node> children;
    
    public Group() {
        super();
        children = new LinkedList<Node>();
    }
    
    public List<Node> getChildren() {
        return this.children;
    }

    public Shape getShape() {
        return null;
    }

    public void setShape(Shape shape) {
    }
    
    public Light getLight() {
        return null;
    }
    
    public void setLight(Light light){
    }

    public Matrix4f getTransformationMatrix() {
        return null;
    }
    
    public void addChild(Node child) {
        this.children.add(child);
    }
    
    public void removeChild(Node child) {
        this.children.remove(child);
    }

}
