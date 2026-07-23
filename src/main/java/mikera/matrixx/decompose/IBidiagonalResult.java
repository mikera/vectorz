package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;

/**
 * Interface representing the result of a Bidiagonal decompostion
 * 
 * @author prasant
 */
public interface IBidiagonalResult {

	/**
     * Returns the bidiagonal matrix.
     *
     * @return The bidiagonal matrix.
     */
    public AMatrix getB();
    
    /**
     * Returns the orthogonal U matrix.
     *
     * @return The extracted U matrix.
     */
    public AMatrix getU();
    
    /**
     * Returns the orthogonal V matrix.
     *
     * @return The extracted V matrix.
     */
    public AMatrix getV();
    
}
