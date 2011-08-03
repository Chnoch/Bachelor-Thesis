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


varying float vSpecular_exponent;

varying vec3 vDirection;
varying vec3 vHalfplane;

varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vMaterial_specular;
varying vec4 vLight_specular;

varying vec3 vNormals;

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

	vec3 n, halfV;
	float NdotL, NdotHV;
	
	vec4 color = vAmbient;
	n = normalize(vNormals);
	NdotL = max(dot(n,vDirection),0.0);
	if (NdotL > 0.0) {
		color += vDiffuse * NdotL;
		halfV = normalize(vHalfplane);
		NdotHV = max(dot(n,halfV),0.0);
		color+= vMaterial_specular*vLight_specular* pow(NdotHV, vSpecular_exponent);
	}

	gl_FragColor = color;
}