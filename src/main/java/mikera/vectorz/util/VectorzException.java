package mikera.vectorz.util;

@SuppressWarnings("serial")
public class VectorzException extends RuntimeException {
	public VectorzException(String message) {
		super(message);
	}

	public VectorzException(String message, Throwable e) {
		super(message,e);
	}
}
