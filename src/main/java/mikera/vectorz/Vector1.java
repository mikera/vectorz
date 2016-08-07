package mikera.vectorz;

import mikera.vectorz.impl.APrimitiveVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised 1D vector
 * 
 * @author Mike
 */
public final class Vector1 extends APrimitiveVector {
	private static final long serialVersionUID = -6312801771839902928L;

	public double x;
	
	public Vector1() {
		super();
	}
	
	public Vector1(double x) {
		this.x=x;
	}
	
	public Vector1(double... values) {
		if (values.length!=length()) throw new IllegalArgumentException("Can't create "+length()+"D vector from values with length: "+values.length);
		this.x=values[0];
	}
	
	public static Vector1 of(double x) {
		return new Vector1(x);
	}
	
	public static Vector1 of(double... values) {
		return new Vector1(values);
	}
	
	@Override
	public double dotProduct(AVector a) {
		a.checkLength(1);
		return x*a.unsafeGet(0);
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		return x*data[offset+0];
	}
	
	@Override
	public int length() {
		return 1;
	}
	
	@Override
	public double elementSum() {
		return x;
	}
	
	@Override 
	public double elementSquaredSum() {
		return (x*x);
	}
	
	@Override
	public double elementProduct() {
		return x;
	}
	
	@Override
	public double elementMax(){
		return x;
	}
	
	@Override
	public double elementMin(){
		return x;
	}
	
	@Override
	public void applyOp(Op op) {
		x=op.apply(x);
	}
	
	@Override
	public double reduce(Op2 op,double init) {
		return op.apply(init, x);
	}
	
	@Override
	public double reduce(Op2 op) {
		return x;
	}

	@Override
	public double get(int i) {
		if (i==0) {
			return x;
		}
		throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
	}
	
	@Override
	public double unsafeGet(int i) {
		return x;
	}

	@Override
	public void set(int i, double value) {
		if (i==0) {
			x=value;
		} else {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		}
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		x=value;
	}
	
	@Override 
	public void fill(double v) {
		x=v;
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		data[offset]=x;
	}
	
	@Override
	public void add(AVector v) {
		if (v.length()!=1) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));
		}
		x+=v.unsafeGet(0);
	}
	
	@Override
	public void addAt(int i, double value) {
		switch (i) {
		case 0: x+=value; return;
		default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		}
	}
	
	public void setValues(double x) {
		this.x=x;
	}
	
	@Override
	public void negate() {
		x=-x;
	}
	
	@Override
	public void multiply(double factor) {
		x*=factor;
	}
	
	@Override
	public boolean isZero() {
		return (x==0.0);
	}
	
	@Override
	public Vector1 clone() {
		return new Vector1(x);	
	}
	
	@Override
	public double[] toDoubleArray() {
		return new double[] {x};
	}
	
	@Override
	public double getX() {
		return x;
	}
	
	@Override 
	public Vector1 exactClone() {
		return clone();
	}
	
	@Override 
	public boolean equals(AVector v) {
		if (v instanceof Vector1) {
			return equals((Vector1)v);
		}
		return (v.length()==1)&&(x==v.unsafeGet(0));
	}
	
	public boolean equals(Vector1 v) {
		return (x==v.x);
	}

	@Override
	public boolean equalsArray(double[] data, int offset) {
		return x==data[offset];
	}

}
