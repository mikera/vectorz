package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix33;
import mikera.matrixx.algo.decompose.lu.ILUP;
import mikera.matrixx.algo.decompose.lu.impl.SimpleLUP;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

/**
 * Static function class for determinant calculation.
 * 
 * Use Determinant.calculate(m) to compute the determinant of a matrix.
 * 
 * @author Mike
 *
 */
public class Determinant {

	public static double calculate(AMatrix m) {
		int rc = m.rowCount();
		int cc = m.columnCount();
		if (rc!=cc) {
			throw new UnsupportedOperationException(ErrorMessages.nonSquareMatrix(m));
		}

		if (rc==1) return m.unsafeGet(0,0);
		if (rc==2) return m.unsafeGet(0,0)*m.unsafeGet(1,1)-m.unsafeGet(1,0)*m.unsafeGet(0,1);
		if (rc==3) {
			return new Matrix33(m).determinant();
		}
		
		return smartDeterminant(m);		
	}
	
	static double smartDeterminant(AMatrix m) {
		ILUP lup=SimpleLUP.decompose(m);
		double det=lup.getL().diagonalProduct()*lup.getU().diagonalProduct()*lup.getP().determinant();
		return det;
	}
	
	@SuppressWarnings("unused")
	static double naiveDeterminant(AMatrix m) {
		int rc = m.rowCount();
		int[] inds = new int[rc];
		for (int i = 0; i < rc; i++) {
			inds[i] = i;
		}
		return calcDeterminant(m,inds, 0);
	}

	private static double calcDeterminant(AMatrix m, int[] inds, int offset) {
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
