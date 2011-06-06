// Vertex shader for per-pixel diffuse shading.The
// teapot is a good object to test this shader, since
// the .obj file includes surface normals.

// The shader computes the unit light direction and unit
// surface normal, which are passed to the fragment
// shader as varying variables.

varying vec3 normal, lightDir;
uniform mat4 u_MVPMatrix;
attribute vec4 a_position;
attribute vec4 a_normal;

void main()
{	
	// Note that gl_LightSource, gl_NormalMatrix, and gl_Normal
	// are pre-defined variables that access the current OpenGL
	// state.
	lightDir = normalize(vec3(gl_LightSource[0].position));
	normal = normalize(gl_NormalMatrix * gl_Normal);
	
	// ftransform() is a built-in function that applies all
	// transformations (i.e., modelview and 
	// projection) to a vertex.
	gl_Position = u_MVPMatrix * a_position;
}
