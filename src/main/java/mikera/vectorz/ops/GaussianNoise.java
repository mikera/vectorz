package mikera.vectorz.ops;

import mikera.util.Rand;
import mikera.vectorz.Op;

/**
 * Operator to add gaussian noise to a value
 * @author Mike
 *
 */
public class GaussianNoise extends Op {
	private final double std;

	public static final GaussianNoise UNIT_NOISE=new GaussianNoise(1.0);
	
	private GaussianNoise(double stdev) {
		std=stdev;
	}
	
	public static GaussianNoise create(double stdDev) {
		return new GaussianNoise(stdDev);
	}

	@Override 
	public boolean isStochastic() {return true;} 
	
	@Override
	public double apply(double x) {
		return x+Rand.nextGaussian()*std;
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		for (int i=0; i<length; i++) {
			data[i+start]+=Rand.nextGaussian()*std;
		}	
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivative(double x) {
		return 1.0;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return 1.0;
	}
	
	@Override
	public Op getDerivativeOp() {
		return Constant.ONE;
	}

	@Override
	public double averageValue() {
		return 0;
	}
}
