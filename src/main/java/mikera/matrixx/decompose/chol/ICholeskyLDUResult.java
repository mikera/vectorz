package mikera.matrixx.decompose.chol;

import mikera.matrixx.impl.ADiagonalMatrix;

public interface ICholeskyLDUResult extends ICholeskyResult{
	
	/**
	 * Returns a Diagonal matrix from the decomposition
	 * 
	 * @return The diagonal matrix D
	 */
	public ADiagonalMatrix getD();

}