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
varying vec3 vEyeVector;
varying vec3 vPosition;

varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vMaterial_specular;
varying vec4 vLight_specular;

varying vec4 vNormals;
varying vec3 vVaryingNormal;
varying vec3 vVaryingLightDir;


void main(void) {
	float diff = max(0.0, dot(normalize(vVaryingNormal), normalize(vVaryingLightDir)));
	vec4 color = diff * vDiffuse;
	color += vAmbient;
	vec3 vReflection = normalize(reflect(-normalize(vVaryingLightDir),normalize(vVaryingNormal)));
	float spec = max(0.0, dot(normalize(vVaryingNormal), vReflection));

	if(diff != 0.0) {
		vec4 specu = pow(spec, vSpecular_exponent)*vMaterial_specular*vLight_specular;
		color += specu;
	}
	
	gl_FragColor = color;
}