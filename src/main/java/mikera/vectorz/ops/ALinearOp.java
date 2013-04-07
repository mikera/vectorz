package mikera.vectorz.ops;

public abstract class ALinearOp extends APolynomialOp {

	public abstract double getFactor();
	
	public abstract double getConstant();
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivative(double x) {
		return getFactor();
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return getFactor();
	}
	
	@Override
	public boolean hasInverse() {
		return true;
	}
}
