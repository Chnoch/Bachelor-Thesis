precision mediump float;

struct directional_light {
	vec3 direction;
	vec3 halfplane;
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
};

struct material_properties {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	float specular_exponent;
};


varying float vSpecular_exponent;

varying vec3 vDirection;
varying vec3 vHalfplane;

varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vMaterial_specular;
varying vec4 vLight_specular;

varying vec3 vNormals;

//Textures
varying float vTex;
varying vec2 vTextureCoord;

uniform sampler2D sTexture; 

const float c_zero = 0.0;
const float c_one = 0.0;

void main() {

	vec3 n, halfV;
	float NdotL, NdotHV;
	
	vec3 direction = normalize(vDirection);
	vec4 color;
	if (vTex >= 1.0) {
		color += texture2D(sTexture, vTextureCoord);
	}
	color += vAmbient;
	n = normalize(vNormals);
	NdotL = max(dot(n,direction),0.0);
	if (NdotL > 0.0) {
		color += vDiffuse * NdotL;
		halfV = normalize(vHalfplane);
		NdotHV = max(dot(n,halfV),0.0);
		color+= vMaterial_specular*vLight_specular;
//		* pow(NdotHV, vSpecular_exponent);
	}

	gl_FragColor = color;
}