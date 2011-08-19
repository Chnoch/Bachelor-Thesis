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

// texture variables
attribute float aHasTexture;
varying float vTex;
attribute vec2 aTextureCoord;
varying vec2 vTextureCoord;

uniform material_properties material;
uniform directional_light light;
uniform mat4 uMVPMatrix;
uniform mat4 uNormalMatrix;

attribute vec4 aPosition;
attribute vec4 aNormals;
attribute vec3 aEyeVector;

varying float vSpecular_exponent;

varying vec3 vDirection;
varying vec3 vEyeVector;
varying vec4 vPosition;


varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vMaterial_specular;
varying vec4 vLight_specular;

varying vec4 vNormals;

void main() {
	// pass on texture variables
	vTex = aHasTexture;
	vTextureCoord = aTextureCoord;
	
	vDirection = normalize(light.direction);
	vEyeVector = normalize(aEyeVector);
	vDiffuse = material.diffuse * light.diffuse;
	vAmbient = material.ambient * light.ambient;
	vMaterial_specular = material.specular;
	vLight_specular = light.specular;
	vSpecular_exponent = material.specular_exponent;
	
	vNormals = normalize(uNormalMatrix * aNormals);
	vPosition = uMVPMatrix * aPosition;
	gl_Position = uMVPMatrix * aPosition;
}