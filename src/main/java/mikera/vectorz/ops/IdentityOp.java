package mikera.vectorz.ops;

import mikera.transformz.ATransform;
import mikera.transformz.Transformz;
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
	
	@Override
	public ATransform getTransform(int dimensions) {
		return Transformz.identityTransform(dimensions);
	}
}
