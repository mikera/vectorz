package mikera.matrixx.decompose.impl.svd;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ISVDResult;

public class SVDResult implements ISVDResult {

	private final AMatrix u;
	private final AMatrix s;
	private final AMatrix v;

	public SVDResult(AMatrix u, AMatrix s, AMatrix v) {
		this.u=u;
		this.s=s;
		this.v=v;
	}
	
	@Override
	public AMatrix getU() {
		return u;
	}

	@Override
	public AMatrix getS() {
		return s;
	}

	@Override
	public AMatrix getV() {
		return v;
	}

}
