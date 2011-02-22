varying vec3 normal, lightDir1, lightDir2, eyeVec;

void main()
{	
	normal = gl_NormalMatrix * gl_Normal;

	vec3 vVertex = vec3(gl_ModelViewMatrix * gl_Vertex);

	lightDir1 = vec3(gl_LightSource[0].position.xyz - vVertex);
	lightDir2 = vec3(gl_LightSource[1].position.xyz - vVertex);
	eyeVec	= -vVertex;
	
	gl_TexCoord[0] = gl_MultiTexCoord0;

	gl_Position = ftransform();		
}