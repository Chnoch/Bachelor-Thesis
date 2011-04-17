package ch.chnoch.thesis.viewer.test;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.BoundingBox;
import ch.chnoch.thesis.renderer.GLRenderer10;
import ch.chnoch.thesis.renderer.GraphSceneManager;
import ch.chnoch.thesis.renderer.Ray;
import ch.chnoch.thesis.renderer.RayShapeIntersection;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.ShapeNode;
import ch.chnoch.thesis.renderer.Trackball;
import ch.chnoch.thesis.renderer.util.Util;
import android.test.AndroidTestCase;

public class RotationTest extends AndroidTestCase {

	GraphSceneManager mSceneManager;
	GLRenderer10 mRenderer;
	Matrix4f mIdentity;
	Shape shape;
	ShapeNode root;
	Trackball mTrackball;
	float epsilon = 0.00001f;

	public RotationTest() {
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
		root = new ShapeNode(shape, mSceneManager);
		mTrackball = new Trackball();
		mSceneManager.setRoot(root);
		root.initTranslationMatrix(mIdentity);
	}

	public void testRotation() {

		mTrackball.setNode(root);
		BoundingBox prevBB = root.getBoundingBox();
		Vector3f prev = new Vector3f(-1, 0, 1);
		Vector3f cur = new Vector3f(1, 0, 1);

		mTrackball.update(cur, prev, 1);
		BoundingBox postBB = root.getBoundingBox();
		assertEquals(prevBB, postBB);

		Matrix4f t = new Matrix4f();
		t.rotY(-(float) Math.PI / 2);
		assertTrue(root.getRotationMatrix().epsilonEquals(t, epsilon));
	}

	public void testComplicatedRotation() {
		mTrackball.setNode(root);

		Vector3f prev = new Vector3f(-1, -1, -1);
		Vector3f cur = new Vector3f(1, 1, -1);
		mTrackball.update(cur, prev, 1);

		AxisAngle4f aa = new AxisAngle4f();
		aa.angle = prev.angle(cur);
		aa.x = -1;
		aa.y = 1;
		aa.z = 0;

		Matrix4f t = new Matrix4f();
		t.set(aa);

		assertTrue(root.getRotationMatrix().epsilonEquals(t, epsilon));
	}
	
	public void test90DegreeRotation() {
		mTrackball.setNode(root);
		
		Vector3f prev = new Vector3f(-1, 0, 1);
		Vector3f cur = new Vector3f(-1, 0, -1);
		mTrackball.update(cur, prev, 1);
		
		Matrix4f t = new Matrix4f();
		t.rotY((float) Math.PI / 2);
		assertTrue(root.getRotationMatrix().epsilonEquals(t, epsilon));
//		assertTrue(root.getRotationMatrix().epsilonEquals(t, epsilon));
		
	}
	
	public void test90DegreeVerticalRotation() {
		mTrackball.setNode(root);
		
		Vector3f prev = new Vector3f(-1, -1, 0);
		Vector3f cur= new Vector3f(-1, 1, 0);
		mTrackball.update(cur, prev, 1);
		
		Matrix4f t = new Matrix4f();
		t.rotZ((float) Math.PI / 2);
		assertTrue(root.getRotationMatrix().epsilonEquals(t, epsilon));
//		assertEquals(root.getRotationMatrix(),t);
	}

	public void testProjecting() {
		mTrackball.setNode(root);

		Ray ray = new Ray(new Vector3f(-2, 0, -2), new Vector3f(0, 0, 1));
		RayShapeIntersection intersect = mTrackball.intersect(ray);
		assertTrue(intersect.hit);
		assertEquals(new Vector3f(-1, 0, 0), intersect.hitPoint);

		ray = new Ray(new Vector3f(-1.1f, 0, -2), new Vector3f(0, 0, 1));
		intersect = mTrackball.intersect(ray);
		assertTrue(intersect.hit);
		assertEquals(new Vector3f(-1, 0, 0), intersect.hitPoint);
	}

	public void testHitpoints() {
		mTrackball.setNode(root);

		Ray ray = new Ray(new Vector3f(0, 0, -2), new Vector3f(0, 0, 1));
		RayShapeIntersection intersect = mTrackball.intersect(ray);

		assertTrue(intersect.hit);
		assertEquals(new Vector3f(0, 0, -1), intersect.hitPoint);

		ray = new Ray(new Vector3f(1, 0, -1), new Vector3f(0, 0, 1));
		intersect = mTrackball.intersect(ray);

		assertTrue(intersect.hit);
		assertEquals(new Vector3f(1, 0, 0), intersect.hitPoint);
	}
}
