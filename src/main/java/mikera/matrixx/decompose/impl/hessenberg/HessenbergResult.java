package mikera.matrixx.decompose.impl.hessenberg;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.IHessenbergResult;

public class HessenbergResult implements IHessenbergResult{
	private final AMatrix H;
	private final AMatrix Q;
	
	public HessenbergResult(AMatrix H, AMatrix Q) {
		this.H = H;
		this.Q = Q;
	}

	/**
     * An upper Hessenberg matrix from the decompostion.
     *
     * @return The extracted H matrix.
     */
	public AMatrix getH() {
		return H;
	}
	
	/**
     * An orthogonal matrix that has the following property: H = Q<sup>T</sup>AQ
     *
     * @return The extracted Q matrix.
     */
	public AMatrix getQ() {
		return Q;
	}
	
}
