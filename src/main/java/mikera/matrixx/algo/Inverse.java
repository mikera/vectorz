package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix11;
import mikera.matrixx.Matrix22;
import mikera.matrixx.Matrix33;
import mikera.matrixx.solve.impl.lu.LUSolver;
import mikera.vectorz.util.ErrorMessages;

public final class Inverse {

	private Inverse(){}

	/**
	 * Computes the inverse of an arbitrary square Matrix.
	 * 
	 * Throws an exception if the matrix is not square
	 * 
	 * Returns null if the matrix cannot be inverted
	 */
	public static AMatrix calculate(AMatrix a) {
		int rc=a.checkSquare();
		
		if (rc<=3) return calculateSmall(a,rc);
		
		return createLUPInverse(a);
	}
	
	/**
	 * Computes the inverse of a symmetric matrix
	 */
	public static AMatrix calculateSymmetric(AMatrix a) {
		return calculate(a);
	}
	
	private static AMatrix calculateSmall(AMatrix m, int rc) {
		if (rc==1) return new Matrix11(m).inverse();
		if (rc==2) return new Matrix22(m).inverse();
		if (rc==3) return new Matrix33(m).inverse();
		throw new IllegalArgumentException(ErrorMessages.incompatibleShape(m));
	}
	
	static AMatrix createLUPInverse(AMatrix m) {
		LUSolver lus = new LUSolver();
        lus.setA(m);
        AMatrix im=lus.invert();
		return im;
	}
	
}
