package ch.chnoch.thesis.renderer.interfaces;

import javax.vecmath.Matrix4f;
import android.opengl.GLSurfaceView;

/**
 * Declares the functionality of a render context, or a "renderer". It is
 * currently implemented by {@link GLRenderContext} and {@link SWRenderContext}. 
 */
public interface RenderContext extends GLSurfaceView.Renderer {

	/**
	 * Set a scene manager that will be rendered.
	 */
	void setSceneManager(SceneManagerInterface sceneManager);
	
	/**
	 * Make a shader.
	 * 
	 * @return the shader
	 */
	Shader makeShader();
	
	/**
	 * Make a texture.
	 * 
	 * @return the texture
	 */
	Texture makeTexture();
	
	public Matrix4f getViewportMatrix();
	
	public SceneManagerInterface getSceneManager();
	
	public Matrix4f createMatrices();

}
