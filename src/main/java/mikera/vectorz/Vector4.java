package mikera.vectorz;

/**
 * Specialised 3D vector
 * @author Mike
 */
public final class Vector4 extends PrimitiveVector {
	public double x;
	public double y;
	public double z;
	public double t;
	
	public Vector4() {
		super();
	}
	
	public Vector4(Vector4 source) {
		this.x=source.x;
		this.y=source.y;
		this.z=source.z;
		this.t=source.t;
	}
	
	public Vector4(double x, double y, double z, double t) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.t=t;
	}
	
	public Vector4(double... values) {
		this.x=values[0];
		this.y=values[1];
		this.z=values[2];
		this.t=values[3];
	}
	
	public static Vector4 create(double... values) {
		return new Vector4(values);
	}
	
	public void add(double dx, double dy, double dz, double dt) {
		x+=dx;
		y+=dy;
		z+=dz;
		t+=dt;
	}
	
	public void set(Vector4 a) {
		this.x=a.x;
		this.y=a.y;
		this.z=a.z;
		this.t=a.t;
	}
	
	public void addMultiple(double dx, double dy, double dz, double dt, double factor) {
		x+=dx*factor;
		y+=dy*factor;
		z+=dz*factor;
		t+=dt*factor;
	}
	
	public double dotProduct(Vector4 a) {
		return x*a.x+y*a.y+z*a.z+t*a.t;
	}

	
	@Override
	public int length() {
		return 4;
	}

	@Override
	public double get(int i) {
		switch (i) {
		case 0: return x;
		case 1: return y;
		case 2: return z;
		case 3: return t;
		default: throw new IndexOutOfBoundsException("Index: i");
		}
	}

	@Override
	public void set(int i, double value) {
		switch (i) {
		case 0: x=value; return;
		case 1: y=value; return;
		case 2: z=value; return;
		case 3: t=value; return;
		default: throw new IndexOutOfBoundsException("Index: i");
		}
	}
	
	@Override
	public Vector4 clone() {
		return new Vector4(x,y,z,t);	
	}

}
