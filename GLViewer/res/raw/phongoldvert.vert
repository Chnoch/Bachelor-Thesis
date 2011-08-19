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

uniform material_properties material;
uniform directional_light light;

uniform mat4 uMVPMatrix;
uniform mat4 uNormalMatrix;

// position and normal of the vertices
attribute vec4 aPosition;
attribute vec4 aNormals; 

// texture variables
attribute float aHasTexture;
varying float vTex;
attribute vec2 aTextureCoord;
varying vec2 vTextureCoord;

// normals to pass on
varying vec3 EyespaceNormal;

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

void main() {
	// pass on texture variables
	vTex = aHasTexture;
	vTextureCoord = aTextureCoord;
	
	// normal
	EyespaceNormal = vec3(uNormalMatrix * aNormals);
	
	// the vertex position
	vec4 position = uMVPMatrix * aPosition; 
	vEyeVec = light.halfplane;
	
	// light dir
	//lightDir = lightPos.xyz - position.xyz;
	vLightDir = normalize(light.direction);
	vLightSpecular = light.specular;
	vLightAmbient = light.ambient;
	vLightDiffuse = light.diffuse;
	
	vMatDiffuse = material.diffuse;
	vMatAmbient = material.ambient;
	vMatSpecular = material.specular;
	vMatShininess = material.specular_exponent;
		
	gl_Position = uMVPMatrix * aPosition; 
}