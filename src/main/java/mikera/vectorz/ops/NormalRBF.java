package mikera.vectorz.ops;

public class NormalRBF extends ABoundedOp {

	public static final NormalRBF INSTANCE=new NormalRBF();
	
	@Override
	public double apply(double x) {
		return Math.exp(-(x*x));
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public boolean hasDerivativeForOutput() {
		return false;
	}
	
	@Override
	public double derivative(double x) {
		return -2*x*Math.exp(-(x*x));
	}

	@Override
	public double averageValue() {
		return 0.5;
	}

	@Override
	public double minValue() {
		return 0.0;
	}
	
	@Override
	public boolean hasInverse() {
		return false;
	}

	@Override
	public double maxValue() {
		return 1.0;
	}

}
