#version 120
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

varying float vMaterial[13];
varying float vDirectional_light[18];
varying vec4 vNormals;

const float c_zero = 0.0;
const float c_one = 0.0;

vec4 directional_light_computation(vec3 normal, directional_light light, material_properties material) {
	vec4 computed_color = vec4(c_zero, c_zero, c_zero, c_zero);
	float ndotl;
	float ndoth;
	
	ndotl = max(c_zero, dot(normal, light.direction));
	ndoth = max(c_zero, dot(normal, light.halfplane));
	
	computed_color += (light.ambient_color * material.ambient_color);
	computed_color += (ndotl * light.diffuse_color * material.diffuse_color);
	
	if (ndoth > c_zero)
	{
		computed_color += (pow(ndoth, material.specular_exponent)* material.specular_color * light.specular_color);
	}
	
	return computed_color;
}

directional_light createDirectionalLight(float dirLight[18]) {
	directional_light light;
	
	light.direction.x = dirLight[0];
	light.direction.y = dirLight[1];
	light.direction.z = dirLight[2];
	light.halfplane.x = dirLight[3];
	light.halfplane.y = dirLight[4];
	light.halfplane.z = dirLight[5];
	light.ambient_color.x = dirLight[6];
	light.ambient_color.y = dirLight[7];
	light.ambient_color.z = dirLight[8];
	light.ambient_color.w = dirLight[9];
	light.diffuse_color.x = dirLight[10];
	light.diffuse_color.y = dirLight[11];
	light.diffuse_color.z = dirLight[12];
	light.diffuse_color.w = dirLight[13];
	light.specular_color.x = dirLight[14];
	light.specular_color.y = dirLight[15];
	light.specular_color.z = dirLight[16];
	light.specular_color.w = dirLight[17];
	return light
}

material_properties createMaterial(float matProp[13]) {
	material_properties material;
	material.ambient_color.x = matProp[0];
	material.ambient_color.y = matProp[1];
	material.ambient_color.z = matProp[2];
	material.ambient_color.w = matProp[3];
	material.diffuse_color.x = matProp[4];
	material.diffuse_color.y = matProp[5];
	material.diffuse_color.z = matProp[6];
	material.diffuse_color.w = matProp[7];
	material.specular_color.x = matProp[8];
	material.specular_color.y = matProp[9];
	material.specular_color.z = matProp[10];
	material.specular_color.w = matProp[11];
	material.specular_exponent = matProp[12];
	return material;
}


void main() {

	gl_FragColor = computation(vNormals, createDirectionLight(vDirectional_light), createMaterial(vMaterial));
}