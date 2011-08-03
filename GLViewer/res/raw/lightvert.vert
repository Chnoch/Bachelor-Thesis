precision mediump float;

struct directional_light {
	vec3 direction;
	vec3 halfplane;
	vec4 ambient_color;
	vec4 diffuse_color;
	vec4 specular_color;
};

struct material_properties {
	vec4 ambient_color;
	vec4 diffuse_color;
	vec4 specular_color;
	float specular_exponent;
};

uniform material_properties material;
uniform directional_light light;
uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec4 aNormals;

varying float vSpecular_exponent;

varying vec3 vDirection;
varying vec3 vHalfplane;

varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vMaterial_specular;
varying vec4 vLight_specular;

varying vec3 vNormals;

void main() {
	vDirection = normalize(light.direction);
	vHalfplane = normalize(light.halfplane);
	vDiffuse = material.diffuse_color * light.diffuse_color;
	vAmbient = material.ambient_color * light.ambient_color;
	vMaterial_specular = material.specular_color;
	vLight_specular = light.specular_color;
	vSpecular_exponent = material.specular_exponent;
	
	vNormals = normalize(uMVPMatrix * aNormals).xyz;
	
	gl_Position = uMVPMatrix * aPosition;
}