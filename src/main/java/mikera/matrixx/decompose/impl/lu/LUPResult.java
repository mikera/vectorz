package mikera.matrixx.decompose.impl.lu;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ILUPResult;
import mikera.matrixx.impl.PermutationMatrix;

public class LUPResult implements ILUPResult {
	final AMatrix l;
	final AMatrix u;
	final PermutationMatrix p;
	private boolean isSingular;

	public LUPResult(AMatrix l, AMatrix u, PermutationMatrix p, boolean isSingular) {
		this.l=l;
		this.u=u;
		this.p=p;
		this.isSingular = isSingular;
	}
	
	public LUPResult(AMatrix l, AMatrix u, boolean isSingular, double pivsign) {
		this (l,u,PermutationMatrix.createIdentity(l.rowCount()), isSingular);
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
	public PermutationMatrix getP() {
		return p;
	}

	@Override
	public boolean isSingular() {
		if (l.rowCount() != u.columnCount())
			throw new IllegalArgumentException("Matrix must be a square matrix");
		return isSingular;
	}

	@Override
	public double computeDeterminant() {
		if (l.rowCount() != u.columnCount())
			throw new IllegalArgumentException("Matrix must be a square matrix");
		double ret = pivsign();
		for (int i=0; i<u.columnCount(); i++) {
			ret *= u.unsafeGet(i, i);
		}
		return ret;
	}
	
	private double pivsign() {
		double ret = 1;
		for(int i=0; i<p.rowCount(); i++) {
			if (p.unsafeGet(i, i) == 1) {
				ret = ret == 1 ? -1 : 1;
			}
		}
		return ret;
	}

}
