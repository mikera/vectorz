package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.bidiagonal.BidiagonalRow;

/**
 * API class for performing bidiagonal decompositions
 * 
 * @author Mike
 *
 */
public class Bidiagonal {

	private Bidiagonal() {
	}

	/**
	 * Computes the Bidiagonal decomposition of a matrix A
	 * 
	 * A = U.B.Vt
	 * 
	 * Where: B is a bidiagonal matrix
	 * 
	 * @param A
	 * @return
	 */
	public static IBidiagonalResult decompose(AMatrix A) {
		return BidiagonalRow.decompose(A);
	}

	public static IBidiagonalResult decompose(AMatrix A, boolean compact) {
		return BidiagonalRow.decompose(A, compact);
	}

	public static IBidiagonalResult decomposeCompact(AMatrix A) {
		return decompose(A, true);
	}
}
