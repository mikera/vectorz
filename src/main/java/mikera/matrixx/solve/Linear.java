package mikera.matrixx.solve;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ColumnMatrix;
import mikera.matrixx.solve.impl.lu.LUSolver;
import mikera.matrixx.solve.impl.qr.QRHouseColSolver;
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

	private Linear(){}
    
    /**
     * 
     * Returns the least squares solution to the equation A.x = b
     * Use this in the case of over-determined (more equations than unknowns) or
     * under-determined (more unknowns than equations)
     * 
     * @param A
     * @param b
     * @return AVector x
     */
    public static AVector solveLeastSquares(AMatrix A, AVector b) {
        QRHouseColSolver solver = new QRHouseColSolver();
        solver.setA(A);
//        create AMatrix from AVector
        AMatrix B = ColumnMatrix.wrap(b);
        AMatrix X = solver.solve(B);
//        convert AMatrix into AVector and return
        return X.asVector();
    }
    
    /**
     * Returns the least squares solution to the equation A.X = B
     * Use this in the case of over-determined (more equations than unknowns) or
     * under-determined (more unknowns than equations)
     * 
     * @param A
     * @param B
     * @return AMatrix X
     */
    public static AMatrix solveLeastSquares(AMatrix A, AMatrix B) {
        QRHouseColSolver solver = new QRHouseColSolver();
        solver.setA(A);
        AMatrix x = solver.solve(B);
        return x;
    }
    
    /**
     * A general linear system solver,
     * Returns the solution to the equation A.x = b, returns null if A is square and
     * has no unique solution.
     * 
     * @param A
     * @param b
     * @return
     */
    public static AVector solve(AMatrix A, AVector b) {
        if (A.isSquare()) 
            return solveSquare(A,b);
        else
            return solveLeastSquares(A, b);
    }

	/**
	 * A general linear system solver,
	 * For a matrix A, returns a matrix whose each column is the
     * solution to the equation A.x = b, where b is the corresponding column
     * of B.
     * Returns null if A is square and equations don't have solutions.
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	public static AMatrix solve(AMatrix A, AMatrix B) {
		if (A.isSquare()) 
		    return solveSquare(A,B);
		else
		    return solveLeastSquares(A, B);
	}
	
	/**
	 * For a square matrix A, returns the solution to the equation A.x = b.
	 * Returns null if equation doesn't have a solution.
	 * 
	 * @param A
	 * @param b
	 * @return
	 */
	private static AVector solveSquare(AMatrix A, AVector b) {
	    A.checkSquare();
	    LUSolver solver = new LUSolver();
	    solver.setA(A);
//      create AMatrix from AVector
	    AMatrix B = ColumnMatrix.wrap(b);
	    AMatrix X = solver.solve(B);
//      if no solution
	    if(X == null)
	        return null;
	    return X.asVector();
	}
	
	/**
     * For a square matrix A, returns a matrix whose each column is the
     * solution to the equation A.x = b, where b is the corresponsing column
     * of B.
     * Returns null if equations don't have a solution.
     * 
     * @param A
     * @param B
     * @return
     */
	private static AMatrix solveSquare(AMatrix A, AMatrix B) {
		A.checkSquare();
		LUSolver solver = new LUSolver();
		solver.setA(A);
		return solver.solve(B);
	}

}
