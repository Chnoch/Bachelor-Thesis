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

varying vec4 computed_color;

const float c_zero = 0.0;
const float c_one = 0.0;

vec4 directional_light_computation(vec3 normal) {
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


void main() {
	gl_Position = uMVPMatrix * aPosition;
	computed_color = directional_light_computation(aNormals.xyz);
}
