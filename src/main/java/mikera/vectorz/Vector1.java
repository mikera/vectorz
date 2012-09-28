package mikera.vectorz;

/**
 * Specialised 2D vector
 * @author Mike
 */
public final class Vector1 extends PrimitiveVector {
	public double x;
	
	public Vector1() {
		super();
	}
	
	public Vector1(double x, double y) {
		this.x=x;
	}
	
	public Vector1(double... values) {
		this.x=values[0];
	}
	
	public static Vector1 create(double... values) {
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
		} else {
			throw new IndexOutOfBoundsException("Index: i");
		}
	}

	@Override
	public void set(int i, double value) {
		if (i==0) {
			x=value;
		} else {
			throw new IndexOutOfBoundsException("Index: i");
		}
	}
	
	@Override
	public Vector1 clone() {
		return new Vector1(x);	
	}


}
