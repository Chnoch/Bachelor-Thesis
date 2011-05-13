package ch.chnoch.thesis.box2dintegration;

import javax.vecmath.Vector3f;

import org.jbox2d.collision.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import ch.chnoch.thesis.renderer.*;
import ch.chnoch.thesis.renderer.Shape;
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
	private World world;
	private Body body;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSceneManager = new GraphSceneManager();
		mSceneManager.getCamera()
				.setCenterOfProjection(new Vector3f(0, 15, 20));
		mSceneManager.getFrustum().setFarPlane(300);
		mShape = Util.loadCube(1);

		mRoot = new TransformGroup();
		mSceneManager.setRoot(mRoot);

		mRenderer = new GLRenderer10(getApplication());
		mRenderer.setSceneManager(mSceneManager);
		mViewer = new GLViewer(this, mRenderer);

		mNode = new ShapeNode(mShape);
		mNode.move(new Vector3f(0, 10, 0));
		mRoot.addChild(mNode);

		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);

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

		mViewer.setOnClickListener(this);

	}

	private class Simulation implements Runnable {
		public void run() {
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
		}
	}

	@Override
	public void onClick(View arg0) {
		Log.d("Box2dIntegration", "onClick");
		new Thread(new Simulation()).run();
	}
}