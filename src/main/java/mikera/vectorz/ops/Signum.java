package mikera.vectorz.ops;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;

public class Signum extends ARoundingOp {
	public static final Signum INSTANCE=new Signum();
	
	@Override
	public double apply(double x) {
		return Math.signum(x);
	}
	
	@Override
	public void applyTo(INDArray v) {
		v.signum();
	}
	
	@Override
	public void applyTo(AVector v) {
		v.signum();
	}
}
