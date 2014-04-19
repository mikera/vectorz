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
	public AVector solve(AMatrix a, AVector b) {
		if (a.isSquare()) return solveSquare(a,b);
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public AVector solveSquare(AMatrix a, AVector b) {
		if (!a.isSquare()) throw new IllegalArgumentException("matrix must be square");
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
