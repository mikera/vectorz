package mikera.matrixx.solve;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.solve.impl.qr.QRHouseColSolver;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

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
     * Returns the least squares solution to the equation A.x = b
     * 
     * @param a
     * @param b
     * @return AVector x
     */
    public static AVector solveLeastSquares(AMatrix a, AVector b) {
        QRHouseColSolver solver = new QRHouseColSolver();
        solver.setA(a);
//        create AMatrix from AVector
        Matrix B = Matrix.create(b.length(), 1);
        B.setElements(b.asDoubleArray());
        AMatrix X = solver.solve(B);
//        convert AMatrix into AVector and return
        return Vector.create(X.asDoubleArray());
    }
    /**
     * Returns the least squares solution to the equation A.x = b
     * 
     * @param a
     * @param b
     * @return AMatrix x
     */
    public static AMatrix solveLeastSquares(AMatrix a, AMatrix b) {
        QRHouseColSolver solver = new QRHouseColSolver();
        solver.setA(a);
        AMatrix x = solver.solve(b);
        return x;
    }

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
		a.checkSquare();
		AMatrix m=a.inverse();
		if (m==null) return null;
		return m.transform(b);
	}

}
