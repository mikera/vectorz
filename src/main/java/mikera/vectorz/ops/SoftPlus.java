package mikera.vectorz.ops;

import java.io.ObjectStreamException;

import mikera.vectorz.Op;


public final class SoftPlus extends ABoundedOp {
	
	public static final SoftPlus INSTANCE=new SoftPlus();
	
	private static double softplus (double x) {
		if (x>100) return x;
		if (x<-100) return 0.0;
		return Math.log(1.0+Math.exp(x));
	}
	
	private static double inverseSoftplus (double y) {
		return Math.log(Math.exp(y)-1.0);
	}
	
	@Override
	public double apply(double x) {
		return softplus(x);
	}
	
	@Override
	public double applyInverse(double y) {
		return inverseSoftplus(y);
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		for (int i=0; i<length; i++) {
			data[i+start]=softplus(data[i+start]);
		}	
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return 1.0-Math.exp(-y);
	}
	
	@Override
	public double derivative(double x) {
		return Logistic.logisticFunction(x);
	}
	
	@Override
	public Op getDerivativeOp() {
		return Logistic.INSTANCE;
	}

	@Override
	public double minValue() {
		return 0.0;
	}

	@Override
	public double maxValue() {
		return Double.POSITIVE_INFINITY;
	}
	
	@Override
	public double averageValue() {
		return 1.0;
	}
	
	private Object readResolve() throws ObjectStreamException {
		return INSTANCE; 
	}

}
