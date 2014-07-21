package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;

/**
 * Interface representing the result of a hessenberg decomposition
 * 
 * @author prasant
 */
public interface IHessenbergResult {
	
	/**
     * An upper Hessenberg matrix from the decompostion.
     *
     * @return The extracted H matrix.
     */
	public AMatrix getH();
	
	/**
     * An orthogonal matrix that has the following property: H = Q<sup>T</sup>AQ
     *
     * @return The extracted Q matrix.
     */
	public AMatrix getQ();

}
