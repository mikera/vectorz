package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Interface representing the result of an SVD decomposition
 * 
 * @author Mike
 */
public interface ISVDResult {
	/**
     * <p>
     * Returns the orthogonal 'U' matrix.
     * </p>
     * @return An orthogonal matrix.
     */
    public AMatrix getU();
    
    /**
     * Returns a diagonal matrix with the singular values.  Order of the singular values
     * is not guaranteed.
     *
     * @return matrix with singular values along the diagonal.
     */
    public AMatrix getS();
    
    /**
     * <p>
     * Returns the orthogonal 'V' matrix.
     * </p>
     * @return An orthogonal matrix.
     */
    public AMatrix getV();
    
    /**
     * Returns an array containing singular values. The non zero singular values are
     * the square roots of the non-zero eigenvalues of M<sup>T</sup>M and MM<sup>T</sup>, 
     * where M is the input matrix.
     * 
     * @return
     */
    public AVector getSingularValues();
}
