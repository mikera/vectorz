package mikera.matrixx.algo.decompose.bidiagonal;

import mikera.matrixx.AMatrix;

public class BidiagonalRowResult implements BidiagonalResult {
	
	private AMatrix B;
	private AMatrix U;
	private AMatrix V;
	
	public BidiagonalRowResult(AMatrix U, AMatrix B, AMatrix V) {
		this.U = U;
		this.B = B;
		this.V = V;
	}
	
	/**
     * Returns the bidiagonal matrix.
     *
     * @param B If not null the results are stored here, if null a new matrix is created.
     * @return The bidiagonal matrix.
     */
    public AMatrix getB() {
    	return B;
    }
    
    /**
     * Returns the orthogonal U matrix.
     *
     * @param U If not null then the results will be stored here.  Otherwise a new matrix will be created.
     * @return The extracted Q matrix.
     */
    public AMatrix getU() {
    	return U;
    }
    
    /**
     * Returns the orthogonal V matrix.
     *
     * @param V If not null then the results will be stored here.  Otherwise a new matrix will be created.
     * @return The extracted Q matrix.
     */
    public AMatrix getV() {
    	return V;
    }   
    
}
