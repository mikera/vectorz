package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ADiagonalMatrix;

/**
 * @author prasant
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
    public AMatrix getW();
    
    /**
     * <p>
     * Returns the orthogonal 'V' matrix.
     * </p>
     * @return An orthogonal matrix.
     */
    public AMatrix getV();
}
