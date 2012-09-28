package mikera.vectorz;

/**
 * Specialised 3D vector
 * @author Mike
 */
public final class Vector3 extends PrimitiveVector {
	public double x;
	public double y;
	public double z;
	
	public Vector3() {
		super();
	}
	
	public Vector3(Vector3 source) {
		this.x=source.x;
		this.y=source.y;
		this.z=source.z;
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
	
	public void add(double dx, double dy, double dz) {
		x+=dx;
		y+=dy;
		z+=dz;
	}
	
	public void set(Vector3 a) {
		this.x=a.x;
		this.y=a.y;
		this.z=a.z;
	}
	
	public void addMultiple(double dx, double dy, double dz, double factor) {
		x+=dx*factor;
		y+=dy*factor;
		z+=dz*factor;
	}
	
	public double dotProduct(Vector3 a) {
		return x*a.x+y*a.y+z*a.z;
	}
	
	public void crossProduct(Vector3 a) {
		double tx=y*a.z-z*a.y;
		double ty=z*a.x-x*a.z;
		double tz=x*a.y-y*a.x;			
		x=tx;
		y=ty;
		z=tz;
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
