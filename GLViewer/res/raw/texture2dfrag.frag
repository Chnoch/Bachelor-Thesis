// Basic fragment shader for 2D texture mapping.

// Define a variable to access the texture. 
uniform sampler2D tex;

void main()
{		
	// The built-in function texture2D performs the texture
	// look-up. We read the texture coordinates from the
	// pre-defined varying variable gl_TexCoord[0], which
	// we set in the vertex shader.
	gl_FragColor = texture2D(tex, gl_TexCoord[0].st);		
}
