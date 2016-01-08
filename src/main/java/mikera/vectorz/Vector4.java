package mikera.vectorz;

import java.nio.DoubleBuffer;

import mikera.vectorz.impl.APrimitiveVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised 4D vector
 * 
 * @author Mike
 */
public final class Vector4 extends APrimitiveVector {
	private static final long serialVersionUID = -6018622211027585397L;

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
		if (values.length!=length()) throw new IllegalArgumentException("Can't create "+length()+"D vector from values of length: "+values.length);
		this.x=values[0];
		this.y=values[1];
		this.z=values[2];
		this.t=values[3];
	}
	
	public static Vector4 of(double x, double y, double z, double t) {
		return new Vector4(x,y,z,t);
	}
	
	public static Vector4 of(double... values) {
		return new Vector4(values);
	}
	
	@Override
	public void applyOp(Op op) {
		x=op.apply(x);
		y=op.apply(y);
		z=op.apply(z);
		t=op.apply(t);
	}
	
	@Override
	public double reduce(Op2 op,double init) {
		init=op.apply(init, x);
		init=op.apply(init, y);
		init=op.apply(init, z);
		init=op.apply(init, t);
		return init;
	}
	
	@Override
	public double reduce(Op2 op) {
		double result=op.apply(x, y);
		result=op.apply(result, z);
		result=op.apply(result, t);
		return result;
	}
	
	@Override
	public boolean isZero() {
		return (x==0.0)&&(y==0.0)&&(z==0.0)&&(t==0.0);
	}

	public void add(double dx, double dy, double dz, double dt) {
		x+=dx;
		y+=dy;
		z+=dz;
		t+=dt;
	}
	
	@Override
	public void add(AVector v) {
		if (v.length()!=4) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));
		}
		x+=v.unsafeGet(0);
		y+=v.unsafeGet(1);
		z+=v.unsafeGet(2);
		t+=v.unsafeGet(3);
	}
	
	public void add(Vector4 a) {
		this.x+=a.x;
		this.y+=a.y;
		this.z+=a.z;
		this.t+=a.t;
	}
	
	public void set(Vector4 a) {
		this.x=a.x;
		this.y=a.y;
		this.z=a.z;
		this.t=a.t;
	}
	
	@Override
	public void negate() {
		x=-x;
		y=-y;
		z=-z;
		t=-t;
	}
	
	@Override
	public void multiply(double factor) {
		x*=factor;
		y*=factor;
		z*=factor;
		t*=factor;
	}
	
	public void addMultiple(double dx, double dy, double dz, double dt, double factor) {
		x+=dx*factor;
		y+=dy*factor;
		z+=dz*factor;
		t+=dt*factor;
	}
	
	@Override
	public void addMultiple(AVector v, double factor) {
		if (v instanceof Vector4) {
			addMultiple((Vector4)v,factor);
		} else {
			v.checkLength(4);
			x+=v.unsafeGet(0)*factor;
			y+=v.unsafeGet(1)*factor;
			z+=v.unsafeGet(2)*factor;
			t+=v.unsafeGet(3)*factor;
		}
	}
	
	public void addMultiple(Vector4 v, double factor) {
		x+=v.x*factor;
		y+=v.y*factor;
		z+=v.z*factor;
		t+=v.t*factor;
	}
	
	public void addProduct(Vector4 a, Vector4 b) {
		x+=a.x*b.x;
		y+=a.y*b.y;
		z+=a.z*b.z;
		t+=a.t*b.t;
	}
	
	public void addProduct(Vector4 a, Vector4 b, double factor) {
		x+=a.x*b.x*factor;
		y+=a.y*b.y*factor;
		z+=a.z*b.z*factor;
		t+=a.t*b.t*factor;
	}
	
	public double dotProduct(Vector4 a) {
		return (x*a.x)+(y*a.y)+(z*a.z)+(t*a.t);
	}
	
	@Override
	public double dotProduct(double[] as, int offset) {
		return (x*as[offset])+(y*as[offset+1])+(z*as[offset+2])+(t*as[offset+3]);
	}
	
	@Override
	public double dotProduct(AVector v) {
		v.checkLength(4);
		return x*v.unsafeGet(0)+y*v.unsafeGet(1)+z*v.unsafeGet(2)+t*v.unsafeGet(3);
	}
	
	@Override
	public int length() {
		return 4;
	}
	
	@Override
	public double elementSum() {
		return x+y+z+t;
	}
	
	@Override 
	public double elementSquaredSum() {
		return (x*x)+(y*y)+(z*z)+(t*t);
	}
	
	@Override
	public double elementProduct() {
		return x*y*z*t;
	}

	@Override
	public double get(int i) {
		switch (i) {
		case 0: return x;
		case 1: return y;
		case 2: return z;
		case 3: return t;
		default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		}
	}

	@Override
	public void set(int i, double value) {
		switch (i) {
		case 0: x=value; return;
		case 1: y=value; return;
		case 2: z=value; return;
		case 3: t=value; return;
		default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		}
	}
	
	@Override
	public void addAt(int i, double value) {
		switch (i) {
		case 0: x+=value; return;
		case 1: y+=value; return;
		case 2: z+=value; return;
		case 3: t+=value; return;
		default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		}
	}
	
	public void setValues(double x, double y, double z, double t) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.t=t;
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		data[offset]=x;
		data[offset+1]=y;
		data[offset+2]=z;
		data[offset+3]=t;
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(x);
		dest.put(y);
		dest.put(z);
		dest.put(t);
	}
	
	@Override
	public double[] toDoubleArray() {
		return new double[] {x,y,z,t};
	}
	
	@Override
	public Vector4 clone() {
		return new Vector4(x,y,z,t);	
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
	public double getZ() {
		return z;
	}
	
	@Override
	public double getT() {
		return t;
	}
	
	@Override 
	public Vector4 exactClone() {
		return clone();
	}

	@Override
	public boolean equalsArray(double[] data, int offset) {
		return (x==data[offset])&&(y==data[offset+1])&&(z==data[offset+2])&&(t==data[offset+3]);
	}
}
