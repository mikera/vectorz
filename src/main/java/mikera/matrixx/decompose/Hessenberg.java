package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.hessenberg.HessenbergSimilarDecomposition;

/**
 * Public API class for Hessenberg decomposition
 */
public class Hessenberg {

	private Hessenberg() {
	}

	/**
	 * <p>
	 * Finds the decomposition of a matrix in the form of:<br>
	 * <br>
	 * A = QHQ<sup>T</sup><br>
	 * <br>
	 * where A is an m by m matrix, Q is an orthogonal matrix, and H is an upper
	 * Hessenberg matrix.
	 * </p>
	 *
	 * <p>
	 * A matrix is upper Hessenberg if a<sup>ij</sup> = 0 for all i > j+1.
	 * </p>
	 */
	public static IHessenbergResult decompose(AMatrix A) {
		return HessenbergSimilarDecomposition.decompose(A);
	}

}
