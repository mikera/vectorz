package mikera.vectorz.ops;

import mikera.util.Rand;

public class StochasticBinary extends ABoundedOp {

	public static final StochasticBinary INSTANCE=new StochasticBinary();

	@Override 
	public boolean isStochastic() {return true;} 
	
	@Override
	public double apply(double x) {
		return (Rand.nextDouble()<x)?1.0:0.0;
	}
	
	@Override
	public double applyInverse(double y) {
		return y;
	}
	
	
	@Override
	public double minValue() {
		return 0.0;
	}

	@Override
	public double maxValue() {
		return 1.0;
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivative(double x) {
		if ((x<0.0)||(x>1.0)) return 0.0;
		return 1.0;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		// output must be 0.0 or 1.0, so average derivative must be 1.0 assuming in-range value of x
		return 1.0;
	}
}
