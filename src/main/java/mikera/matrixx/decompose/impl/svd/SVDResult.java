package mikera.matrixx.decompose.impl.svd;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ISVDResult;
import mikera.matrixx.impl.ADiagonalMatrix;

public class SVDResult implements ISVDResult {

	private final AMatrix u;
	private final ADiagonalMatrix s;
	private final AMatrix v;

	public SVDResult(AMatrix u, ADiagonalMatrix s, AMatrix v) {
		this.u=u;
		this.s=s;
		this.v=v;
	}
	
	@Override
	public AMatrix getU() {
		return u;
	}

	@Override
	public ADiagonalMatrix getS() {
		return s;
	}

	@Override
	public AMatrix getV() {
		return v;
	}

}
