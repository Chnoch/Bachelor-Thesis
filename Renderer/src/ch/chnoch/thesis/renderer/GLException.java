package ch.chnoch.thesis.renderer;

/**
 * The Class represents an exception that occurred during any interaction with
 * OpenGL
 */
public class GLException extends Exception {
	
	private static final long serialVersionUID = 4146023685623820925L;
	private String mError;
	
	/**
	 * Instantiates a new gL exception.
	 */
	public GLException() {
		super();
		mError = "unknown";
	}
	
	/**
	 * Instantiates a new exception.
	 * 
	 * @param error
	 *            the error that occurred
	 */
	public GLException(String error) {
		super();
		mError = error;
	}
	
	/**
	 * Gets the error.
	 * 
	 * @return the error
	 */
	public String getError() {
		return mError;
	}
}
