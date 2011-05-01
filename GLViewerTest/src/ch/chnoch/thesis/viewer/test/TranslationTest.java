package ch.chnoch.thesis.viewer.test;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.test.AndroidTestCase;

import ch.chnoch.thesis.renderer.GLRenderer10;
import ch.chnoch.thesis.renderer.GraphSceneManager;
import ch.chnoch.thesis.renderer.Plane;
import ch.chnoch.thesis.renderer.Ray;
import ch.chnoch.thesis.renderer.RayShapeIntersection;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.ShapeNode;
import ch.chnoch.thesis.renderer.Trackball;
import ch.chnoch.thesis.renderer.util.Util;

public class TranslationTest extends AndroidTestCase {
	GraphSceneManager mSceneManager;
	GLRenderer10 mRenderer;
	Matrix4f mIdentity;
	Shape shape;
	ShapeNode root;
	Plane mPlane;
	float epsilon = 0.00001f;

	public TranslationTest() {
		super();
		setUp();
	}

	public void setUp() {
		mSceneManager = new GraphSceneManager();
		mRenderer = new GLRenderer10(getContext());
		mRenderer.setSceneManager(mSceneManager);
		mIdentity = Util.getIdentityMatrix();

		// mSceneManager.getCamera().getCameraMatrix().set(mIdentity);
		// mSceneManager.getFrustum().getProjectionMatrix().set(mIdentity);
		// mRenderer.setViewportMatrix(320, 480);

		shape = Util.loadCube(1f);
		root = new ShapeNode(shape);
		mPlane = new Plane();
		mSceneManager.setRoot(root);
		root.initTranslationMatrix(mIdentity);
	}

	public void testOnNodeTranslation() {
		mPlane.setNode(root);

		Vector3f prev = new Vector3f(0, 0, 0);
		Vector3f cur = new Vector3f(1, 0, 0);

		mPlane.update(cur, prev);

		Matrix4f t = Util.getIdentityMatrix();
		t.setTranslation(new Vector3f(1, 0, 0));
		assertEquals(root.getTranslationMatrix(), t);
	}

	public void testOffNodeTranslation() {
		mPlane.setNode(root);

		Vector3f prev = new Vector3f(-3, -1, 0);
		Vector3f cur = new Vector3f(4, 2, 0);

		mPlane.update(cur, prev);

		Matrix4f t = Util.getIdentityMatrix();
		t.setTranslation(new Vector3f(7, 3, 0));
		assertEquals(root.getTranslationMatrix(), t);
	}

	public void testHitPoint() {
		mPlane.setNode(root);

		Ray ray = new Ray(new Vector3f(0, 0, 5), new Vector3f(0, 0, -1));
		RayShapeIntersection intersection = mPlane.intersect(ray);

		assertTrue(intersection.hit);
		assertTrue(new Vector3f(0, 0, 4).epsilonEquals(intersection.hitPoint, epsilon));
	}

	public void testDirectTranslation() {
		mPlane.setNode(root);

		Ray rayPrev = new Ray(new Vector3f(10, 0, 5), new Vector3f(0, 0, -1));
		Ray rayCur = new Ray(new Vector3f(0, 3, 5), new Vector3f(0, 0, -1));
		RayShapeIntersection intPrev = mPlane.intersect(rayPrev);
		RayShapeIntersection intCur = mPlane.intersect(rayCur);
		
		
		assertTrue(intPrev.hit);
		assertTrue(intCur.hit);
//		assertTrue(new Vector3f(10,0,1).epsilonEquals(intPrev.hitPoint, epsilon));
//		assertTrue(new Vector3f(0,3,1).epsilonEquals(intCur.hitPoint, epsilon));
		assertEquals(new Vector3f(10,0,1), intPrev.hitPoint);
		assertEquals(new Vector3f(0,3,1), intCur.hitPoint);
		
		mPlane.update(intCur.hitPoint, intPrev.hitPoint);
		
		Matrix4f t = Util.getIdentityMatrix();
		t.setTranslation(new Vector3f(-10,3,0));
		assertEquals(root.getTranslationMatrix(), t);
	}
	
	public void Plane() {
		mPlane.setNode(root);
		Vector3f pointOnPlane = new Vector3f(-1,-1,-1);
		Vector3f normal = new Vector3f();
		
	}
}
