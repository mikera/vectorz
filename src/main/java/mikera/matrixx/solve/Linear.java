package mikera.matrixx.solve;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Class providing liner solver algorithms that find the solution to systems of the form:
 * 
 *    A.x = b
 * 
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
		if (!a.isSquare()) throw new IllegalArgumentException("Matrix must be square but got shape: "+Index.of(a.getShape()));
		AMatrix m=a.inverse();
		if (m==null) return null;
		return m.transform(b);
	}

}
