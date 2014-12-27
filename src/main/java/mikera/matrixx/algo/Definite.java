package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.Cholesky;
import mikera.matrixx.decompose.Eigen;
import mikera.matrixx.decompose.IEigenResult;

@SuppressWarnings("unused")
public class Definite {

	public static boolean isPositiveDefinite(AMatrix a) {
		return Cholesky.decompose(a)!=null;
	}
	
	public static boolean isPositiveSemiDefinite(AMatrix a) {
		IEigenResult e=Eigen.decompose(a);
		throw new UnsupportedOperationException("TODO: definiteness test");
		
	}
}
