package mikera.vectorz.ops;

public final class Tanh extends ABoundedOp {
	
	public static final Tanh INSTANCE=new Tanh();
	
	@Override
	public double apply(double x) {
		return Math.tanh(x);
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		for (int i=0; i<length; i++) {
			data[i+start]=Math.tanh(data[i+start]);
		}	
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return 1.0-y*y;
	}
	
	@Override
	public double derivative(double x) {
		double y=Math.tanh(x);
		return 1.0-y*y;
	}

	@Override
	public double minValue() {
		return -1.0;
	}
	
	@Override
	public double averageValue() {
		return 0.0;
	}

	@Override
	public double maxValue() {
		return 1.0;
	}

}
