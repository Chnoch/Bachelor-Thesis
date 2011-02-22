// Basic vertex shader for 2D texturing.

void main()
{	
	// gl_MultiTexCoord is a pre-defined vertex attribute that
	// stores the texture coordinates of the vertex. gl_TexCoord[0]
	// is a pre-defined varying variable that is passed to the 
	// fragment shader.	
	gl_TexCoord[0] = gl_MultiTexCoord0;

	// ftransform() is a built-in function that applies all
	// transformations (i.e., modelview and 
	// projection) to a vertex.
	gl_Position = ftransform();
}
