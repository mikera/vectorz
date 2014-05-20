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
     * @return The bidiagonal matrix.
     */
    public AMatrix getB() {
    	return B;
    }
    
    /**
     * Returns the orthogonal U matrix.
     *
     * @return The extracted Q matrix.
     */
    public AMatrix getU() {
    	return U;
    }
    
    /**
     * Returns the orthogonal V matrix.
     *
     * @return The extracted Q matrix.
     */
    public AMatrix getV() {
    	return V;
    }   
    
}
