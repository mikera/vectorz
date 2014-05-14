package mikera.matrixx.algo.decompose.chol.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.algo.decompose.chol.ICholeskyLDU;
import mikera.matrixx.impl.ADiagonalMatrix;
import mikera.matrixx.impl.IdentityMatrix;

public class CholeskyResult implements ICholeskyLDU {

	private final AMatrix L;
	private final ADiagonalMatrix D;
	private final AMatrix U;
	
	public CholeskyResult(AMatrix L) {
		this(L,IdentityMatrix.create(L.rowCount()),L.getTranspose());
	}

	public CholeskyResult(AMatrix L, ADiagonalMatrix D, AMatrix U) {
		this.L = L;
		this.D = D;
		this.U = U;
	}

	@Override
	public AMatrix getL() {
		return L;
	}

	@Override
	public AMatrix getU() {
		return U;
	}

	@Override
	public ADiagonalMatrix getD() {
		return D;
	}

}
