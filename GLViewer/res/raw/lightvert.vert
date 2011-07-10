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

varying float vMaterial[13];
varying float vDirectional_light[18];
varying vec4 vNormals;



float[] createDirectionalLightArray(directional_light light) {
	float dirLight[18];
	
	dirLight[0] = light.direction.x;
	dirLight[1] = light.direction.y;
	dirLight[2] = light.direction.z;
	dirLight[3] = light.halfplane.x;
	dirLight[4] = light.halfplane.y;
	dirLight[5] = light.halfplane.z;
	dirLight[6] = light.ambient_color.x;
	dirLight[7] = light.ambient_color.y;
	dirLight[8] = light.ambient_color.z;
	dirLight[9] = light.ambient_color.w;
	dirLight[10] = light.diffuse_color.x;
	dirLight[11] = light.diffuse_color.y;
	dirLight[12] = light.diffuse_color.z;
	dirLight[13] = light.diffuse_color.w;
	dirLight[14] = light.specular_color.x;
	dirLight[15] = light.specular_color.y;
	dirLight[16] = light.specular_color.z;
	dirLight[17] = light.specular_color.w;
	return dirLight;
}

float[] createMaterialArray(material_properties material) {
	float matProp[13];
	matProp[0] = material.ambient_color.x;
	matProp[1] = material.ambient_color.y;
	matProp[2] = material.ambient_color.z;
	matProp[3] = material.ambient_color.w;
	matProp[4] = material.diffuse_color.x;
	matProp[5] = material.diffuse_color.y;
	matProp[6] = material.diffuse_color.z;
	matProp[7] = material.diffuse_color.w;
	matProp[8] = material.specular_color.x;
	matProp[9] = material.specular_color.y;
	matProp[10] = material.specular_color.z;
	matProp[11] = material.specular_color.w;
	matProp[12] = material.specular_exponent;
	return matProp;
}


void main() {
	gl_Position = uMVPMatrix * aPosition;
	vMaterial = createMaterialArray(material);
	vDirectional_Light = createDirectionalLightArray(light);
	vNormals = aNormals;
}
