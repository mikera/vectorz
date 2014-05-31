package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrix11;
import mikera.matrixx.Matrix22;
import mikera.matrixx.Matrix33;
import mikera.matrixx.decompose.impl.lu.DecomposeLUP;
import mikera.vectorz.util.ErrorMessages;

public final class Inverse {

	/*
	 * Computes the inverse of an arbitrary Matrix
	 */
	public static AMatrix calculate(AMatrix a) {
		int rc=a.rowCount(); 
		if (rc!=a.columnCount()) throw new IllegalArgumentException(ErrorMessages.nonSquareMatrix(a));
		
		if (rc<=3) return calculateSmall(a,rc);
		
		return createLUPInverse(a);
	}
	
	public static AMatrix calculateSmall(AMatrix m, int rc) {
		if (rc==1) return new Matrix11(1.0/m.unsafeGet(0, 0));
		if (rc==2) return new Matrix22(m).inverse();
		if (rc==3) return new Matrix33(m).inverse();
		throw new IllegalArgumentException(ErrorMessages.incompatibleShape(m));
	}
	
	static Matrix createLUPInverse(AMatrix m) {
		return DecomposeLUP.createLUPInverse(m);
	}
	
}
