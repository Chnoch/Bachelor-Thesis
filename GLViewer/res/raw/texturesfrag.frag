varying vec3 normal, lightDir1, lightDir2, eyeVec;
uniform sampler2D tex;

void main (void)
{

	vec4 texture = texture2D(tex, gl_TexCoord[0].st);
	float matSpec = texture.r + texture.g + texture.b;
	
	vec4 final_color = 
	(gl_FrontLightModelProduct.sceneColor * texture) + 
	(gl_LightSource[0].ambient * texture) + (gl_LightSource[1].ambient * texture);
							
	vec3 N = normalize(normal);
	vec3 L1 = normalize(lightDir1);
	vec3 L2 = normalize(lightDir2);
	
	float NdotL = dot(N,L1);
	
	if(NdotL > 0.0)
	{
		final_color += gl_LightSource[0].diffuse * texture * 
					   NdotL;	
		
		vec3 E = normalize(eyeVec);
		vec3 R = reflect(-L1, N);
		float specular = pow( max(dot(R, E), 0.0), 
		                 gl_FrontMaterial.shininess );
		
		final_color += gl_LightSource[0].specular * 
		               matSpec * 
					   specular;	
	}
	
	NdotL = dot(N,L2);
	
	if(NdotL > 0.0)
	{
		final_color += gl_LightSource[1].diffuse * texture * 
					   NdotL;	
		
		vec3 E = normalize(eyeVec);
		vec3 R = reflect(-L2, N);
		float specular = pow( max(dot(R, E), 0.0), 
		                 gl_FrontMaterial.shininess );
		
		final_color += gl_LightSource[1].specular * matSpec * 
					   specular;	
	}

	gl_FragColor = final_color;			
}