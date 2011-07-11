// Basic vertex shader for 2D texturing.
uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
attribute vec2 aTextureCoord;

varying vec2 vTextureCoord;

void main()
{	
	vTextureCoord = aTextureCoord;
	
	gl_Position = uMVPMatrix * aPosition;
}
