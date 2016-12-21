package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;

public interface IPLSResult {
	
	/**
	 * Get the original input matrix,
	 * @return
	 */
	public AMatrix getX();
	
	/**
	 * Get the original output matrix,
	 * @return
	 */
	public AMatrix getY();
	
	/**
	 * Get the score matrix for X
	 * @return
	 */
	public AMatrix getT();
	
	/**
	 * Get the loadings matrix for X
	 * @return
	 */

	public AMatrix getP();

	/**
	 * Get the loadings matrix for Y
	 * @return
	 */
	public AMatrix getQ();

	/**
	 * Get the PLS Weight matrix
	 * @return
	 */
	public AMatrix getW();

	/**
	 * Get the matrix of diagonal coefficients 
	 * @return
	 */
	public AMatrix getB();


	
}
