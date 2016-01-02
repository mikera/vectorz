package mikera.vectorz.ops;

import mikera.vectorz.AVector;
import mikera.vectorz.Op2;

/**
 * Binary operator for `max` function
 * 
 * @author Mike
 *
 */
public final class Add extends Op2 {

	@Override
	public double apply(double x, double y) {
		return x+y;
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
