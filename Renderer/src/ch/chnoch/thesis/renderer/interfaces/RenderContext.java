package ch.chnoch.thesis.renderer.interfaces;

import javax.vecmath.Matrix4f;

import android.opengl.GLSurfaceView;
import ch.chnoch.thesis.renderer.GLViewer;

// TODO: Auto-generated Javadoc
/**
 * Declares the functionality of a render context, or a "renderer". It is
 * currently implemented by {@link GLRenderContext} and {@link SWRenderContext}. 
 */
public interface RenderContext extends GLSurfaceView.Renderer {

	/**
	 * Set a scene manager that will be rendered.
	 * 
	 * @param sceneManager
	 *            the new scene manager
	 */
	public void setSceneManager(SceneManagerInterface sceneManager);
	
	/**
	 * Make a shader.
	 * 
	 * @param shader
	 *            the shader
	 * @param vertexShader
	 *            the vertex shader
	 * @param fragmentShader
	 *            the fragment shader
	 * @return the shader
	 * @throws Exception
	 *             the exception
	 */
	public void createShader(Shader shader, String vertexShader, String fragmentShader) throws Exception;
	
	/**
	 * Make a texture.
	 * 
	 * @return the texture
	 */
	public Texture createTexture();
	
	/**
	 * Gets the viewport matrix.
	 * 
	 * @return the viewport matrix
	 */
	public Matrix4f getViewportMatrix();
	
	/**
	 * Gets the scene manager.
	 * 
	 * @return the scene manager
	 */
	public SceneManagerInterface getSceneManager();
	
	/**
	 * Sets the viewer.
	 * 
	 * @param viewer
	 *            the new viewer
	 */
	public void setViewer(GLViewer viewer);

}
