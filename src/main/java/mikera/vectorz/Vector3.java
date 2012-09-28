package mikera.vectorz;

public final class Vector3 extends PrimitiveVector {
	public double x;
	public double y;
	public double z;
	
	public Vector3() {
		super();
	}
	
	public Vector3(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public Vector3(double... values) {
		this.x=values[0];
		this.y=values[1];
		this.y=values[2];
	}
	
	@Override
	public int length() {
		return 3;
	}

	@Override
	public double get(int i) {
		switch (i) {
		case 0: return x;
		case 1: return y;
		case 2: return z;
		default: throw new IndexOutOfBoundsException("Index: i");
		}
	}

	@Override
	public void set(int i, double value) {
		switch (i) {
		case 0: x=value; return;
		case 1: y=value; return;
		case 2: z=value; return;
		default: throw new IndexOutOfBoundsException("Index: i");
		}
	}
	
	@Override
	public Vector3 clone() {
		return new Vector3(x,y,z);	
	}

}
