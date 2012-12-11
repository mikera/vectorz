package mikera.vectorz;

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
	public int length() {
		return 1;
	}

	@Override
	public double get(int i) {
		if (i==0) {
			return x;
		}
		throw new IndexOutOfBoundsException("Index: i");
	}

	@Override
	public void set(int i, double value) {
		if (i==0) {
			x=value;
		} else {
			throw new IndexOutOfBoundsException("Index: i");
		}
	}
	
	public void setValues(double x) {
		this.x=x;
	}
	
	@Override
	public Vector1 clone() {
		return new Vector1(x);	
	}
	
	@Override
	public double getX() {
		return x;
	}
}
