package mikera.vectorz.ops;

import java.util.Arrays;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public class IdentityOp extends Op {
	
	public IdentityOp() {
		// no content
	}
	
	@Override
	public double apply(double x) {
		return x;
	}
	
	@Override
	public void applyTo(AVector v) {
		// done!
	}
	
	@Override
	public void applyTo(double[] data) {
		// done!
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		// done!
	}

}
