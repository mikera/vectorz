package mikera.vectorz.ops;

public final class Logistic extends ABoundedOp {
	
	public static final Logistic INSTANCE=new Logistic();
	
	public static double logisticFunction(double a) {
		double ea=Math.exp(-a);
		double df=(1/(1.0f+ea));
		if (Double.isNaN(df)) return (a>0)?1:0;
		return df;
	}
	
	@Override
	public double apply(double x) {
		return logisticFunction(x);
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		for (int i=0; i<length; i++) {
			data[i+start]=logisticFunction(data[i+start]);
		}	
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return y*(1-y);
	}

	@Override
	public double minValue() {
		return 0.0;
	}

	@Override
	public double maxValue() {
		return 1.0;
	}

}
