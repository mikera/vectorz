package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Class for liner solver algorithms
 * @author Mike
 *
 */
public class Linear {

	/**
	 * Returns the solution to the equation A.x = b
	 * 
	 * Returns null if no unique solution exists
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static AVector solve(AMatrix a, AVector b) {
		if (a.isSquare()) return solveSquare(a,b);
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public static AVector solveSquare(AMatrix a, AVector b) {
		if (!a.isSquare()) throw new IllegalArgumentException("matrix must be square");
		AMatrix m=a.inverse();
		if (m==null) return null;
		return m.transform(b);
	}

}
