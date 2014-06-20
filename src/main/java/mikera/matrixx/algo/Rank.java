package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.svd.SVDResult;
import mikera.matrixx.decompose.impl.svd.SvdImplicitQr;
import mikera.vectorz.AVector;

public class Rank {
	
	private static double threshold = 2.220446e-15;
	
	/**
     * Returns the rank of a matrix.
     * 
     * @param A The input matrix
     * @return The matrix's rank
     */
	public static int compute(AMatrix A) {
		return compute(A, threshold);
	}
	
	/**
     * Returns the rank of a matrix.
     * 
     * @param A The input matrix
     * @param threshold Tolerance used to determine if a singular value is singular.
     * @return The matrix's rank
     */
	public static int compute(AMatrix A, double threshold) {
		SVDResult ans = SvdImplicitQr.decompose(A, true);
		int rank = 0;
		AVector singularValues = ans.getSingularValues();
        for( double s : singularValues ) {
            if( s > threshold)
                rank++;
        }
        return rank;
	}

}
