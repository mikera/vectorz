package mikera.vectorz.ops;

import mikera.vectorz.AVector;
import mikera.vectorz.Op2;

/**
 * Binary operator to compute derivative of cross entropy loss multiplied by derivative of
 * logistic function.
 * given two probability values in range 0..1
 * 
 * @author Mike
 *
 */
public final class CrossEntropyLogisticDerivative extends Op2 {

	@Override
	public double apply(double x, double y) {
		return y-x;
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
