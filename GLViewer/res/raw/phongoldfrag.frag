// Frag shader Phong Shading - Per-pixel lighting

precision mediump float;

// texture variables


varying float vTex;
varying vec2 vTextureCoord;

varying vec3 EyespaceNormal;
varying vec3 vNormals;

varying vec3 vDirection;
varying vec3 vHalfplane;

varying vec4 vMatDiffuse;
varying vec4 vMatAmbient;
varying vec4 vMatSpecular;
varying float vMatShininess;

varying vec4 vLightSpecular;
varying vec4 vLightAmbient;
varying vec4 vLightDiffuse;
varying vec3 vLightDir, vEyeVec;
uniform sampler2D sTexture; // color texture

void main() {
	float NdotL, NdotHV;
	vec3 N = normalize(EyespaceNormal);
    vec3 E = normalize(vEyeVec); 
    
    vec3 L = normalize(vLightDir);
    
    // Reflect the vector. Use this or reflect(incidentV, N);
    vec3 reflectV = reflect(-L, N);
    
    // Get lighting terms
    vec4 ambientTerm;
    if (vTex >= 1.0) {
    	ambientTerm = texture2D(sTexture, vTextureCoord);
    }
    else
    	ambientTerm = vMatAmbient * vLightAmbient;
    	
    //vec4 diffuseTerm = vMatDiffuse * vLightDiffuse * max(dot(N, L), 0.0);
    //vec4 specularTerm = vMatSpecular * vLightSpecular * pow(max(dot(reflectV, E), 0.0), vMatShininess);
	vec4 diffuseTerm, specularTerm;
	NdotL = max(dot(N,L),0.0);
	if (NdotL > 0.0) {
		diffuseTerm = vLightDiffuse *vMatDiffuse* NdotL;
		NdotHV = max(dot(N,E),0.0);
		specularTerm = vMatSpecular*vLightSpecular* pow(NdotHV, vMatShininess);
	}
    
    gl_FragColor =  ambientTerm + diffuseTerm + specularTerm;
    //gl_FragColor = vec4(1.0, .5, 1.0, 1.0);//texture2D(sTexture, vTextureCoord);
	
}