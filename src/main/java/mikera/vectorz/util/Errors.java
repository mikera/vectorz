package mikera.vectorz.util;

import mikera.arrayz.INDArray;

public class Errors {
	public static Throwable immutable(INDArray a) {
		return new UnsupportedOperationException(ErrorMessages.immutable(a));
	}
}
