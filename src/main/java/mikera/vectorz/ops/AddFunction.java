package mikera.vectorz.ops;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;

/**
 * Binary operator for z = a.y + b.f(x) function
 * 
 * @author Mike
 *
 */
public final class AddFunction extends Op2 {
	private final double a; // factor for first argument
	private final double b; // factor for function argument
	private final Op f;
	
	public AddFunction(double a, double b, Op f) {
		this.a=a;
		this.b=b;
		this.f=f;
	}

	public static AddFunction create(Op f) {
		return new AddFunction(1.0,1.0,f);
	}
	
	public static AddFunction create(double b, Op f) {
		return new AddFunction(1.0,b,f);
	}
	
	public static AddFunction create(double a,double b, Op f) {
		return new AddFunction(a,b,f);
	}
	
	@Override
	public double apply(double x, double y) {
		return a*x+b*f.apply(x);
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
