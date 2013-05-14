package mikera.vectorz.ops;

import mikera.vectorz.Op;
import mikera.vectorz.Ops;

public class Power extends Op {
	private double exponent;
	
	private Power(double d) {
		exponent=d;
	}
	
	public static Op create(double exponent) {
		if (exponent==0) return Constant.ONE;
		if (exponent==1) return Identity.INSTANCE;
		if (exponent==2) return Ops.SQUARE;
		return new Power(exponent);
	}
	
	@Override 
	public double minDomain() {
		if (exponent!=(long)exponent) return 0.0;
		return super.minDomain();
	}
	
	@Override
	public double apply(double x) {
		return Math.pow(x, exponent);
	}

	@Override
	public double averageValue() {
		return 1;
	}

}
