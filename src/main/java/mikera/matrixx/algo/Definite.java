package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.Cholesky;
import mikera.matrixx.decompose.Eigen;
import mikera.matrixx.decompose.IEigenResult;
import mikera.vectorz.Vector2;

public class Definite {

	private Definite(){}

	/**
	 * Tests whether a symmetric matrix is positive definite
	 * 
	 * Results are undefined if the matrix is not symmetric
	 * 
	 * @param a
	 * @return
	 */
	public static boolean isPositiveDefinite(AMatrix a) {
		return Cholesky.decompose(a)!=null;
	}
	
	/**
	 * Tests whether a symmetric matrix is positive semi-definite
	 * 
	 * Results are undefined if the matrix is not symmetric
	 * 
	 * @param a
	 * @return
	 */
	public static boolean isPositiveSemiDefinite(AMatrix a) {
		IEigenResult e=Eigen.decomposeSymmetric(a);
		if (e==null) return false;
		
		Vector2[] eigenValues=e.getEigenvalues();
		for (Vector2 v:eigenValues) {
			if (v.x<0) return false;
		}
		return true;
	}
}
