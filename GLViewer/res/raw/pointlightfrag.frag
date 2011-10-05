precision mediump float;

varying float vSpecular_exponent;

varying vec3 vDirection;
varying vec3 vEyeVector;
varying vec3 vPosition;

varying vec4 vDiffuse;
varying vec4 vAmbient;
varying vec4 vSpecular;

varying vec3 vNormals;


void main(void) {
	float diff = max(0.0, dot(normalize(vNormals), normalize(vDirection)));
	vec4 color = diff * vDiffuse;
	color += vAmbient;
	vec3 vReflection = normalize(reflect(-normalize(vDirection),normalize(vNormals)));
	float spec = max(0.0, dot(normalize(vNormals), vReflection));

	if(diff != 0.0) {
		vec4 specu = pow(spec, vSpecular_exponent)*vSpecular;
		color += specu;
	}
	
	gl_FragColor = color;
}