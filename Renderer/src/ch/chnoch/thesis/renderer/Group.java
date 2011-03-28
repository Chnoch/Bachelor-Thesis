package ch.chnoch.thesis.renderer;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Matrix4f;

public abstract class Group implements Node {

	public Node parent;
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
    
    public Matrix4f getTransformationMatrix() {
        return null;
    }
    
    public BoundingBox getBoundingBox() {
    	return null;
    }
    
    public void addChild(Node child) {
        this.children.add(child);
        child.setParent(this);
    }
    
    public void removeChild(Node child) {
        this.children.remove(child);
        child.setParent(null);
    }
    
    public Node getParent() {
    	return this.parent;
    }
    
    public void setParent(Node parent) {
    	this.parent = parent;
    }
    

}
