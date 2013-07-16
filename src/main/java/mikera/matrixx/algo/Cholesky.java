package mikera.matrixx.algo;

import mikera.matrixx.Matrix;

/**
 * Class implementing a standard Cholesky decomposition
 * @author Mike
 *
 */
public class Cholesky {

	public static final Matrix decompose(Matrix m) {
		if (!m.isSquare()) throw new IllegalArgumentException("Matrix must be square for Cholesky decomposition");
		
		Matrix u=Matrix.create(m);
		
		
		throw new UnsupportedOperationException();
	}
}
