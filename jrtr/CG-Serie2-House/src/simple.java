import jrtr.*;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.vecmath.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implements a simple application that opens a 3D rendering window and shows a
 * rotating cube.
 */
public class simple {
    static RenderPanel renderPanel;
    static RenderContext renderContext;
    static GraphSceneManager sceneManager;
    static float angle;


    /**
     * An extension of {@link GLRenderPanel} or {@link SWRenderPanel} to provide
     * a call-back function for initialization.
     */
    public final static class SimpleRenderPanel extends GLRenderPanel {
        /**
         * Initialization call-back. We initialize our renderer here.
         * @param r
         *            the render context that is associated with this render
         *            panel
         */
        public void init(RenderContext r) {
            renderContext = r;
            renderContext.setSceneManager(sceneManager);
            
            
            Shader s = r.makeShader();
            try {
                s.load("..\\shaders\\diffuse.vert", "..\\shaders\\diffuse.frag");
            } catch (Exception e) {
                System.out.print("Problem with shader:\n");
                System.out.print(e.getMessage());
            }
            s.use();

            // Register a timer task
            Timer timer = new Timer();
            angle = 0.001f;
            timer.scheduleAtFixedRate(new AnimationTask(), 0, 100);
        }
    }

    /*
    	/**
    	 * A timer task that generates an animation. This task triggers
    	 * the redrawing of the 3D scene every time it is executed.
    	 */
    public static class AnimationTask extends TimerTask {
        public void run() {
            // Update transformation
//            Matrix4f t = new Matrix4f(1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1);
            Matrix4f t = root.getTransformationMatrix();
            Matrix4f rotZ = new Matrix4f();
            rotZ.rotY(angle);
            t.mul(rotZ);
            root.setTransformationMatrix(t);
            
/*            t = leftArmGroup.getTransformationMatrix();
            t.mul(rotX);
            leftArmGroup.setTransformationMatrix(t);
            
            t = rightArmGroup.getTransformationMatrix();
            t.mul(rotX);
            rightArmGroup.setTransformationMatrix(t);
*/         
            
            /*Matrix4f t2 =  leftLowerArm.getTransformationMatrix();
            Matrix4f rotY2 = new Matrix4f();
            Matrix4f rotX2 = new Matrix4f();
            Matrix4f rotZ2 = new Matrix4f();
            rotX2.rotX(angle);
            rotY2.rotY(angle);
            rotZ2.rotZ(angle);
            t2.mul(rotX2);
            t2.mul(rotY2);
            leftLowerArm.setTransformationMatrix(t2);*/
             
            /*
            Matrix4f t3 =  rightArmGroup.getTransformationMatrix();
            Matrix4f rotY3 = new Matrix4f();
            Matrix4f rotX3 = new Matrix4f();
            Matrix4f rotZ3 = new Matrix4f();
            rotX3.rotX(angle);
            rotY3.rotY(angle);
            rotZ3.rotZ(angle);
//            t3.mul(rotX3);
            rotY3.mul(t3);
//            t3.mul(rotZ3);
            rotY3.mul(rotX3);
//            rotX3.mul(t3);
//            rotZ3.mul(t3);
            rightArmGroup.setTransformationMatrix(rotY3);*/

            // Trigger redrawing of the render window
            renderPanel.getCanvas().repaint();
        }
    }

