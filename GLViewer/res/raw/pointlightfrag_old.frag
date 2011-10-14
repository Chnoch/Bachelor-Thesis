precision mediump float;

varying vec4 vNormals;
varying vec3 vDirection;
varying vec3 vEyeVector;

varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vSpecular;
varying float vSpecular_exponent;

void main (void)
{
	vec4 final_color = vAmbient;
							
	vec3 N = normalize(vNormals).xyz;
	vec3 L = normalize(vDirection);
	
	float lambertTerm = dot(N,L);
	
	if(lambertTerm > 0.0)
	{
		final_color += vDiffuse * 
					   lambertTerm;	
		
		vec3 E = normalize(vEyeVector);
		vec3 R = reflect(-L, N);
		float specular = pow( max(dot(R, E), 0.0), 
		                 vSpecular_exponent );
		final_color += vSpecular * specular;	
	}

	gl_FragColor = final_color;			
}