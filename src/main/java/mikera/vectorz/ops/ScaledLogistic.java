package mikera.vectorz.ops;

import mikera.vectorz.Op;

/**
 * A logistic function, with input scaled by a fixed factor
 * @author Mike
 *
 */
public final class ScaledLogistic extends Op {
	
	private final double factor;
	private final double inverseFactor;
	
	private static final double DEFAULT_SCALE_FACTOR=8.0;

	public static final ScaledLogistic INSTANCE=new ScaledLogistic(DEFAULT_SCALE_FACTOR);
	
	public ScaledLogistic(double d) {
		factor=d;
		inverseFactor=1.0/d;
	}

	private double scaledLogisticFunction(double a) {
		double ea=Math.exp(-factor*a);
		double df=(1/(1.0f+ea));
		if (Double.isNaN(df)) return (a>0)?1:0;
		return df;
	}
	
	private double inverseLogistic (double a) {
		if (a>=1) return 800*inverseFactor;
		if (a<=0) return -800*inverseFactor;
		double ea=a/(1.0-a);
		return inverseFactor*Math.log(ea);
	}
	
	@Override
	public double apply(double x) {
		return scaledLogisticFunction(x);
	}
	
	@Override
	public double applyInverse(double y) {
		return inverseLogistic(y);
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		for (int i=0; i<length; i++) {
			data[i+start]=scaledLogisticFunction(data[i+start]);
		}	
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return factor*y*(1-y);
	}
	
	@Override
	public double derivative(double x) {
		double y=scaledLogisticFunction(x);
		return factor*y*(1-y);
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
	public double averageValue() {
		return 0.5;
	}

}
