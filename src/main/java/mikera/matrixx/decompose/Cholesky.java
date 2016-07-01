package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;

/**
 * Class implementing Cholesky decomposition
 * 
 *    A = L.L*
 *    
 * Where: A  is a symmetric (actually Hermitian), positive-definite matrix 
 * and:   L  is a lower triangular matrix
 *        L* is the conjugate transpose of L (which is equal to its transpose, since A is real in Vectorz)
 * 
 * See: http://en.wikipedia.org/wiki/Cholesky_decomposition
 * 
 * @author Mike
 *
 */
public class Cholesky {
	// TODO: refactor to use best available Cholesky decomposition algorithm for different matrix types and sizes

	private Cholesky(){}
	
	/**
	 * Decompose a Matrix according the the Cholesky decomposition A = L.L*
	 * 
	 * @param a Any symmetric, positive definite matrix
	 * @return The decomposition result, or null if not possible
	 */
	public static final ICholeskyResult decompose(AMatrix a) {
		return mikera.matrixx.decompose.impl.chol.Cholesky.decompose(a);
	}
}
