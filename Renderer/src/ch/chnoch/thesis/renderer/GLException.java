package ch.chnoch.thesis.renderer;

public class GLException extends Exception {
	
	String mError;
	
	public GLException() {
		super();
		mError = "unknown";
	}
	
	public GLException(String error) {
		super();
		mError = error;
	}
	
	public String getError() {
		return mError;
	}
}
