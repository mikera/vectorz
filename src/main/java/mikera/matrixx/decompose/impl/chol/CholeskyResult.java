package mikera.matrixx.decompose.impl.chol;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ICholeskyLDUResult;
import mikera.matrixx.impl.ADiagonalMatrix;
import mikera.matrixx.impl.IdentityMatrix;

public class CholeskyResult implements ICholeskyLDUResult {

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

	/**
	 * <p>
	 * Returns the lower triangular matrix from the decomposition.
	 * </p>
	 * @return A lower triangular matrix.
	 */
	@Override
	public AMatrix getL() {
		return L;
	}

	/**
	 * <p>
	 * Returns the upper triangular matrix from the decomposition.
	 * </p>
	 * @return An upper triangular matrix.
	 */
	@Override
	public AMatrix getU() {
		return U;
	}

	/**
	 * <p>
	 * Returns the diagonal matrix from the decomposition.
	 * </p>
	 * @return A diagonal matrix.
	 */
	@Override
	public ADiagonalMatrix getD() {
		return D;
	}

}
