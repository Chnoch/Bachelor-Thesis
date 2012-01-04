precision mediump float;

struct point_light {
	vec3 position;
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
uniform point_light light;

attribute vec4 aPosition;
attribute vec3 aNormals;

varying float vSpecular_exponent;

varying vec3 vViewPosition;
varying vec3 vLightPosition;

varying vec3 vNormal;

varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vSpecular;

attribute vec2 aTextureCoord;

varying vec2 vTextureCoord;

uniform mat4 uMVPMatrix;  // mvp = ModelViewProjection
uniform mat4 uMVMatrix; // mv = ModelView
uniform mat4 uNormalMatrix;

void main() {
	vDiffuse = material.diffuse * light.diffuse;
	vAmbient = material.ambient * light.ambient;
	vSpecular = material.specular * light.specular;
	vSpecular_exponent = material.specular_exponent;
	vLightPosition = light.position;
	
	//vTextureCoord = aTextureCoord;
	
	vNormal = (uNormalMatrix * vec4(aNormals, 0.0)).xyz;
	vNormal = normalize(vNormal);
	vViewPosition = (uMVMatrix * aPosition).xyz;
	
	gl_Position = uMVPMatrix * aPosition;
}