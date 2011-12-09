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

varying float vSpecular_exponent;

varying vec3 vViewPosition;
varying vec3 vLightPosition;

varying vec3 vNormal;

uniform sampler2D sTexture;
varying vec2 vTextureCoord;

varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vSpecular;

void main(void) {

	vec3 L = normalize(vLightPosition - vViewPosition);
	vec3 R = normalize(-reflect(L, vNormal));
	vec3 V = normalize(-vViewPosition);
	
	vec4 diffuse = vDiffuse * max(dot(vNormal, L), 0.0);
	vec4 specular = vSpecular * pow(max(dot(R,V), 0.0), vSpecular_exponent);
	
	diffuse = clamp(diffuse, 0.0, 1.0);
	specular = clamp(specular, 0.0, 1.0);
	
	gl_FragColor = vAmbient + diffuse + specular + texture2D(sTexture, vTextureCoord);
}