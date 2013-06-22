package mikera.vectorz.ops;

public abstract class ARoundingOp extends AFunctionOp {

	@Override
	public double averageValue() {
		return apply(0);
	}
	
	@Override
	public boolean hasInverse() {
		return false;
	}
	
	@Override 
	public boolean hasDerivativeForOutput() {
		return true;
	}
	
	@Override
	public double derivative(double x) {
		return 0.0;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return 0.0;
	}
}
