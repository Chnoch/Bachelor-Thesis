precision mediump float;

struct directional_light {
	vec3 direction;
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

uniform material_properties material;
uniform directional_light light;

varying float vSpecular_exponent;

varying vec3 vDirection;
varying vec3 vEyeVector;
varying vec4 vPosition;

varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vMaterial_specular;
varying vec4 vLight_specular;

attribute vec4 aPosition;
attribute vec4 aNormals;
uniform mat4 uMVPMatrix;  // mvp = ModelViewProjection
uniform mat4 uMVMatrix; // mv = ModelView
uniform mat4 uNormalMatrix;

varying vec3 vVaryingNormal;
varying vec3 vVaryingLightDir;

void main() {
	vDiffuse = material.diffuse * light.diffuse;
	vAmbient = material.ambient * light.ambient;
	vMaterial_specular = material.specular;
	vLight_specular = light.specular;
	vSpecular_exponent = material.specular_exponent;
	
	vVaryingNormal = normalize(uNormalMatrix * aNormals).xyz;
	//vVaryingNormal = normalize(aNormals).xyz;
	//vVaryingNormal = normalize(vec3(0,1,1));
	
	vec4 vPosition4 = uMVMatrix * aPosition;
	vec3 vPosition = vPosition4.xyz; // vPosition4.w;
	
	vVaryingLightDir = light.direction;
	
	gl_Position = uMVPMatrix * aPosition;
}