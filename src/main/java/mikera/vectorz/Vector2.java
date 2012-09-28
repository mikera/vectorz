package mikera.vectorz;

/**
 * Specialised 2D vector
 * @author Mike
 */
public final class Vector2 extends PrimitiveVector {
	public double x;
	public double y;
	
	public Vector2() {
		super();
	}
	
	public Vector2(double x, double y) {
		this.x=x;
		this.y=y;
	}
	
	public Vector2(double... values) {
		this.x=values[0];
		this.y=values[0];
	}
	
	@Override
	public int length() {
		return 2;
	}

	@Override
	public double get(int i) {
		switch (i) {
			case 0: return x;
			case 1: return y;
			default: throw new IndexOutOfBoundsException("Index: i");
		}
	}

	@Override
	public void set(int i, double value) {
		switch (i) {
			case 0: x=value; return;
			case 1: y=value; return;
			default: throw new IndexOutOfBoundsException("Index: i");
		}
	}
	
	@Override
	public Vector2 clone() {
		return new Vector2(x,y);	
	}


}