    /**
     * A mouse listener for the main window of this application. This can be
     * used to process mouse events.
     */
    public static class SimpleMouseListener implements MouseListener {
        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }
    }

    public static Shape makeCylinder(int resolution, float x, float y, float z, float scale) {
        float cylinder[], c[];
        int indices[];
        double angle = (Math.PI * 2) / resolution;
        cylinder = new float[2 * 3 * resolution];
        // top
        int a = -1;
        for (int i = 0; i < resolution; i++) {
            cylinder[++a] = scale*(float) Math.cos(i * angle)+x;
            cylinder[++a] = scale*(float) Math.sin(i * angle)+y;
            cylinder[++a] = scale+z;
        }

        // bottom
        for (int i = 0; i < resolution; i++) {
            cylinder[++a] = scale*(float) Math.cos(i * angle)+x;
            cylinder[++a] = scale*(float) Math.sin(i * angle)+y;
            cylinder[++a] = -1*scale+z;
        }

        // colors
        c = new float[2 * 3 * resolution];
        a = -1;
        for (int i = 0; i < resolution; i++) {
            c[++a] = 1;
            c[++a] = 1;
            c[++a] = 1;

            c[++a] = 0;
            c[++a] = 0;
            c[++a] = 0;
        }

        // 2*3*(resolution-2) (top and bottom) + 6*resolution (sides)
        indices = new int[12 * resolution - 12];
        a = -1;

        // top
        for (int i = 0; i < resolution - 2; i++) {
            indices[++a] = 0;
            indices[++a] = (i + 1);
            indices[++a] = (i + 2);
        }

        // bottom
        for (int i = resolution; i < 2 * resolution - 2; i++) {
            indices[++a] = resolution;
            indices[++a] = (i + 2);
            indices[++a] = (i + 1);
        }

        // sides
        for (int i = 0; i < resolution - 1; i++) {
            indices[++a] = i;
            indices[++a] = resolution + i;
            indices[++a] = resolution + i + 1;

            indices[++a] = i;
            indices[++a] = resolution + i + 1;
            indices[++a] = (i + 1) % resolution;

        }
        // correction for last side
        indices[++a] = resolution - 1;
        indices[++a] = 2 * resolution - 1;
        indices[++a] = resolution;

        indices[++a] = resolution - 1;
        indices[++a] = resolution;
        indices[++a] = 0;

        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = new VertexData(cylinder.length / 3);
        vertexData.addElement(cylinder, VertexData.Semantic.POSITION, 3);
        vertexData.addElement(c, VertexData.Semantic.COLOR, 3);

        vertexData.addIndices(indices);

        // Make a shape and add the object
        return new Shape(vertexData);
    }

    public static Shape makeBall(int resolution) {
        float c[], ball[];
        int indices[];
        
        double phi = (Math.PI * 2) / resolution;
        double theta = (Math.PI) / 2*resolution;
        ball = new float[(2 * (resolution - 2) * 3 * resolution) + 3
                * resolution + 6];
        // top
        int a = -1;
        for (int i = 0; i < ball.length / 3; i++) {
            ball[++a] = (float) (Math.cos(i * phi) * Math.sin(i * theta));
            ball[++a] = (float) (Math.sin(i * phi) * Math.sin(i * theta));
            ball[++a] = (float) Math.cos(i * theta);
        }

        // colors
        c = new float[(2 * (resolution - 2) * 3 * resolution) + 3
                      * resolution + 6];
        a = -1;
        for (int i = 0; i < resolution; i++) {
            c[++a] = 1;
            c[++a] = 1;
            c[++a] = 1;

            c[++a] = 0;
            c[++a] = 0;
            c[++a] = 0;
        }

        // 2*3*(resolution-2) (top and bottom) + 6*resolution (sides)
        indices = new int[12 * resolution - 12];
        a = -1;

        // top
        for (int i = 0; i < resolution - 2; i++) {
            indices[++a] = 0;
            indices[++a] = (i + 1);
            indices[++a] = (i + 2);
        }

        // bottom
        for (int i = resolution; i < 2 * resolution - 2; i++) {
            indices[++a] = resolution;
            indices[++a] = (i + 2);
            indices[++a] = (i + 1);
        }

        // sides
        for (int i = 0; i < resolution - 1; i++) {
            indices[++a] = i;
            indices[++a] = resolution + i;
            indices[++a] = resolution + i + 1;

            indices[++a] = i;
            indices[++a] = resolution + i + 1;
            indices[++a] = (i + 1) % resolution;

        }
        // correction for last side
        indices[++a] = resolution - 1;
        indices[++a] = 2 * resolution - 1;
        indices[++a] = resolution;

        indices[++a] = resolution - 1;
        indices[++a] = resolution;
        indices[++a] = 0;
        
        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = new VertexData(ball.length / 3);
        vertexData.addElement(ball, VertexData.Semantic.POSITION, 3);
        vertexData.addElement(c, VertexData.Semantic.COLOR, 3);

        // The triangles (three vertex indices for each triangle)
        /*int indices[] = {0,2,3, 0,1,2,            // front face
                         4,6,7, 4,5,6,          // left face
                         8,10,11, 8,9,10,       // back face
                         12,14,15, 12,13,14,    // right face
                         16,18,19, 16,17,18,    // top face
                         20,22,23, 20,21,22};   // bottom face
        */
        vertexData.addIndices(indices);

        // Make a scene manager and add the object
        return new Shape(vertexData);
    }

    
    private static Node root, headGroup, head, leftArmGroup, leftLowerArmGroup, rightArmGroup, leftLegGroup, rightLegGroup,
    body, leftLowerArm, leftUpperArm, rightLowerArm,rightUpperArm, leftUpperLeg, leftLowerLeg, rightUpperLeg, rightLowerLeg;
    /**
     * The main function opens a 3D rendering window, constructs a simple 3D
     * scene, and starts a timer task to generate an animation.
     */
    public static void main(String[] args) {
        Matrix4f ident = new Matrix4f(1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1);

        Shape leftUpperArmShape= makeCylinder(10, 0.5f,0,0, 0.4f);
        Shape leftLowerArmShape = makeCylinder(10, 0,0,0, 0.3f);
        Shape rightUpperArmShape = makeCylinder(10, 3.5f,0,0, 0.4f);
        Shape rightLowerArmShape = makeCylinder(10, 4,0,0, 0.3f);
        Shape rightLowerLegShape = makeCylinder(10, 2.5f,-4,0, 0.4f);
        Shape rightUpperLegShape = makeCylinder(10, 2.5f,-2,0, 0.5f);
        Shape leftLowerLegShape = makeCylinder(10, 1.5f,-4,0, 0.4f);
        Shape leftUpperLegShape = makeCylinder(10, 1.5f,-2,0, 0.5f);
        Shape headShape = makeCylinder(10, 2,2,0, 0.5f);
        Shape bodyShape = makeCylinder(4, 2,0,0, 1f);

        // Make a scene manager and add the object
        sceneManager = new GraphSceneManager();
        root = new TransformGroup();
        root.setTransformationMatrix(ident);
        
        body = new ShapeNode();
        root.addChild(body);
        body.setShape(bodyShape);
        body.setTransformationMatrix(ident);
        
        headGroup = new TransformGroup();
        headGroup.setTransformationMatrix(ident);
        
        root.addChild(headGroup);
        
        leftArmGroup = new TransformGroup();
        leftArmGroup.setTransformationMatrix(ident);
        
        leftLowerArmGroup = new TransformGroup();
        leftLowerArmGroup.setTransformationMatrix(ident);
        
        leftArmGroup.addChild(leftLowerArmGroup);
        
        rightArmGroup = new TransformGroup();
        rightArmGroup.setTransformationMatrix(ident);
        root.addChild(leftArmGroup);
        root.addChild(rightArmGroup);
        
        leftLegGroup = new TransformGroup();
        leftLegGroup.setTransformationMatrix(ident);
        rightLegGroup = new TransformGroup();
        rightLegGroup.setTransformationMatrix(ident);
        root.addChild(leftLegGroup);
        root.addChild(rightLegGroup);
        
        
        head = new ShapeNode();
        headGroup.addChild(head);
        head.setShape(headShape);
        head.setTransformationMatrix(ident);
        
        leftUpperLeg = new ShapeNode();
        leftLegGroup.addChild(leftUpperLeg);
        leftUpperLeg.setShape(leftUpperLegShape);
        leftUpperLeg.setTransformationMatrix(ident);
        
        leftLowerLeg = new ShapeNode();
        leftLegGroup.addChild(leftLowerLeg);
        leftLowerLeg.setShape(leftLowerLegShape);
        leftLowerLeg.setTransformationMatrix(ident);
        
        rightUpperLeg = new ShapeNode();
        rightLegGroup.addChild(rightUpperLeg);
        rightUpperLeg.setShape(rightUpperLegShape);
        rightUpperLeg.setTransformationMatrix(ident);
        
        rightLowerLeg = new ShapeNode();
        rightLegGroup.addChild(rightLowerLeg);
        rightLowerLeg.setShape(rightLowerLegShape);
        rightLowerLeg.setTransformationMatrix(ident);
        
        
        leftUpperArm = new ShapeNode();
        leftArmGroup.addChild(leftUpperArm);
        leftUpperArm.setShape(leftUpperArmShape);
        leftUpperArm.setTransformationMatrix(ident);
        
        leftLowerArm = new ShapeNode();
        leftLowerArmGroup.addChild(leftLowerArm);
        leftLowerArm.setShape(leftLowerArmShape);
        leftLowerArm.setTransformationMatrix(ident);
        
        rightUpperArm = new ShapeNode();
        rightArmGroup.addChild(rightUpperArm);
        rightUpperArm.setShape(rightUpperArmShape);
        rightUpperArm.setTransformationMatrix(ident);

        rightLowerArm = new ShapeNode();
        rightArmGroup.addChild(rightLowerArm);
        rightLowerArm.setShape(rightLowerArmShape);
        rightLowerArm.setTransformationMatrix(ident);
        
        LightNode lightNode = new LightNode();
        lightNode.setTransformationMatrix(ident);
        Light light = new Light();
        light.type = Light.Type.POINT;
        light.position = new Vector3f(-2,0.5f,0);
        light.specular = new Vector3f(0,0,1);
        light.diffuse = new Vector3f(0.7f,0.7f,0.7f);
        light.ambient = new Vector3f(0,0,0);
        lightNode.setLight(light);
        leftLowerArmGroup.addChild(lightNode);
        
        VertexData vertexData = null;
        
        try {
            vertexData = ObjReader.read("teapot.obj", 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Material material = new Material();
        material.ambient = new Vector3f(0.2f,0.2f,0.2f);
        material.diffuse = new Vector3f(0.5f,0.5f,0.5f);
        material.specular = new Vector3f(1f,0.5f,0.5f);
        material.shininess = 8;


        Shape shape = new Shape(vertexData);
        shape.setMaterial(material);
        
        ShapeNode teapot = new ShapeNode();
        teapot.setShape(shape);
        teapot.setTransformationMatrix(ident);
        root.addChild(teapot);
        
        
        sceneManager.setRoot(root);
        // Make a render panel. The init function of the renderPanel
        // (see above) will be called back for initialization.
        renderPanel = new SimpleRenderPanel();

        // Make the main window of this application and add the renderer to it
        JFrame jframe = new JFrame("Cylinder");
        jframe.setSize(500, 500);
        jframe.setLocationRelativeTo(null); // center of screen
        jframe.getContentPane().add(renderPanel.getCanvas());// put the canvas
        // into a JFrame
        // window

        // Add a mouse listener
        jframe.addMouseListener(new SimpleMouseListener());

        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setVisible(true); // show window
    }
}