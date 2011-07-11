package ch.chnoch.thesis.renderer.interfaces;

import javax.vecmath.Matrix4f;

import ch.chnoch.thesis.renderer.GLViewer;
import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Declares the functionality of a render context, or a "renderer". It is
 * currently implemented by {@link GLRenderContext} and {@link SWRenderContext}. 
 */
public interface RenderContext extends GLSurfaceView.Renderer {

	/**
	 * Set a scene manager that will be rendered.
	 */
	public void setSceneManager(SceneManagerInterface sceneManager);
	
	/**
	 * Make a shader.
	 * @param fragmentShader 
	 * @param vertexShader 
	 * 
	 * @return the shader
	 * @throws Exception 
	 */
	public void createShader(Shader shader, String vertexShader, String fragmentShader) throws Exception;
	
	/**
	 * Make a texture.
	 * 
	 * @return the texture
	 */
	public Texture makeTexture();
	
	public Matrix4f getViewportMatrix();
	
	public SceneManagerInterface getSceneManager();
	
	public void setViewer(GLViewer viewer);

}
