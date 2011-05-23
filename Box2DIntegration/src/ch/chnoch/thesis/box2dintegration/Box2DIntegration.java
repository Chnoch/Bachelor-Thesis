package ch.chnoch.thesis.box2dintegration;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.jbox2d.collision.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.box2d.Box2DBody.TType;
import ch.chnoch.thesis.renderer.interfaces.*;
import ch.chnoch.thesis.renderer.util.*;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class Box2DIntegration extends Activity implements OnClickListener {

	private GraphSceneManager mSceneManager;
	private Shape mShape;
	private Node mNode, mRoot;
	private RenderContext mRenderer;
	private GLSurfaceView mViewer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSceneManager = new GraphSceneManager();
		mRenderer = new GLRenderer10(getApplication());
		mRenderer.setSceneManager(mSceneManager);
		mViewer = new GLViewer(this, mRenderer);
		
		mSceneManager.getCamera()
				.setCenterOfProjection(new Vector3f(0, 15, 20));
		mSceneManager.getFrustum().setFarPlane(300);
		
		createLights();
		
		mShape = Util.loadCube(1); 
		Shape shape = Util.loadCube(3);
//		Shape ground = Util.loadGround();

		mRoot = new TransformGroup();
		mSceneManager.setRoot(mRoot);

		TransformGroup group = new TransformGroup();
		Matrix4f trans = Util.getIdentityMatrix();
		trans.setTranslation(new Vector3f(0, 3, 0));
		group.setTranslationMatrix(trans);
		mRoot.addChild(group);
		
		mNode = new ShapeNode(shape);
		group.addChild(mNode);
		
		TransformGroup smallerGroup = new TransformGroup();
		trans = Util.getIdentityMatrix();
		trans.setTranslation(new Vector3f(0,9,0));
		smallerGroup.setTranslationMatrix(trans);
		group.addChild(smallerGroup);
		
		Node node = new ShapeNode(mShape);
		trans = Util.getIdentityMatrix();
		trans.setTranslation(new Vector3f(1.5f, 0,0));
		node.setTranslationMatrix(trans);
		smallerGroup.addChild(node);
		
		Node node2 = new ShapeNode(mShape);
		trans = Util.getIdentityMatrix();
		trans.setTranslation(new Vector3f(-1.5f, 0,0));
		node.setTranslationMatrix(trans);
		smallerGroup.addChild(node2);
		
		Node node3 = new ShapeNode(mShape);
		trans = Util.getIdentityMatrix();
		trans.setTranslation(new Vector3f(0,1.5f,0));
		node.setTranslationMatrix(trans);
//		smallerGroup.addChild(node3);

		
		Node node4 = new ShapeNode(mShape);
		trans = Util.getIdentityMatrix();
		trans.setTranslation(new Vector3f(-0.5f,3,0));
		node.setTranslationMatrix(trans);
//		smallerGroup.addChild(node4);
		
		Material mat = new Material();

		mat.shininess = 5;
		mat.mAmbient.set(1, 0, 0);
		mat.mDiffuse.set(1f, 0, 0);
		mat.mSpecular.set(1f, 0f, 0f);

		mNode.setMaterial(mat);
		node.setMaterial(mat);
		node2.setMaterial(mat);
		
		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);
		
		mSceneManager.enablePhysicsEngine();
		mViewer.setOnClickListener(this);
		
		mNode.getPhysicsProperties().setType(TType.STATIC);

		/*
		Vec2 gravity = new Vec2(0.0f, -10.0f);
		boolean doSleep = true;
		AABB completeBoundingBox = new AABB(new Vec2(-100f, -100f), new Vec2(
				100f, 100f));
		world = new World(completeBoundingBox, gravity, doSleep);

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(0.0f, -10.0f);
		Body groundBody = world.createBody(groundBodyDef);
		PolygonDef groundBox = new PolygonDef();
		groundBox.setAsBox(50f, 10f);
		groundBody.createShape(groundBox);

		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(0.0f, 4.0f);
		body = world.createBody(bodyDef);
		body.m_type = Body.e_dynamicType;

		PolygonDef shapeDef = new PolygonDef();
		shapeDef.density = 1.0f;
		shapeDef.friction = 0.3f;
		shapeDef.setAsBox(1.f, 1.f);

		org.jbox2d.collision.Shape dynamicBox = body.createShape(shapeDef);

		body.createShape(shapeDef);
		body.setMassFromShapes();
		*/
	}
	
	private void createLights() {

		Light light = new Light();
		light.type = Light.Type.DIRECTIONAL;
		light.position.set(5, 5, 5);
		light.direction.set(1, 1, 1);
		light.specular.set(1, 1, 1);
		light.ambient.set(0.4f, 0.4f, 0.4f);
		light.diffuse.set(0.3f, 0.3f, 0.3f);

		mSceneManager.addLight(light);
	}

	private class Simulation implements Runnable {
		public void run() {
			/*
			float timeStep = 1.0f / 60.f;

			int velocityIterations = 6;
			int positionIterations = 2;
			Vec2 previous = body.getPosition();
			Vec2 position;
			Vec2 difference;

			do {
				world.step(timeStep, velocityIterations + positionIterations);
				position = body.getPosition();
				float angle = body.getAngle();
				difference = position.sub(previous);
				mNode.move(new Vector3f(difference.x, difference.y, 0));
				mNode.rotZ(angle);

				mViewer.requestRender();

				previous.set(position);

				Log.d("Box2dIntegration", position.x + ", " + position.y
						+ ", angle: " + angle);
			} while (difference.x > 0.001f && difference.y > 0.001f);
			*/
			for (int i=0; i<10000; i++) {
				mSceneManager.updateScene();
				mViewer.requestRender();
			}
		}
	}

	public void onClick(View arg0) {
		Log.d("Box2dIntegration", "onClick");
		new Thread(new Simulation()).run();
	}
}