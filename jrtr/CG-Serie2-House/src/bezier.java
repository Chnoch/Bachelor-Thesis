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
public class bezier {
    static RenderPanel renderPanel;
    static RenderContext renderContext;
    static SimpleSceneManager sceneManager;
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

//            Shader s = r.makeShader();
//            try {
//                s.load("..\\shaders\\phong.vert", "..\\shaders\\phong.frag");
//            } catch (Exception e) {
//                System.out.print("Problem with shader:\n");
//                System.out.print(e.getMessage());
//            }
//            s.use();

            // Register a timer task
            // Timer timer = new Timer();
            // angle = 0.001f;
            // timer.scheduleAtFixedRate(new AnimationTask(), 0, 100);
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
            // Matrix4f t = new Matrix4f(1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1);
            /*Matrix4f t = root.getTransformationMatrix();
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

    public static Shape makeCylinder(int resolution, float x, float y, float z,
            float scale) {
        float cylinder[], c[];
        int indices[];
        double angle = (Math.PI * 2) / resolution;
        cylinder = new float[2 * 3 * resolution];
        // top
        int a = -1;
        for (int i = 0; i < resolution; i++) {
            cylinder[++a] = scale * (float) Math.cos(i * angle) + x;
            cylinder[++a] = scale * (float) Math.sin(i * angle) + y;
            cylinder[++a] = scale + z;
        }

        // bottom
        for (int i = 0; i < resolution; i++) {
            cylinder[++a] = scale * (float) Math.cos(i * angle) + x;
            cylinder[++a] = scale * (float) Math.sin(i * angle) + y;
            cylinder[++a] = -1 * scale + z;
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

    private static Shape makeBezier(int segments, Vector3f[] controlPoints,
            int resolution, int angleRes, Vector3f translation) {
        // assertion
        if (controlPoints.length != (segments - 1) * 3 + 4) {
            System.out.println("not enough control points");
            return null;
        }

        // approximation of curve
        float element[] = new float[3 * segments * resolution * (angleRes - 1)
                + 3 * angleRes];

        // normals
        float[] normals = new float[element.length];

        float t = 0;
        int a = -1;
        element[++a] = controlPoints[0].x;
        element[++a] = controlPoints[0].y;
        element[++a] = controlPoints[0].z;
        for (int j = 0; j < segments; j++) {
            for (int i = 0; i < (resolution - 1); i++) {
                t += 1f / (resolution - 1);
                // interpolate
                Vector3f q1 = new Vector3f(controlPoints[0 + 3 * j]);
                q1.interpolate(controlPoints[1 + 3 * j], t);
                Vector3f q2 = new Vector3f(controlPoints[1 + 3 * j]);
                q2.interpolate(controlPoints[2 + 3 * j], t);
                Vector3f q3 = new Vector3f(controlPoints[2 + 3 * j]);
                q3.interpolate(controlPoints[3 + 3 * j], t);

                Vector3f r1 = new Vector3f(q1);
                r1.interpolate(q2, t);
                Vector3f r2 = new Vector3f(q2);
                r2.interpolate(q3, t);

                Vector3f normal = new Vector3f(r2);
                normal.sub(r1);

                Vector3f x = new Vector3f(r1);
                x.interpolate(r2, t);

                element[++a] = x.getX();
                normals[a] = normal.getZ();
                element[++a] = x.getY();
                normals[a] = 0;
                element[++a] = x.getZ();
                normals[a] = -normal.getX();

            }
            t = 0;
        }

        // rotation

        double myAngle = (2 * Math.PI) / angleRes;
        int res = 0;
        for (int i = 1; i < angleRes; i++) {
            for (int j = 0; j < (resolution - 1) * segments + 1; j++) {
                float x = element[res];
                float normalX = normals[res];
                float y = element[res + 1];
                float normalY = normals[res + 1];
                float z = element[j * 3 + 2];
                float normalZ = normals[3 * j + 2];

                float radius = (float) Math.sqrt(x * x + y * y);
                element[++a] = (float) Math.cos(i * myAngle) * radius;
                normals[a] = (float) Math.cos(i * myAngle) * normalX;
                element[++a] = (float) Math.sin(i * myAngle) * radius;
                normals[a] = (float) Math.sin(i * myAngle) * normalY;
                element[++a] = z;
                normals[a] = normalZ;
                res += 3;
                res %= 3 * ((resolution - 1) * segments + 1);
            }
        }

        int row = (resolution - 1) * segments + 1;

        // create indices
        int[] indices = new int[3 * (2 * (row - 1) * angleRes + 2 * (angleRes - 2))];
        a = -1;

        // bottom
        for (int i = 1; i < angleRes - 1; i++) {
            indices[++a] = 0;
            indices[++a] = i * row;
            indices[++a] = (i + 1) * row;
        }

        // top
        int init = row - 1;
        for (int i = 1; i < angleRes - 1; i++) {
            indices[++a] = init;
            indices[++a] = init + i * row;
            indices[++a] = init + (i + 1) * row;
        }

        // sides
        for (int i = 0; i < row - 1; i++) {
            for (int j = 0; j < angleRes; j++) {
                indices[++a] = i + j * row;
                indices[++a] = i + j * row + 1;
                indices[++a] = ((i + 1) + (j + 1) * row) % (angleRes * row);

                indices[++a] = i + j * row;
                indices[++a] = ((i + 1) + (j + 1) * row) % (angleRes * row);
                indices[++a] = ((i + 1) + (j + 1) * row) % (angleRes * row) - 1;
            }
        }

        // colors
        float[] c = new float[element.length];
        a = -1;
        for (int i = 0; i < c.length / 6; i++) {
            c[++a] = 1f;
            c[++a] = 1f;
            c[++a] = 1f;

            c[++a] = 0f;
            c[++a] = 0f;
            c[++a] = 0f;
        }
        
        // move shape
        a = -1;
        for (int i=0; i<element.length/3;i++) {
            element[++a]+=translation.getX();
            element[++a]+=translation.getY();
            element[++a]+=translation.getZ();
        }

        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = new VertexData(element.length / 3);
        vertexData.addElement(element, VertexData.Semantic.POSITION, 3);
         vertexData.addElement(c, VertexData.Semantic.COLOR, 3);
        vertexData.addElement(normals, VertexData.Semantic.NORMAL, 3);

        vertexData.addIndices(indices);

        // Make a shape and add the object
        return new Shape(vertexData);

    }

    /**
     * The main function opens a 3D rendering window, constructs a simple 3D
     * scene, and starts a timer task to generate an animation.
     */
    public static void main(String[] args) {

        // Make a scene manager and add the object
        sceneManager = new SimpleSceneManager();

        Light light = new Light();
        light.type = Light.Type.POINT;
        light.position = new Vector3f(0, 20, 60);
        light.specular = new Vector3f(1, 0, 0);
        light.diffuse = new Vector3f(0.7f, 0.7f, 0.7f);
        light.ambient = new Vector3f(0, 0, 0);

        Light light2 = new Light();
        light.type = Light.Type.POINT;
        light.position = new Vector3f(0, -40, 10);
        light.specular = new Vector3f(0, 0, 1);
        light.diffuse = new Vector3f(0.5f, 0.5f, 0.5f);
        light.ambient = new Vector3f(0.3f, 0.3f, 0.3f);

        Vector3f[] points = new Vector3f[7];
        points[0] = new Vector3f(0, 0, 0);
        points[1] = new Vector3f(3, 0, 1);
        points[2] = new Vector3f(3, 0, 3);
        points[3] = new Vector3f(4, 0, 7);
        points[4] = new Vector3f(7, 0, 8);
        points[5] = new Vector3f(7, 0, 10);
        points[6] = new Vector3f(0, 0, 11);

        Material material = new Material();
        material.ambient = new Vector3f(0.3f, 0.3f, 0.3f);
        material.diffuse = new Vector3f(0.5f, 0.5f, 0.5f);
        material.specular = new Vector3f(1,1,1);
        material.shininess = 8;
        
        Vector3f translation = new Vector3f(0,0,0);

        Shape shape = makeBezier(2, points, 100, 100, translation);
        shape.setMaterial(material);

        Shape shape2 = xPlane();
        shape2.setMaterial(material);
        // sceneManager.addShape(shape2);
        sceneManager.addShape(shape);
        sceneManager.addLight(light);
        sceneManager.addLight(light2);

        Camera camera = sceneManager.getCamera();
        camera.setCenterOfProjection(new Vector3f(0, -30, 20));

        // Make a render panel. The init function of the renderPanel
        // (see above) will be called back for initialization.
        renderPanel = new SimpleRenderPanel();

        // Make the main window of this application and add the renderer to it
        JFrame jframe = new JFrame("CG");
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

    private static Shape xPlane() {
        float[] element = new float[12];

        element[0] = -50;
        element[1] = -50;
        element[2] = 0;

        element[3] = 50;
        element[4] = -50;
        element[5] = 0;

        element[6] = 50;
        element[7] = 50;
        element[8] = 0;

        element[9] = -50;
        element[10] = 50;
        element[11] = 0;

        int[] indices = new int[6];
        indices[0] = 0;
        indices[0] = 3;
        indices[0] = 2;
        indices[0] = 0;
        indices[0] = 2;
        indices[0] = 1;

        float[] c = new float[element.length];
        for (int i = 0; i < c.length; i++) {
            c[i] = 0.5f;
        }

        // Construct a data structure that stores the vertices, their
        // attributes, and the triangle mesh connectivity
        VertexData vertexData = new VertexData(4);
        vertexData.addElement(element, VertexData.Semantic.POSITION, 3);
        vertexData.addElement(c, VertexData.Semantic.COLOR, 3);

        vertexData.addIndices(indices);

        // Make a shape and add the object
        return new Shape(vertexData);
    }
}
