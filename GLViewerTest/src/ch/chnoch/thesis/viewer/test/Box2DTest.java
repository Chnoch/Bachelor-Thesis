package ch.chnoch.thesis.viewer.test;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.GLES11Renderer;
import ch.chnoch.thesis.renderer.GraphSceneManager;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.ShapeNode;
import ch.chnoch.thesis.renderer.TransformGroup;
import ch.chnoch.thesis.renderer.box2d.Box2DBody.TType;
import ch.chnoch.thesis.renderer.interfaces.Node;
import ch.chnoch.thesis.renderer.util.Util;
import android.test.AndroidTestCase;

public class Box2DTest extends AndroidTestCase {

	GraphSceneManager mSceneManager;
	Matrix4f mIdentity, mTransform;

	Shape mShape;
	Node mRoot, mNode1, mNode2;

	public Box2DTest() {
		super();
	}

	public void setUp() {
		mSceneManager = new GraphSceneManager();
		mIdentity = Util.getIdentityMatrix();
		mTransform = Util.getIdentityMatrix();
		mTransform.setTranslation(new Vector3f(2, -0.5f, 0));

		mRoot = new TransformGroup();

		mShape = Util.loadCube(1);
		mNode1 = new ShapeNode(mShape);
		mNode2 = new ShapeNode(mShape);
		mNode2.move(new Vector3f(2, -0.5f, 0));

		mSceneManager.setRoot(mRoot);
		mRoot.addChild(mNode1);
		mRoot.addChild(mNode2);
		mSceneManager.enablePhysicsEngine();
		mNode1.getPhysicsProperties().setType(TType.STATIC);
	}

	public void testSimpleTranslation() {
		mSceneManager.updateScene();
		
		assertTrue(mNode1.getPhysicsProperties().getType()==TType.STATIC);
		assertTrue(!mNode2.getTransformationMatrix().epsilonEquals(mTransform, 0.002f));
//		assertEquals(mNode2.getTransformationMatrix(),mTransform);
		assertEquals(mNode1.getTransformationMatrix(),mIdentity);
	}

}
