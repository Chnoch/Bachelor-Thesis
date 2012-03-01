package ch.chnoch.thesis.renderer;

// TODO: Auto-generated Javadoc
/**
 * The Class GLException.
 */
public class GLException extends Exception {
	
	/** The m error. */
	String mError;
	
	/**
	 * Instantiates a new gL exception.
	 */
	public GLException() {
		super();
		mError = "unknown";
	}
	
	/**
	 * Instantiates a new gL exception.
	 * 
	 * @param error
	 *            the error
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
