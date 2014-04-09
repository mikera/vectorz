package mikera.matrixx.algo.decompose.lu.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.algo.decompose.lu.ILUP;
import mikera.matrixx.impl.IdentityMatrix;

public class LUPResult implements ILUP {
	final AMatrix l;
	final AMatrix u;
	final AMatrix p;

	public LUPResult(AMatrix l, AMatrix u, AMatrix p) {
		this.l=l;
		this.u=u;
		this.p=p;
	}
	
	public LUPResult(AMatrix l, AMatrix u) {
		this (l,u,IdentityMatrix.create(l.rowCount()));
	}

	@Override
	public AMatrix getL() {
		return l;
	}

	@Override
	public AMatrix getU() {
		return u;
	}

	@Override
	public AMatrix getP() {
		return p;
	}

}
