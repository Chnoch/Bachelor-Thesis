package ch.chnoch.thesis.viewer.test;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.test.AndroidTestCase;

import ch.chnoch.thesis.renderer.GLES11Renderer;
import ch.chnoch.thesis.renderer.GraphSceneManager;
import ch.chnoch.thesis.renderer.Plane;
import ch.chnoch.thesis.renderer.Ray;
import ch.chnoch.thesis.renderer.RayShapeIntersection;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.ShapeNode;
import ch.chnoch.thesis.renderer.util.Util;

public class TranslationTest extends AndroidTestCase {
	GraphSceneManager mSceneManager;
	GLES11Renderer mRenderer;
	Matrix4f mIdentity;
	Shape shape;
	ShapeNode root;
	Plane mPlane;
	Vector3f pointOnPlane, normal;
	float epsilon = 0.00001f;

	public TranslationTest() {
		super();
		setUp();
	}

	public void setUp() {
		mSceneManager = new GraphSceneManager();
		mRenderer = new GLES11Renderer();
		mRenderer.setSceneManager(mSceneManager);
		mIdentity = Util.getIdentityMatrix();

		// mSceneManager.getCamera().getCameraMatrix().set(mIdentity);
		// mSceneManager.getFrustum().getProjectionMatrix().set(mIdentity);
		// mRenderer.setViewportMatrix(320, 480);

		shape = Util.loadCube(1f);
		root = new ShapeNode(shape);
		pointOnPlane = new Vector3f();
		normal = new Vector3f(1, 0, 0);
		mPlane = new Plane(pointOnPlane, normal);
		mPlane.setNode(root);
		mSceneManager.setRoot(root);
//		root.initTranslationMatrix(mIdentity);
	}

	public void testOnNodeTranslation() {
		Vector3f prev = new Vector3f(0, 0, 0);
		Vector3f cur = new Vector3f(1, 0, 0);

		mPlane.update(cur, prev);

		Matrix4f t = Util.getIdentityMatrix();
		t.setTranslation(new Vector3f(1, 0, 0));
		assertEquals(root.getTranslationMatrix(), t);
	}

	public void testOffNodeTranslation() {
		Vector3f prev = new Vector3f(-3, -1, 0);
		Vector3f cur = new Vector3f(4, 2, 0);

		mPlane.update(cur, prev);

		Matrix4f t = Util.getIdentityMatrix();
		t.setTranslation(new Vector3f(7, 3, 0));
		assertEquals(root.getTranslationMatrix(), t);
	}

	public void testHitPoint() {
		Ray ray = new Ray(new Vector3f(5, 2, 0), new Vector3f(-1, 0, 0));
		RayShapeIntersection intersection = mPlane.intersect(ray);

		assertTrue(intersection.hit);
		assertTrue(new Vector3f(0, 2, 0).epsilonEquals(intersection.hitPoint,
				epsilon));
	}

	public void testDirectTranslation() {
		Ray rayPrev = new Ray(new Vector3f(10, 0, 0), new Vector3f(-1, 0, 0));
		Ray rayCur = new Ray(new Vector3f(10, 5, 2), new Vector3f(-1, 0, 0));
		RayShapeIntersection intPrev = mPlane.intersect(rayPrev);
		RayShapeIntersection intCur = mPlane.intersect(rayCur);

		assertTrue(intPrev.hit);
		assertTrue(intCur.hit);
		assertEquals(new Vector3f(0, 0, 0), intPrev.hitPoint);
		assertEquals(new Vector3f(0, 5, 2), intCur.hitPoint);

		mPlane.update(intCur.hitPoint, intPrev.hitPoint);

		Matrix4f t = Util.getIdentityMatrix();
		t.setTranslation(new Vector3f(0, 5, 0));
		assertEquals(root.getTranslationMatrix(), t);
	}

	public void testClosestPlane() {

		Ray ray = new Ray(new Vector3f(10, 0, 0), new Vector3f(-1, 0, 0));
		Plane closestPlane = findClosestPlane(ray);
		assertEquals(closestPlane.getPointOnPlane().x, 1.f);
		
		ray.getDirection().set(1,0,0);
		ray.getOrigin().set(-10,0,0);
		
		closestPlane = findClosestPlane(ray);
		assertEquals(closestPlane.getPointOnPlane().x, -1.f);
		
		Ray ray2 = new Ray();
		ray2.getDirection().set(0,1,0);
		ray2.getOrigin().set(0,-10,0);
		
		closestPlane = findClosestPlane(ray2);
		assertEquals(closestPlane.getPointOnPlane().y, -1.f);
	}
	
	private Plane findClosestPlane(Ray ray) {
		List<Plane> planes = null;
		Plane closestPlane = null;
		RayShapeIntersection tempInter = new RayShapeIntersection();
		Vector3f tempVec = new Vector3f();
		// Initialize the closest Vector at Infinity
		float tempClosestDist = Float.MAX_VALUE;
		for (Plane plane : planes) {
			tempInter = plane.intersect(ray);
			if (tempInter.hit) {
				tempVec.sub(tempInter.hitPoint, ray.getOrigin());
				if (tempVec.length() < tempClosestDist) {
					tempClosestDist = tempVec.length();
					closestPlane = plane;
				}
			}
		}
		return closestPlane;
	}
}
