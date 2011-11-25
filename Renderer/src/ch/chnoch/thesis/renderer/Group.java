package ch.chnoch.thesis.renderer;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.interfaces.Node;

import ch.chnoch.thesis.renderer.interfaces.Node;

import ch.chnoch.thesis.renderer.interfaces.Node;

public abstract class Group implements Node {

	public Node mParent;
    protected List<Node> mChildren;
    protected boolean mIsActive = true;
    
    public Group() {
        super();
        mChildren = new LinkedList<Node>();
    }
    
    public List<Node> getChildren() {
        return this.mChildren;
    }

    public Shape getShape() {
        return null;
    }

    public void setShape(Shape shape) {
    }
    
    public Matrix4f getTranslationMatrix() {
    	return null;
    }
    
    public Matrix4f getRotationMatrix() {
    	return null;
    }
    
    public float getScale() {
    	return 1;
    }
    
    public void setScale(float f) {}
    
    public Matrix4f getTransformationMatrix() {
    	return null;
    }
    
    public BoundingBox getBoundingBox() {
    	return null;
    }
    
    public void addChild(Node child) {
        this.mChildren.add(child);
        child.setParent(this);
    }
    
    public void removeChild(Node child) {
        this.mChildren.remove(child);
        child.setParent(null);
    }
    
    public Node getParent() {
    	return this.mParent;
    }
    
    public void setParent(Node parent) {
    	this.mParent = parent;
    }
    
    public void setActiveState(boolean b) {
    	this.mIsActive = b;
    }
    

}
