package mikera.matrixx.decompose.impl.chol;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.decompose.ICholeskyResult;
import mikera.util.Maths;

/**
 * Class implementing a standard Cholesky decomposition
 * 
 *    A = L.L*
 *    
 * Where: A  is a symmetric (actually Hermitian), positive-definite matrix 
 * and:   L  is a lower triangular matrix
 *        L* is the conjugate transpose of L (which is equal to its transpose, since A is real in Vectorz)
 * 
 * See: http://en.wikipedia.org/wiki/Cholesky_decomposition
 * 
 * @author Mike
 *
 */
public class SimpleCholesky {

	private SimpleCholesky(){}

	/**
	 * Decompose a matrix according the the Cholesky decomposition A = L.L*
	 * 
	 * @param a Any symmetric, positive definite matrix
	 * @return The decomposition result
	 */
	public static final ICholeskyResult decompose(AMatrix a) {
		return decompose(a.toMatrix());
	}
	
	public static final ICholeskyResult decompose(Matrix a) {
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
				double uij=(aij-crossSum);
				
				double ujj=u.get(j,j);
				if (ujj==0.0) return null;
				uij=uij/ujj;
				u.set(i,j,uij);
				squareSum+=uij*uij;
			}	
			
			double aii =a.get(i,i);
			double uii=Maths.sqrt(aii-squareSum);
			u.set(i,i,uii);
		}
		
		// TODO: should be null return for a failed decomposition?
		
		AMatrix L = Matrixx.extractLowerTriangular(u);
		return new CholeskyResult(L);
	}
}
