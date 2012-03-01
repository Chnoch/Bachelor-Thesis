package ch.chnoch.thesis.renderer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import android.app.Application;
import android.util.Log;
import ch.chnoch.thesis.renderer.Shape;
import ch.chnoch.thesis.renderer.VertexBuffers;

// TODO: Auto-generated Javadoc
/**
 * The Class Util.
 */
public class Util {

	/** The one. */
	public static int one = 1;

	/** The one normal. */
	public static int oneNormal = 1;

	/**
	 * Creates a primitive type float array from a List of reference Float type
	 * values.
	 * 
	 * @param list
	 *            The List with the Float values
	 * @return a float array
	 */
	public static float[] floatListToArray(List<Float> list) {
		float[] floatArray = new float[list.size()];
		for (int i = 0; i < list.size(); i++) {
			floatArray[i] = list.get(i);
		}

		return floatArray;
	}

	/**
	 * Creates a primitive type float array from a List of reference Float type
	 * values.
	 * 
	 * @param list
	 *            The List with the Float values
	 * @return a float array
	 */
	public static int[] intListToArray(List<Integer> list) {
		int[] floatArray = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			floatArray[i] = list.get(i);
		}

		return floatArray;
	}

	/**
	 * Creates a new Identity Matrix.
	 * 
	 * @return an identity Matrix.
	 */
	public static Matrix4f getIdentityMatrix() {
		return new Matrix4f(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}
	
	/**
	 * Read raw text.
	 * 
	 * @param app
	 *            the app
	 * @param id
	 *            the id
	 * @return the string
	 */
	public static String readRawText(Application app, int id) {
		InputStream raw = app.getResources().openRawResource(id);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = raw.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = raw.read();
			}
			raw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}

	/**
	 * Load cube.
	 * 
	 * @param scale
	 *            the scale
	 * @return the shape
	 */
	public static Shape loadCube(float scale) {
		return loadCuboid(scale, scale, scale);
	}
	
	/**
	 * Load cuboid.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param depth
	 *            the depth
	 * @return the shape
	 */
	public static Shape loadCuboid(float width, float height, float depth) {
		VertexBuffers vertexBuffer = new VertexBuffers();
		vertexBuffer.setColorBuffer(colors);
		vertexBuffer.setVertexBuffer(generateCuboidVertices(width, height, depth));
		vertexBuffer.setNormalBuffer(newNormals);
		vertexBuffer.setIndexBuffer(indices);
		vertexBuffer.setTexCoordsBuffer(generateCuboidTextureCoordinates());
		
		return new Shape(vertexBuffer);
	}

	/**
	 * Load sphere.
	 * 
	 * @param latitudeBands
	 *            the latitude bands
	 * @param longitudeBands
	 *            the longitude bands
	 * @param radius
	 *            the radius
	 * @return the shape
	 */
	public static Shape loadSphere(int latitudeBands, int longitudeBands,
			float radius) {
		List<Float> vertexNormalsList = new ArrayList<Float>();
		List<Float> texCoordsList = new ArrayList<Float>();
		List<Float> verticesList = new ArrayList<Float>();

		for (int latNumber = 0; latNumber < latitudeBands+1; latNumber++) {
			double theta = latNumber * Math.PI / latitudeBands- Math.PI/2;
			Log.d("Util", "Theta: " + theta);
			double sinTheta = Math.sin(theta);
			double cosTheta = Math.cos(theta);

			for (int longNumber = 0; longNumber < longitudeBands+1; longNumber++) {
				double phi = longNumber * 2 * Math.PI / longitudeBands;
				double sinPhi = Math.sin(phi);
				double cosPhi = Math.cos(phi);

				double x = cosPhi * cosTheta;
				double y = sinTheta;
				double z = sinPhi * cosTheta;
				// double x = cosTheta * sinPhi;
				// double y = sinTheta * sinPhi;
				// double z = cosPhi;
//				double u = 1 - (longNumber / longitudeBands);
//				double v = latNumber / latitudeBands;
				// double u = sinPhi * cosTheta;
				// double v = sinPhi * cosPhi;
				double u = Math.asin(x) / Math.PI + 0.5;
				double v = Math.asin(y) / Math.PI + 0.5;

				vertexNormalsList.add((float) x);
				vertexNormalsList.add((float) y);
				vertexNormalsList.add((float) z);
				texCoordsList.add((float) u);
				texCoordsList.add((float) v);
				verticesList.add((float) (radius * x));
				verticesList.add((float) (radius * y));
				verticesList.add((float) (radius * z));
			}
		}

		List<Integer> indicesList = new ArrayList<Integer>();
		for (int latNumber = 0; latNumber < latitudeBands; latNumber++) {
			for (int longNumber = 0; longNumber < longitudeBands; longNumber++) {
				int first = ((latNumber * (longitudeBands + 1)) + longNumber);
				int second = (first + longitudeBands + 1);
				indicesList.add(first);
				indicesList.add(second);
				indicesList.add((first + 1));

				indicesList.add(second);
				indicesList.add(second + 1);
				indicesList.add(first + 1);
			}
		}

		float[] verticesArray = floatListToArray(verticesList);
		float[] texCoordsArray = floatListToArray(texCoordsList);
		float[] vertexNormalsArray = floatListToArray(vertexNormalsList);
		int[] indicesArray = intListToArray(indicesList);

		VertexBuffers buffers = new VertexBuffers();
		buffers.setVertexBuffer(verticesArray);
		buffers.setTexCoordsBuffer(texCoordsArray);
		buffers.setNormalBuffer(vertexNormalsArray);
		buffers.setIndexBuffer(indicesArray);
		Shape shape = new Shape(buffers);

		return shape;
	}

	
	/**
	 * Generate cuboid vertices.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @return the float[]
	 */
	private static float[] generateCuboidVertices(float x, float y, float z) {
		
		float vertices[] = { 
				x, -y, -z, 
				x, -y, -z,
				x, -y, -z,
			
			x, -y, z, 
			x, -y, z, 
			x, -y, z,
			
			-x, -y, z, 
			-x, -y, z, 
			-x, -y, z,
			
			-x, -y, -z, 
			-x, -y, -z, 
			-x, -y, -z,
			
			x, y, -z, 
			x, y, -z, 
			x, y, -z,
			
			x, y, z, 
			x, y, z, 
			x, y, z,
			
			-x, y, z, 
			-x, y, z, 
			-x, y, z,
			
			-x, y, -z, 
			-x, y, -z, 
			-x, y, -z };
		
		return vertices;
	}

	/**
	 * Generate cuboid texture coordinates.
	 * 
	 * @return the float[]
	 */
	private static float[] generateCuboidTextureCoordinates() {
		float texCoords[] = {
 1, 1, 1, 1, 0, 1,
 0, 1, 1, 1, 1, 0, 
 1, 1, 1, 1, 0, 1, 
 0, 1, 1, 1, 0, 1,
 1, 0, 0, 0, 1, 0,
 0, 0, 1, 1, 1, 0, 
 1,	0, 0, 0, 0, 1, 
 0, 0, 1, 0, 0, 0 };
		return texCoords;
	}

	/** The colors. */
	static float colors[] = { 0, 0, 0, one, one, 0, 0, one, one, one, 0, one,
			0, one, 0, one, 0, 0, one, one, one, 0, one, one, one, one, one,
			one, 0, one, one, one, };

	/** The indices. */
	static int indices[] = { 0, 12, 15, 0, 15, 3, 4, 17, 19, 4, 19, 8, 6, 18,
			21, 6, 21, 9, 10, 22, 13, 10, 13, 2, 14, 23, 20, 14, 20, 16, 11, 1,
			5, 11, 5, 7 };

	
	/** The new normals. */
	static float newNormals[] = {
		one, 0, 0,
		0, -one, 0,
		0, 0, -one,
		
		one, 0, 0,
		0, 0, one,
		0, -one, 0,
		
		-one, 0, 0,
		0, -one, 0,
		0, 0, one,
		
		-one, 0, 0,
		0, 0, -one,
		0, -one, 0,
		
		one, 0, 0,
		0, 0, -one,
		0, one, 0,
		
		one, 0, 0,
		0, one, 0,
		0, 0, one,
		
		-one, 0, 0,
		0, 0, one,
		0, one, 0,
		
		-one, 0, 0,
		0, 0, -one,
		0, one, 0
	};

	/**
	 * Transform.
	 * 
	 * @param m
	 *            the m
	 * @param point
	 *            the point
	 */
	public static void transform(Matrix4f m, Vector3f point) {
		float x, y, z, w;
		x = m.m00 * point.x + m.m01 * point.y + m.m02 * point.z + m.m03;
		y = m.m10 * point.x + m.m11 * point.y + m.m12 * point.z + m.m13;
		z = m.m20 * point.x + m.m21 * point.y + m.m22 * point.z + m.m23;
		w = m.m30 * point.x + m.m31 * point.y + m.m32 * point.z + m.m33;
		point.x = x / w;
		point.y = y / w;
		point.z = z / w;

	}
}
