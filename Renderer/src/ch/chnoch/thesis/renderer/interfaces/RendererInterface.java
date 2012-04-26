package ch.chnoch.thesis.renderer.interfaces;

import javax.vecmath.Matrix4f;

import android.opengl.GLSurfaceView;
import ch.chnoch.thesis.renderer.GLViewer;

/**
 * This interface declares the functionality of a render context, or a
 * "renderer". This is what's used to draw anything on the screen. This extends
 * the Renderer interface from the class {@link GLSurfaceView} from the Android
 * framework, so that the callback methods will work as advertised.
 */
public interface RendererInterface extends GLSurfaceView.Renderer {

	/**
	 * Set a scene manager that will be rendered.
	 * 
	 * @param sceneManager
	 *            the new scene manager
	 */
	public void setSceneManager(SceneManagerInterface sceneManager);

	/**
	 * Create a {@link Shader} from a given vertex and fragment shader source
	 * code
	 * 
	 * @param shader
	 *            The object that will represent the created shader
	 * @param vertexShader
	 *            The vertex shader source code
	 * @param fragmentShader
	 *            The fragment shader source code
	 * @return the shader object
	 * @throws Exception
	 * 
	 */
	public void createShader(Shader shader, String vertexShader, String fragmentShader) throws Exception;

	/**
	 * Create a {@link Texture} that can be used by OpenGL ES.
	 * 
	 * @return the texture that was created
	 * @throws Exception
	 */
	public Texture createTexture() throws Exception;
	
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
	 * Sets the viewer that represents the actual surface in the Android
	 * framework.
	 * 
	 * @param viewer
	 *            the new viewer
	 */
	public void setViewer(GLViewer viewer);

	/**
	 * Returns a value indicating whether the renderer is using OpenGL ES 2.0
	 * for rendering graphics or version 1.1
	 * 
	 * @return a flag indicating OpenGL ES 2.0 support
	 */
	public boolean supportsOpenGLES20();

}
