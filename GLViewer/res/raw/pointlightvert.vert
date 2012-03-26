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



varying vec3 vDirection;
varying vec3 vEyeVector;
varying vec4 vPosition;
varying vec4 vNormals;

varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vSpecular;
varying float vSpecular_exponent;

attribute vec4 aPosition;
attribute vec4 aNormals;
uniform mat4 uMVPMatrix;  // mvp = ModelViewProjection
uniform mat4 uMVMatrix; // mv = ModelView
uniform mat4 uNormalMatrix;

void main()
{	
	vDiffuse = material.diffuse * light.diffuse;
	vAmbient = material.ambient * light.ambient;
	vSpecular = material.specular * light.specular;
	vSpecular_exponent = material.specular_exponent;

	vNormals = uNormalMatrix * aNormals;

	vec3 vVertex = vec3(uMVMatrix * aPosition);

	vDirection = vec3(light.position.xyz - vVertex);
	vEyeVector = -vVertex;

	gl_Position = uMVPMatrix * aPosition;		
}