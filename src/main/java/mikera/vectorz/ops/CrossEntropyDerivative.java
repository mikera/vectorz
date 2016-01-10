package mikera.vectorz.ops;

import mikera.vectorz.AVector;
import mikera.vectorz.Op2;

/**
 * Binary operator to compute derivative of cross entropy loss between an output and target value
 * given two probability values in range 0..1
 * 
 * @author Mike
 *
 */
public final class CrossEntropyDerivative extends Op2 {

	@Override
	public double apply(double x, double y) {
		if (x==0) return (y==0)?0:Double.NEGATIVE_INFINITY;
		if (x==1) return (y==1)?0:Double.POSITIVE_INFINITY;
		return -((1-y)/(1-x)+y/x);
	}
	
	@Override
	public void applyTo(double[] data, int start, int length, AVector b) {
		b.checkLength(length);
		for (int i=0; i<length; i++) {
			double x=data[start+i];
			data[start+i]=apply(x,b.unsafeGet(i));
		}
	}
}
