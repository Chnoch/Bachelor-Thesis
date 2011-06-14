package ch.chnoch.thesis.viewer.test;


import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.util.Util;

public class SimplePickingTest extends AndroidTestCase {

	GraphSceneManager mSceneManager;
	GLViewer mViewer;
	GLES11Renderer mRenderer;
	
	Matrix4f mIdentity;
	
	Shape shape;
	ShapeNode root;
	
	public SimplePickingTest() {
		super();
	}
	
	public void setUp() {
		mSceneManager = new GraphSceneManager();
		mRenderer = new GLES11Renderer();
		mRenderer.setSceneManager(mSceneManager);
		mIdentity = Util.getIdentityMatrix();
		
//		mSceneManager.getCamera().getCameraMatrix().set(mIdentity);
//		mSceneManager.getFrustum().getProjectionMatrix().set(mIdentity);
//		mRenderer.setViewportMatrix(320, 483);
		
		shape = Util.loadCube(0.5f);
		root = new ShapeNode(shape);
		
		mSceneManager.setRoot(root);
	}

	public void Setup() {
		Matrix4f id = new Matrix4f(1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1);
		
//		assertTrue(mSceneManager.getFrustum().getProjectionMatrix().equals(id));
		
//		assertTrue(mRenderer.getViewportMatrix().equals(id));
		assertEquals(mSceneManager.getRoot(), root);
		assertTrue(root.getChildren() == null);
		assertEquals(root.getShape(), shape);
	}

	public void ProjectionInverse() {
		Matrix4f proj = mSceneManager.getFrustum().getProjectionMatrix();
		proj.mul(mSceneManager.getCamera().getCameraMatrix());
		Matrix4f proj_inv = new Matrix4f(mIdentity);
		proj.invert(proj_inv);
		
		proj_inv.invert();
		
		Vector3f x = new Vector3f(1,0,0);
		Vector3f x_or = new Vector3f(1,0,0);
		Vector3f y = new Vector3f(0,1,0);
		Vector3f y_or = new Vector3f(0,1,0);
		Vector3f z = new Vector3f(0,0,1);
		Vector3f z_or = new Vector3f(0,0,1);
		
		assertTrue(proj != proj_inv);
		
		proj.transform(x);
		proj.transform(y);
		proj.transform(z);
		
		proj_inv.transform(x);
		proj_inv.transform(y);
		proj_inv.transform(z);
		
		assertEquals(x, x_or);
		assertEquals(y, y_or);
		assertEquals(z, z_or);
		assertEquals(proj, proj_inv);
	}
	/*
	public void testBoundingBox() {
		Vector3f one = new Vector3f(1, 1, 1);
		Vector3f minusOne = new Vector3f(-1, -1, -1);
		
		assertEquals(shape.getBoundingBox().getHigh(), one);
		assertEquals(shape.getBoundingBox().getLow(), minusOne);
	}*/
	
	
	public void testPicking() {
		Ray inter = mViewer.unproject(160, 240);
//		assertTrue(inter.hit);
//		assertEquals(inter.node, root);
//		assertNotNull(inter.hitPoint);
		
//		inter = Util.unproject(81, 240, mRenderer);
//		assertTrue(inter.hit);
//		assertEquals(inter.node, root);
		
//		inter = Util.unproject(0, 0, mRenderer);
//		assertTrue(inter.hit);
//		assertEquals(inter.node, root);
////		assertNotNull(inter.hitPoint);
		
		inter = mViewer.unproject(-1, -1);
//		assertTrue(inter.hit);
//		assertEquals(inter.node, root);
//		assertNotNull(inter.hitPoint);
//		
//		assertFalse(mViewer.unproject(-1, 0).hit);
//		assertFalse(mViewer.unproject(0, -1).hit);
//		assertFalse(mViewer.unproject(79, 240).hit);
////		assertFalse(Util.unproject(-2, -72, mRenderer).hit);
//		assertFalse(Util.unproject(10, 0, mRenderer).hit);
	}
	
	public void BoundingBox() {
		Ray ray = new Ray(new Vector3f(0.003125012f, -0.0041407943f, 16f), new Vector3f(0,0,-2));
//		BoundingBox box = new BoundingBox(new Vector3f(-0.5f,-0.5f,-0.5f), new Vector3f(0.5f,0.5f,0.5f));
		
//		assertTrue(box.intersect(ray).hit);
	}
	
}