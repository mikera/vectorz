package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ISVDResult;
import mikera.matrixx.decompose.SVD;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.vectorz.Vector;

public class PseudoInverse {
	/**
	 * Computes the Moore–Penrose pseudoinverse of a matrix
	 * 
	 * @param a
	 * @return
	 */
	public static AMatrix calculate(AMatrix a) {
		// compute a compact SVD decomposition. All singular values should be positive!
		ISVDResult svd = SVD.decomposeNonZero(a);
		
		Vector s=svd.getSingularValues().toVector();
		s.reciprocal(); // reciprocal of singular values
		
		AMatrix U=svd.getU();
		AMatrix V=svd.getV();
		
		return V.innerProduct(DiagonalMatrix.create(s).innerProduct(U.getTranspose()));
	}
}
