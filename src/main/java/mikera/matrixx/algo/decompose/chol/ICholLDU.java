package mikera.matrixx.algo.decompose.chol;

import mikera.matrixx.impl.ADiagonalMatrix;

public interface ICholLDU extends IChol{
	
	/**
	 * Returns a Diagonal matrix from the decomposition
	 * 
	 * @return The diagonal matrix D
	 */
	public ADiagonalMatrix getD();

}
