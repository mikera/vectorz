package mikera.matrixx.decompose;

import mikera.matrixx.impl.PermutationMatrix;

/**
 * Interface for results of LUP decomposition

 * <p>
 * LUP Decomposition refactors the original matrix such that:<br>
 * <div align=center> P<sup>T</sup>*L*U = A</div> where P is a pivot matrix, L
 * is a lower triangular matrix, U is an upper triangular matrix and A is the
 * original matrix.
 * </p>
 * <p/>
 * <p>
 * LUP Decomposition is useful since once the decomposition has been performed
 * linear equations can be quickly solved and the original matrix A inverted.
 * Different algorithms can be selected to perform the decomposition, all will
 * have the same end result.
 * </p>
 *
 * @author Peter Abeles
 */
public interface ILUPResult extends ILUResult {
	
	  /**
	   * <p>
	   * Returns the P matrix from the decomposition.
	   * </p>
	   *
	   * @return The P matrix.
	   */
	  public PermutationMatrix getP();

}
