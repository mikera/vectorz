package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ISVDResult;


import mikera.matrixx.decompose.impl.svd.SvdImplicitQr;

/**
 * Public API class for SVD decomposition
 * 
 * @author Mike
 *
 */
public class SVD {
	
	/**
	 * Computes the Singular Value Decomposition of a matrix, which is the decomposition
	 * of the given matrix A as:
	 * A = U*S*V,
	 * where U and V are orthogonal and S is a diagonal matrix containing singular values
	 * along its diagonal.
	 * The non zero singular values are the square roots of the non-zero eigenvalues of 
	 * M<sup>T</sup>M and MM<sup>T</sup>, where M is the input matrix.
	 * U is m by m, S is  m by n, V is n by n.
	 * 
	 * @param A
	 * @return
	 */
	public static ISVDResult decompose(AMatrix A) {
		return SvdImplicitQr.decompose(A, false);
	}
	
	/**
	 * Computes the Singular Value Decomposition of a matrix, which is the decomposition
	 * of the given matrix A as:
	 * A = U*S*V,
	 * where U and V are orthogonal and S is a diagonal matrix containing singular values
	 * along its diagonal.
	 * The non zero singular values are the square roots of the non-zero eigenvalues of 
	 * M<sup>T</sup>M and MM<sup>T</sup>, where M is the input matrix.
	 * If compact is false, U  is m by m, W is  m by n, V is n by n,
	 * if compact is true, U is m by s, S is s by s, and V is n by s, where s is the number
	 * of Singular Values
	 * 
	 * @param A
	 * @param compact
	 * @return
	 */
	public static ISVDResult decompose(AMatrix A, boolean compact) {
		return SvdImplicitQr.decompose(A, compact);
	}
	
	/**
	 * Computes the Singular Value Decomposition of a matrix, which is the decomposition
	 * of the given matrix A as:
	 * A = U*S*V,
	 * where U and V are orthogonal and S is a diagonal matrix containing singular values 
	 * along its diagonal.
	 * The non zero singular values are the square roots of the non-zero eigenvalues of 
	 * M<sup>T</sup>M and MM<sup>T</sup>, where M is the input matrix.
	 * U is m by s, S is s by s, and V is n by s, where s is the number of Singular Values
	 * 
	 * @param A
	 * @param compact
	 * @return
	 */
	public static ISVDResult decomposeCompact(AMatrix A) {
		return SvdImplicitQr.decompose(A, true);
	}

}
