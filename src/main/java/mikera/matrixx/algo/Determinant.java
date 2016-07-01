package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrix33;
import mikera.matrixx.decompose.ILUPResult;
import mikera.matrixx.decompose.impl.lu.SimpleLUP;
import mikera.vectorz.util.IntArrays;

/**
 * Public API function class for determinant calculation.
 * 
 * Normally you should use m.determinant() to calculate the determinant of a matrix. These functions
 * are provided to allow access to alternative algorithms.
 * 
 * @author Mike
 *
 */
public class Determinant {
	private Determinant(){}
	
	/**
	 * Calculate the determinant of an AMatrix.
	 * 
	 * @param m
	 * @return
	 */
	public static double calculate(AMatrix m) {
		int rc = m.checkSquare();

		if (rc<=4) {
			// much faster for small matrices
			if (rc<=3) return calculateSmallDeterminant(m,rc);

			// benchmarks show naive method is slightly better for
			// size 4 matrices?
			return naiveDeterminant(m.toMatrix(),rc);
		}
		
		// general determinant uses LUP decomposition
		return calculateLUPDeterminant(m);		
	}
	
	/**
	 * Determinant implemented using the LUP decomposition
	 * 
	 * @param m
	 * @return
	 */
	static double calculateLUPDeterminant(AMatrix m) {
		ILUPResult lup=SimpleLUP.decompose(m);
		double det=lup.getL().diagonalProduct()*lup.getU().diagonalProduct()*lup.getP().determinant();
		return det;
	}
	
	/**
	 * Calculates the determinant of a small square matrix via direct computation
	 * @return
	 */
	static double calculateSmallDeterminant(AMatrix m, int rc) {
		if (rc==1) return m.unsafeGet(0,0);
		if (rc==2) return m.unsafeGet(0,0)*m.unsafeGet(1,1)-m.unsafeGet(1,0)*m.unsafeGet(0,1);
		if (rc==3) {
			return new Matrix33(m).determinant();
		}
		throw new UnsupportedOperationException("Small determinant calculation on size "+rc+" not possible");
	}
	
	/**
	 * Calculate determinant using naive method (brute force)
	 */
	static double naiveDeterminant(Matrix m) {
		return naiveDeterminant(m,m.rowCount());
	}
	
	static double naiveDeterminant(AMatrix m, int rc) {
		int[] inds = new int[rc];
		for (int i = 0; i < rc; i++) {
			inds[i] = i;
		}
		return calcDeterminant(m.toMatrix(),inds, 0);
	}


	private static double calcDeterminant(Matrix m, int[] inds, int offset) {
		int rc = m.rowCount();
		if (offset == (rc - 1))
			return m.unsafeGet(offset, inds[offset]);

		// multiple of first submatrix
		double v0=m.unsafeGet(offset, inds[offset]);
		double det = (v0==0)? 0: v0* calcDeterminant(m,inds, offset + 1);
		
		for (int i = 1; i < (rc - offset); i++) {
			IntArrays.swap(inds, offset, offset + i);
			double v=m.unsafeGet(offset, inds[offset]);
			if (v!=0) {
				det -= v * calcDeterminant(m,inds, offset + 1);
			}
			IntArrays.swap(inds, offset, offset + i);
		}
		return det;
	}

}
