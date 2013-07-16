package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.util.Maths;

/**
 * Class implementing a standard Cholesky decomposition
 * @author Mike
 *
 */
public class Cholesky {

	public static final Matrix decompose(AMatrix a) {
		return decompose(Matrix.create(a));
	}
	
	public static final Matrix decompose(Matrix a) {
		if (!a.isSquare()) throw new IllegalArgumentException("Matrix must be square for Cholesky decomposition");
		int n=a.rowCount();
		
		Matrix u=Matrix.create(n,n);
		for (int i=0; i<n;i++) {
			double squareSum=0.0;

			for (int j=0; j<i; j++) {
				double crossSum=0.0;
				
				for (int k=0; k<j; k++) {
					crossSum+=u.get(i,k)*u.get(j,k);
				}
				
				final double aij=a.get(i,j);
				double uij=(aij-crossSum)/u.get(j,j);
				u.set(i,j,uij);
				squareSum+=uij*uij;
			}	
			
			double aii =a.get(i,i);
			double uii=Maths.sqrt(aii-squareSum);
			u.set(i,i,uii);
		}
		
		return u;
	}
}
