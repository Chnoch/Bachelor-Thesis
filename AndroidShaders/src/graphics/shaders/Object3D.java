/**
 * Class represents a 3D object. 
 * Consists of a mesh of triangles, any textures, lighting properties, etc. 
 */

package graphics.shaders;

import android.content.Context;
import android.opengl.GLES20;

public class Object3D {
	/*************************
	 * PROPERTIES
	 ************************/
	// Context
	Context context;

	// Mesh
	Mesh mesh; 						// The mesh of triangles
	int meshID;						// Mesh file (.OFF or .OBJ) from resources

	// texture
	private boolean hasTexture;
	private int[] texFiles;
	private int[] _texIDs;

	/***************************
	 * CONSTRUCTOR(S)
	 **************************/
	public Object3D(int meshID, boolean hasTexture, Context context) {
		this(new int[0], meshID, hasTexture, context);
	}

	public Object3D(int[] texFile, int meshID, boolean hasTexture, Context context) {
		this.texFiles = texFile;
		this.meshID = meshID;
		this.hasTexture = hasTexture;

		// the mesh
		mesh = new Mesh(meshID, context);

		// texture
		_texIDs = new int[texFiles.length];
	} 

	/**************************
	 * OTHER METHODS
	 *************************/
	
	// ...


	/***************************
	 * GET/SET
	 *************************/
	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public int getMeshID() {
		return meshID;
	}

	public void setMeshID(int meshID) {
		this.meshID = meshID;
	}

	public boolean hasTexture() {
		return hasTexture;
	}

	public void setHasTexture(boolean hasTexture) {
		this.hasTexture = hasTexture;
	}

	public int[] getTexFile() {
		return texFiles;
	}

	public void setTexFile(int[] texFile) {
		this.texFiles = texFile;
	}

	public int[] get_texID() {
		return _texIDs;
	}

	public void set_texID(int[] _texid) {
		_texIDs = _texid;
	}

}
