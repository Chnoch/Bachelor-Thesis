package ch.chnoch.thesis.viewer.test;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.*;
import android.content.Context;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

public class ExtendedPickingTest extends AndroidTestCase {
	GraphSceneManager mSceneManager;
	GLViewer mViewer;
	GLES11Renderer mRenderer;
	Context context;
	
	Matrix4f mIdentity, mMove;
	Vector3f zero;
	
	Shape shape;
	Node root, shapeNode, shapeNode2;
	
	public ExtendedPickingTest() {
		super();
	}

	public void setUp() {
		context = new MockContext();
		
		mSceneManager = new GraphSceneManager();
		mRenderer = new GLES11Renderer();
		mRenderer.setSceneManager(mSceneManager);
		mIdentity = Util.getIdentityMatrix();
		zero = new Vector3f(0,0,0);
		
//		mSceneManager.getCamera().getCameraMatrix().set(mIdentity);
//		mSceneManager.getFrustum().getProjectionMatrix().set(mIdentity);
		mRenderer.setViewportMatrix(320, 483);
		
		shape = Util.loadCube(1);
		root = new TransformGroup();
		shapeNode = new ShapeNode(shape);
		shapeNode2 = new ShapeNode(shape);
		
		shapeNode2.move(new Vector3f(2,2,2));
		
		shapeNode.setShape(shape);
		shapeNode2.setShape(shape);
		
		root.addChild(shapeNode);
		root.addChild(shapeNode2);
		
		mSceneManager.setRoot(root);
	}
	
	public void testBoundingBoxes() {
		Vector3f vecOne = new Vector3f(1,1,1);
		Vector3f vecNegOne = new Vector3f(-1,-1,-1);
		
		Vector3f vecThree = new Vector3f(3,3,3);
//		Vector3f vecFour = new Vector3f(4,4,4);
		
		assertEquals(shapeNode.getBoundingBox().getLow(), vecNegOne);
		assertEquals(shapeNode.getBoundingBox().getHigh(), vecOne);
		
		assertEquals(shapeNode2.getBoundingBox().getLow(), vecOne);
		assertEquals(shapeNode2.getBoundingBox().getHigh(), vecThree);
	}
	
	/*public void testPicking() {
		RayBoxIntersection in = Util.unproject(0, 0, mRenderer);
		assertTrue(in.hit);
		assertEquals(in.node, shapeNode);
//		assertTrue(!in.hitPoint.equals(zero));

		in = Util.unproject(0.99f, 0.99f, mRenderer);
		assertTrue(in.hit);
		assertEquals(in.node, shapeNode);
//		assertTrue(!in.hitPoint.equals(zero));

		in = Util.unproject(-0.99f, -0.99f, mRenderer);
		assertTrue(in.hit);
		assertEquals(in.node, shapeNode);
//		assertTrue(!in.hitPoint.equals(zero));
		
		in = Util.unproject(1.01f, 1.01f, mRenderer);
		assertTrue(in.hit);
		assertEquals(in.node, shapeNode2);
//		assertTrue(!in.hitPoint.equals(zero));
		
		in = Util.unproject(2, 2, mRenderer);
		assertTrue(in.hit);
		assertEquals(in.node, shapeNode2);
//		assertTrue(!in.hitPoint.equals(zero));
		
		in = Util.unproject(2.99f, 2.99f, mRenderer);
		assertTrue(in.hit);
		assertEquals(in.node, shapeNode2);
//		assertTrue(!in.hitPoint.equals(zero));


		in = Util.unproject(-2, -2, mRenderer);
		assertFalse(in.hit);

		in = Util.unproject(4, 4, mRenderer);
		assertFalse(in.hit);
	}*/
}
