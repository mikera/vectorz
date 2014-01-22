package example;

import mikera.arrayz.INDArray;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.vectorz.util.Errors;

public class ErrorThrow {

	public static void main(String[] args) throws Throwable {
		INDArray a=IdentityMatrix.create(10);
		throw Errors.immutable(a);
	}
}
