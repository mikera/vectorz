package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ISVDResult;
import mikera.matrixx.decompose.SVD;
import mikera.vectorz.AVector;

public class Rank {

	private Rank(){}
	
	private static double DEFAULT_THRESHOLD = 2.220446e-15;
	
	/**
	 * Returns the rank of a matrix.
	 * 
	 * @param A The input matrix
	 * @return The matrix's rank
	 */
	public static int compute(AMatrix A) {
		return compute(A, DEFAULT_THRESHOLD);
	}
	
	/**
	 * Returns the rank of a matrix.
	 * 
	 * @param A The input matrix
	 * @param threshold Tolerance used to determine if a singular value is singular.
	 * @return The matrix's rank
	 */
	public static int compute(AMatrix A, double threshold) {
		ISVDResult ans = SVD.decompose(A, true);
		int rank = 0;
		AVector singularValues = ans.getSingularValues();
		int n=singularValues.length();
		for(int i=0; i<n; i++) {
			if( singularValues.unsafeGet(i) >= threshold)
				rank++;
		}
		return rank;
	}
	
	/**
     * Directly computes rank of matrix whose SVD decomposition has already been computed.
     * 
     * @param result The result of an SVD decomposition
     * @return The matrix's rank
     */
	public static int compute(ISVDResult result) {
		return compute(result, DEFAULT_THRESHOLD);
	}
	
	/**
     * Directly computes rank of matrix whose SVD decomposition has already been computed.
     * 
     * @param result The result of an SVD decomposition
     * @param threshold Tolerance used to determine if a singular value is singular.
     * @return The matrix's rank
     */
	public static int compute(ISVDResult result, double threshold) {
		int rank = 0;
		AVector singularValues = result.getSingularValues();
        for( double s : singularValues ) {
            if( s > threshold)
                rank++;
        }
        return rank;
	}

}
