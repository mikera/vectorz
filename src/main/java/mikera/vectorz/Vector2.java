package mikera.vectorz;

import java.nio.DoubleBuffer;
import java.util.Arrays;

import mikera.vectorz.impl.APrimitiveVector;
import mikera.vectorz.util.ErrorMessages;

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
	
	public static Vector2 of(double x, double y) {
		return new Vector2(x,y);
	}
	
	public static Vector2 of(double... values) {
		if (values.length!=2) throw new IllegalArgumentException("Can't create Vector2 vector from: "+Arrays.toString(values));
		return new Vector2(values[0],values[1]);
	}
	
	public static Vector2 create(AVector v) {
		if (v.length()!=2) throw new IllegalArgumentException("Can't create Vector2 from vector with length "+v.length());
		return new Vector2(v.unsafeGet(0),v.unsafeGet(1));
	}
	
	@Override
	public void applyOp(Op op) {
		x=op.apply(x);
		y=op.apply(y);
	}
	
	@Override
	public double reduce(Op2 op,double init) {
		init=op.apply(init, x);
		init=op.apply(init, y);
		return init;
	}
	
	@Override
	public double reduce(Op2 op) {
		return op.apply(x, y);
	}
	
	@Override
	public boolean isZero() {
		return (x==0.0)&&(y==0.0);
	}
	
	public void add(Vector2 v) {
		x+=v.x;
		y+=v.y;
	}
	
	public void sub(Vector2 v) {
		x-=v.x;
		y-=v.y;
	}
	
	public void addMultiple(Vector2 v, double factor) {
		x+=v.x*factor;
		y+=v.y*factor;
	}
	
	public void addProduct(Vector2 a, Vector2 b) {
		x+=a.x*b.x;
		y+=a.y*b.y;
	}
	
	public void addProduct(Vector2 a, Vector2 b, double factor) {
		x+=a.x*b.x*factor;
		y+=a.y*b.y*factor;
	}
		
	@Override
	public double dotProduct(AVector a) {
		a.checkLength(2);
		return x*a.unsafeGet(0)+y*a.unsafeGet(1);
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		return x*data[offset+0]+y*data[offset+1];
	}
	
	public double dotProduct(Vector2 a) {
		return x*a.x+y*a.y;
	}
	
	@Override
	public void scaleAdd(double factor, double constant) {
		x=(x*factor)+constant;
		y=(y*factor)+constant;
	}
	
	@Override
	public void scaleAdd(double factor, AVector constant) {
		constant.checkLength(2);
		x=(x*factor)+constant.unsafeGet(0);
		y=(y*factor)+constant.unsafeGet(1);
	}
	
	public void scaleAdd(double factor, Vector2 constant) {
		x=(x*factor)+constant.x;
		y=(y*factor)+constant.y;
	}
	
	/**
	 * Complex multiplication by another Vector2, treating an (x,y) vector as the complex value x+iy
	 * @param a
	 */
	public void complexMultiply(Vector2 a) {
		double nx=x*a.x-y*a.y;
		double ny=x*a.y+y*a.x;
		this.x=nx;
		this.y=ny;	
	}
	
	public Vector2 complexConjugate() {
		return new Vector2(x,-y);
	}
	
	public Vector2 complexReciprocal() {
		double d=x*x+y*y;
		return new Vector2(x/d,-y/d);
	}
	
	public Vector2 complexNegation() {
		return new Vector2(-x,-y);
	}
	
	@Override
	public void negate() {
		x=-x;
		y=-y;
	}
	
	@Override
	public void multiply(double factor) {
		x*=factor;
		y*=factor;
	}

	@Override
	public void add(double constant) {
		x=x+constant;
		y=y+constant;
	}
	
	public void add(double dx, double dy) {
		x=x+dx;
		y=y+dy;
	}
	
	@Override
	public void add(AVector v) {
		v.checkLength(2);
		x+=v.unsafeGet(0);
		y+=v.unsafeGet(1);
	}
	
	@Override
	public int length() {
		return 2;
	}
	
	@Override
	public double elementSum() {
		return x+y;
	}
	
	@Override
	public double elementProduct() {
		return x*y;
	}
	
	@Override
	public double elementMax(){
		return Math.max(x, y);
	}
	
	@Override
	public double elementMin(){
		return Math.min(x, y);
	}
	
	@Override 
	public double elementSquaredSum() {
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
			default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		}
	}
	
	@Override
	public double unsafeGet(int i) {
		return (i==0)?x:y;
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		data[offset]=x;
		data[offset+1]=y;
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(x);
		dest.put(y);
	}
	
	@Override
	public double[] toDoubleArray() {
		return new double[] {x,y};
	}
	
	@Override
	public Vector2 toNormal() {
		double d=this.magnitudeSquared();
		if (d==0) return null;
		d=Math.sqrt(d);
		return new Vector2(x/d,y/d);
	}

	@Override
	public void set(int i, double value) {
		switch (i) {
			case 0: x=value; return;
			case 1: y=value; return;
			default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		}
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		switch (i) {
		case 0: x=value; return;
		default: y=value; return;
		}
	}
	
	@Override 
	public void fill(double v) {
		x=v;
		y=v;
	}
	
	@Override
	public void addAt(int i, double value) {
		switch (i) {
		case 0: x+=value; return;
		default: y+=value; return;
		}
	}
	
	/**
	 * Rotates a 2D vector around the origin by a given angle
	 * @param angle
	 */
	public void rotateInPlace(int angle) {
		double ca=Math.cos(angle);
		double sa=Math.sin(angle);
		double nx=(x*ca)-(y*sa);
		double ny=(x*sa)+(y*ca);
		x=nx;
		y=ny;
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
	
	@Override 
	public Vector2 exactClone() {
		return clone();
	}
	
	@Override 
	public boolean equals(AVector v) {
		if (v instanceof Vector2) {
			return equals((Vector2)v);
		}
		return (v.length()==2)&&(x==v.unsafeGet(0))&&(y==v.unsafeGet(1));
	}
	
	public boolean equals(Vector2 v) {
		return (x==v.x)&&(y==v.y);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return (x==data[offset])&&(y==data[offset+1]);
	}
}
