package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.lu.AltLU;

public class LUP {
	
	// TODO: fuller documentation for LUP properties

	private LUP(){}
	
	/**
	 * This performs LU decomposition on the given matrix and returns the result as a ILUPResult object
	 * 
	 * @param A The matrix that is to be decomposed. Not modified.
	 * @return An ILUPResult object that contains L, U and P matrices
	 */
	public static ILUPResult decompose(AMatrix A) {
		return AltLU.decompose(A);
	}

}
