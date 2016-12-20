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
     * @param B If not null the results are stored here, if null a new matrix is created.
     * @return The bidiagonal matrix.
     */
    public AMatrix getB();
    
    /**
     * Returns the orthogonal U matrix.
     *
     * @param U If not null then the results will be stored here.  Otherwise a new matrix will be created.
     * @return The extracted Q matrix.
     */
    public AMatrix getU();
    
    /**
     * Returns the orthogonal V matrix.
     *
     * @param V If not null then the results will be stored here.  Otherwise a new matrix will be created.
     * @return The extracted Q matrix.
     */
    public AMatrix getV();
    
}
