package mikera.matrixx.algo.decompose.bidiagonal;

import mikera.matrixx.AMatrix;

public class BidiagonalRowResult implements BidiagonalResult {
	
	private final double[] gammasU;
	private final double[] gammasV;
	private AMatrix B;
	private AMatrix U;
	private AMatrix V;
	
	public BidiagonalRowResult(AMatrix U, AMatrix B, AMatrix V, double[] gammasU, double[] gammasV) {
		this.U = U;
		this.B = B;
		this.V = V;
		this.gammasU = gammasU;
		this.gammasV = gammasV;
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

    /**
     * Returns gammas from the householder operations for the U matrix.
     *
     * @return gammas for householder operations
     */
    public double[] getGammasU() {
        return gammasU;
    }

    /**
     * Returns gammas from the householder operations for the V matrix.
     *
     * @return gammas for householder operations
     */
    public double[] getGammasV() {
        return gammasV;
    }
    
    
}
