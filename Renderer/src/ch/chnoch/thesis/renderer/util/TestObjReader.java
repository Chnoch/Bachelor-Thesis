package ch.chnoch.thesis.renderer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import ch.chnoch.thesis.renderer.VertexBuffers;

/*
 * this is the OBJReader class. vertex, normal, texture, mtl and polygon
 * information are stored here as vectors. ahmet.kizilay@gmail.com
 */

public class TestObjReader {
	String fileName;
	List<Vector3f> vertices;
	List<Vector3f> normals;
	List<Polygon> polygons;
	List<Vector3f> textures;
	boolean wireFrame = false;
	boolean mtlincluded = false;
	int mtlnum = -1; // mtlnum is initially -1 and it keeps the current
						// index of the mtl.

	public TestObjReader() {
		vertices = new ArrayList<Vector3f>();
		normals = new ArrayList<Vector3f>();
		textures = new ArrayList<Vector3f>();
		polygons = new ArrayList<Polygon>();
		// mtlincluded = mtlexists;
	}

	public void readFile(InputStream inputStream) {
		// if(mtlincluded) readMTL();

		try {
			BufferedReader input;
			input = new BufferedReader(new InputStreamReader(inputStream));
			try {
				String newLine = null;
				while ((newLine = input.readLine()) != null) {
					int ind = newLine.indexOf("vn ");
					if (ind != -1) {
						readNormal(newLine);
						continue;
					}

					ind = newLine.indexOf("v ");
					if (ind != -1) {
						readVertex(newLine);
						continue;
					}

					ind = newLine.indexOf("f ");
					if (ind != -1) {
						readPolygon(newLine);
						continue;
					}

					ind = newLine.indexOf("vt ");
					if (ind != -1) {
						readTexture(newLine);
						continue;
					}

					// ind = newLine.indexOf("usemtl ");
					// if(ind != -1){
					// readMTLInfo(newLine);
					// continue;
					// }
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public VertexBuffers createVertexBuffers() {
		VertexBuffers vertexBuffers = new VertexBuffers();
		
		int[] indicesFinal = new int[polygons.size()*3];
		float[] verticesFinal = new float[indicesFinal.length * 3];
		float[] normalsFinal = new float[verticesFinal.length]; 
		
		int vertex = 0;
		int normal = 0;
		for (Polygon polygon : polygons) {
			int[] indices = polygon.getVertexIndices();
			for (int i = 0; i< indices.length; i++){
				verticesFinal[vertex] = vertices.get(indices[i]-1).x;
				verticesFinal[vertex+1] = vertices.get(indices[i]-1).y;
				verticesFinal[vertex+2] = vertices.get(indices[i]-1).z;
				vertex+=3;
			}
			
			indices = polygon.getNormalIndices();
			for (int i = 0; i< indices.length;i++){
				normalsFinal[normal] = normals.get(indices[i]-1).x;
				normalsFinal[normal+1] = normals.get(indices[i]-1).y;
				normalsFinal[normal+2] = normals.get(indices[i]-1).z;
				normal+=3;
			}
		}
		
		for (int i = 0 ;i < indicesFinal.length; i++) {
			indicesFinal[i] = i;
		}
		
//		float[] verticesFinal = new float[vertices.size() * 3];
//		int j = 0;
//		for (int i = 0; i < verticesFinal.length; i += 3) {
//			verticesFinal[i] = vertices.get(j).x;
//			verticesFinal[i + 1] = vertices.get(j).y;
//			verticesFinal[i + 2] = vertices.get(j).z;
//			j++;
//		}

		vertexBuffers.setVertexBuffer(verticesFinal);

//		float[] normalsFinal = new float[polygons.size() * 3];
//		for (Polygon polygon : polygons) {
//			int[] normalIndices = polygon.getNormalIndices();
//			j = 0;
//			for (int i = 0; i < normalIndices.length - 1; i++) {
//				normalsFinal[j] = normals.get(normalIndices[i]-1).x;
//				normalsFinal[j + 1] = normals.get(normalIndices[i]-1).y;
//				normalsFinal[j + 2] = normals.get(normalIndices[i]-1).z;
//				j += 3;
//			}
//		}
		vertexBuffers.setNormalBuffer(normalsFinal);

//		int[] finalIndices = new int[polygons.size() * 3];
//		j = 0;
//		for (Polygon polygon : polygons) {
//			int[] indices = polygon.vertexIndices;
//			finalIndices[j] = indices[0];
//			finalIndices[j + 1] = indices[1];
//			finalIndices[j + 2] = indices[2];
//			j += 3;
//		}

		vertexBuffers.setIndexBuffer(indicesFinal);
		return vertexBuffers;
	}

	private void readVertex(String newLine) {
		String pieces[] = newLine.split(" ");
		Vector3f vert = new Vector3f(Float.parseFloat(pieces[1]),
				Float.parseFloat(pieces[2]), Float.parseFloat(pieces[3]));
		vertices.add(vert);
	}

	private void readNormal(String newLine) {
		String pieces[] = newLine.split(" ");
		Vector3f norms = new Vector3f(Float.parseFloat(pieces[1]),
				Float.parseFloat(pieces[2]), Float.parseFloat(pieces[3]));
		normals.add(norms);
	}

	private void readTexture(String newLine) {
		String pieces[] = newLine.split(" ");
		Vector3f tex = new Vector3f(Float.parseFloat(pieces[1]),
				Float.parseFloat(pieces[2]), 0);
		textures.add(tex);
	}

	private void readPolygon(String newLine) {
		String pieces[] = newLine.split(" ");
		Polygon poly = new Polygon(pieces.length - 1, mtlnum);
		for (int i = 1; i < pieces.length; i++) {
			String smallerPieces[] = pieces[i].split("//");
			poly.setVertexIndex(i - 1, Integer.parseInt(smallerPieces[0]));
			// if texture is not specified
			if (smallerPieces.length == 2) {
				poly.setNormalIndex(i - 1, Integer.parseInt(smallerPieces[1]));
			}
			// if texture is specified
			if (smallerPieces.length == 3) {
				poly.setNormalIndex(i - 1, Integer.parseInt(smallerPieces[2]));
				poly.setTextureIndex(i - 1, Integer.parseInt(smallerPieces[1]));
			}
		}
		polygons.add(poly);
	}

	public List<Polygon> getPolygons() {
		return polygons;
	}

	public List<Vector3f> getVertices() {
		return vertices;
	}

	public List<Vector3f> getNormals() {
		return normals;
	}

	public List<Vector3f> getTextures() {
		return textures;
	}

	/*
	 * this is a simple class to store info about the polygons
	 * ahmet.kizilay@gmail.com
	 */
	private class Polygon {
		int vertexIndices[];
		int normalIndices[];
		int textureIndices[];
		int sides;
		int mtlnum;

		Polygon(int si, int mtl) {
			vertexIndices = new int[si];
			normalIndices = new int[si];
			textureIndices = new int[si];
			sides = si;
			mtlnum = mtl;
		}

		void setVertexIndex(int i, int num) {
			vertexIndices[i] = num;
		}

		void setNormalIndex(int i, int num) {
			normalIndices[i] = num;
		}

		void setTextureIndex(int i, int num) {
			textureIndices[i] = num;
		}

		public int[] getVertexIndices() {
			return vertexIndices;
		}

		public int[] getNormalIndices() {
			return normalIndices;
		}

		public int[] getTextureIndices() {
			return textureIndices;
		}

	}
}
