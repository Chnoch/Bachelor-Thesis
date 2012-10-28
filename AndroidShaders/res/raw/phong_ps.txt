// Frag shader Phong Shading - Per-pixel lighting

precision mediump float;

// texture variables
uniform sampler2D texture1; // color texture

varying float tex;
varying vec2 tCoord;

varying vec3 vNormal;
varying vec3 EyespaceNormal;

// light
uniform vec4 lightPos;
uniform vec4 lightColor;

// material
uniform vec4 matAmbient;
uniform vec4 matDiffuse;
uniform vec4 matSpecular;
uniform float matShininess;

// eye pos
uniform vec3 eyePos;

// from vertex s
varying vec3 lightDir, eyeVec;

void main() {
	// Just to show them being used
	//vec4 a = lightPos;
    vec4 b = lightColor;
    vec4 c = matAmbient;
    vec4 d = matDiffuse;
    vec4 e = matSpecular;
    vec3 g = eyePos;
    float f = matShininess;
	
	vec3 N = normalize(EyespaceNormal);
    vec3 E = normalize(eyeVec); 
    
    vec3 L = normalize(lightDir);
    
    // Reflect the vector. Use this or reflect(incidentV, N);
    vec3 reflectV = reflect(-L, N);
    
    // Get lighting terms
    vec4 ambientTerm;
    if (tex >= 1.0) {
    	ambientTerm = texture2D(texture1, tCoord);
    }
    else
    	ambientTerm = matAmbient * lightColor;
    	
    vec4 diffuseTerm = matDiffuse * max(dot(N, L), 0.0);
    vec4 specularTerm = matSpecular * pow(max(dot(reflectV, E), 0.0), matShininess);
    
    gl_FragColor =  ambientTerm + diffuseTerm + specularTerm;
    //gl_FragColor = vec4(1.0, .5, 1.0, 1.0);//texture2D(sTexture, vTextureCoord);
	
}