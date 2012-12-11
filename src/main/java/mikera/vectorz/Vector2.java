package mikera.vectorz;

/**
 * Specialised 2D vector
 * 
 * @author Mike
 */
public final class Vector2 extends APrimitiveVector {
	private static final long serialVersionUID = -7815583836324137277L;

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
		if (values.length!=length()) throw new IllegalArgumentException("Can't create "+length()+"D vector from: "+values);
		this.x=values[0];
		this.y=values[1];
	}
	
	public static Vector2 of(double x, double y) {
		return new Vector2(x,y);
	}
	
	public static Vector2 of(double... values) {
		return new Vector2(values);
	}
	
	public void add(Vector2 v) {
		x+=v.x;
		y+=v.y;
	}
	
	public void addMultiple(Vector2 v, double factor) {
		x+=v.x*factor;
		y+=v.y*factor;
	}
	
	@Override
	public int length() {
		return 2;
	}
	
	@Override 
	public double magnitudeSquared() {
		return (x*x)+(y*y);
	}
	
	@Override 
	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
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
	
	public void setValues(double x, double y) {
		this.x=x;
		this.y=y;
	}
	
	@Override
	public Vector2 clone() {
		return new Vector2(x,y);	
	}

	@Override
	public double getX() {
		return x;
	}
	
	@Override
	public double getY() {
		return y;
	}
}
