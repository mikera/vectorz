package mikera.vectorz;

import mikera.vectorz.impl.APrimitiveVector;

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
		if (values.length!=length()) throw new IllegalArgumentException("Can't create "+length()+"D vector from: "+values);
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
		if (a.length()!=length()) throw new IllegalArgumentException("Vector size mismatch");
		return x*a.unsafeGet(0);
	}
	
	@Override
	public double dotProduct(Vector v) {
		if (v.length()!=length()) throw new IllegalArgumentException("Vector size mismatch");
		return x*v.data[0];
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
	public void applyOp(Op op) {
		x=op.apply(x);
	}

	@Override
	public double get(int i) {
		if (i==0) {
			return x;
		}
		throw new IndexOutOfBoundsException("Index: "+i);
	}

	@Override
	public void set(int i, double value) {
		if (i==0) {
			x=value;
		} else {
			throw new IndexOutOfBoundsException("Index: "+i);
		}
	}
	
	@Override 
	public void fill(double v) {
		x=v;
	}
	
	@Override
	public void copyTo(double[] data, int offset) {
		data[offset]=x;
	}
	
	@Override
	public void addAt(int i, double value) {
		switch (i) {
		case 0: x+=value; return;
		default: throw new IndexOutOfBoundsException("Index: "+i);
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
	public Vector1 clone() {
		return new Vector1(x);	
	}
	
	@Override
	public double getX() {
		return x;
	}
	
	@Override 
	public Vector1 exactClone() {
		return clone();
	}
}
