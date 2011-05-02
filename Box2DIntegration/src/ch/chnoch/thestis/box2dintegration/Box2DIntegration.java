package ch.chnoch.thestis.box2dintegration;

import javax.vecmath.Matrix4f;
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

public class Box2DIntegration extends Activity {

	private GraphSceneManager mSceneManager;
	private Shape mShape;
	private ch.chnoch.thesis.renderer.interfaces.Node mNode, mRoot;
	private RenderContext mRenderer;
	private GLSurfaceView mViewer;
	private World world;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSceneManager = new GraphSceneManager();
		mSceneManager.getCamera().setCenterOfProjection(
				new Vector3f(100, 20, 40));
		mSceneManager.getFrustum().setFarPlane(300);
		mShape = Util.loadCube(1);

		mRoot = new TransformGroup();
		mSceneManager.setRoot(mRoot);

		mRenderer = new GLRenderer10(getApplication());
		mRenderer.setSceneManager(mSceneManager);
		mViewer = new GLViewer(this, mRenderer);

		mNode = new ShapeNode(mShape);
		mRoot.addChild(mNode);

		setContentView(mViewer);
		mViewer.requestFocus();
		mViewer.setFocusableInTouchMode(true);
		
		Vec2 gravity = new Vec2(0.0f, 10.0f); 
		boolean doSleep = true;
		AABB completeBoundingBox = new AABB(new Vec2(-100f,-100f), new Vec2(100f,100f));
		world = new World(completeBoundingBox, gravity, doSleep);
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(0.0f, -10.0f);
		Body groundBody = world.createBody(groundBodyDef);
		PolygonDef groundBox = new PolygonDef();
		groundBox.setAsBox(50f, 10f);
		groundBody.createShape(groundBox);
	}
}