package mikera.matrixx.decompose.impl.svd;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ISVDResult;

public class SVDResult implements ISVDResult {
	
	private final AMatrix U;
	private final AMatrix S;
	private final AMatrix V;
	
	public SVDResult(AMatrix U, AMatrix S, AMatrix V) {
		this.U = U;
		this.S = S;
		this.V = V;
	}

	/**
     * <p>
     * Returns the orthogonal 'U' matrix.
     * </p>
     * @return An orthogonal matrix.
     */
	@Override
	public AMatrix getU() {
		return U;
	}

	/**
     * Returns a diagonal matrix with the singular values.  Order of the singular values
     * is not guaranteed.
     *
     * @return matrix with singular values along the diagonal.
     */
	@Override
	public AMatrix getS() {
		return S;
	}

	/**
     * <p>
     * Returns the orthogonal 'V' matrix.
     * </p>
     * @return An orthogonal matrix.
     */
	@Override
	public AMatrix getV() {
		return V;
	}

}
