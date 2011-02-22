// Fragment shader for per-pixel diffuse shading. The
// teapot is a good object to test this shader, since
// the .obj file includes surface normals.

// The shader computes the dot product between the unit
// surface normal and light direction, which were 
// passed as varying inputs from the vertex shader. The
// result is multiplied with the vertex color, which is 
// accessed through a pre-defined varying variable.

varying vec3 normal, lightDir;

void main()
{		
	gl_FragColor = gl_LightSource[0].diffuse * max(dot(normal, normalize(lightDir)),0.0) * gl_FrontMaterial.diffuse;		
}
