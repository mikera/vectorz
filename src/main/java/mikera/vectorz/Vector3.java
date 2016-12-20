package mikera.vectorz;

import java.nio.DoubleBuffer;

import mikera.vectorz.impl.APrimitiveVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised 3D vector
 * 
 * Represents a point in 3D x,y,z space.
 * 
 * @author Mike
 */
public final class Vector3 extends APrimitiveVector {
	private static final long serialVersionUID = 2338611313487869443L;

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
	
	@Override
	public void applyOp(Op op) {
		x=op.apply(x);
		y=op.apply(y);
		z=op.apply(z);
	}
	
	@Override
	public double reduce(Op2 op,double init) {
		init=op.apply(init, x);
		init=op.apply(init, y);
		init=op.apply(init, z);
		return init;
	}
	
	@Override
	public double reduce(Op2 op) {
		double result=op.apply(x, y);
		result=op.apply(result, z);
		return result;
	}
	
	@Override
	public double normalise() {
		double d=magnitude();
		if (d>0) multiply(1.0/d);
		return d;
	}
	
	public Vector3(double... values) {
		if (values.length!=length()) throw new IllegalArgumentException("Can't create "+length()+"D vector from values with length: "+values.length);
		this.x=values[0];
		this.y=values[1];
		this.z=values[2];
	}
	
	public Vector3(AVector v) {
		assert(v.length()==3);
		this.set(v);
	}

	public static Vector3 of(double x, double y, double z) {
		return new Vector3(x,y,z);
	}
	
	public static Vector3 of(double... values) {
		return new Vector3(values);
	}
	
	public static Vector3 create(Object o) {
		return create(Vectorz.create(o));
	}
	
	public static Vector3 create(AVector v) {
		return new Vector3(v);
	}
	
	@Override
	public boolean isZero() {
		return (x==0.0)&&(y==0.0)&&(z==0.0);
	}
	
	@Override
	public double angle(AVector v) {
		if (v instanceof Vector3) {return angle((Vector3)v);}
		return super.angle(v);
	}
	
	public double angle(Vector3 v) {
		double mag2=(x*x)+(y*y)+(z*z);
		double vmag2=(v.x*v.x)+(v.y*v.y)+(v.z*v.z);
		double dot=(x*v.x)+(y*v.y)+(z*v.z);
		return Math.acos(dot/Math.sqrt(mag2*vmag2));
	}
	
	public void add(double dx, double dy, double dz) {
		x+=dx;
		y+=dy;
		z+=dz;
	}
	
	@Override 
	public double elementSquaredSum() {
		return (x*x)+(y*y)+(z*z);
	}
	
	public double distanceSquared(Vector3 v) {
		double dx=x-v.x, dy=y-v.y, dz=z-v.z;
		return (dx*dx)+(dy*dy)+(dz*dz);
	}
	
	public double distance(Vector3 v) {
		return Math.sqrt(distanceSquared(v));
	}
	
	@Override
	public double distance(AVector v) {
		if (v instanceof Vector3) {
			return distance((Vector3)v);
		}
		return super.distance(v);
	}
	
	@Override 
	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
	}
	
	public void set(Vector3 a) {
		this.x=a.x;
		this.y=a.y;
		this.z=a.z;
	}
	
	@Override
	public void multiply(double d){
		x*=d;
		y*=d;
		z*=d;
	}
	
	@Override
	public Vector3 multiplyCopy(double d){
		return new Vector3(x*d,y*d,z*d);
	}
	
	public void addMultiple(double dx, double dy, double dz, double factor) {
		x+=dx*factor;
		y+=dy*factor;
		z+=dz*factor;
	}
	
	@Override
	public void addMultiple(AVector v, double factor) {
		if (v instanceof Vector3) {
			addMultiple((Vector3)v,factor);
		} else {
			x+=v.unsafeGet(0)*factor;
			y+=v.unsafeGet(1)*factor;
			z+=v.unsafeGet(2)*factor;
		}
	}
	
	public void addMultiple(Vector3 v, double factor) {
		x+=v.x*factor;
		y+=v.y*factor;
		z+=v.z*factor;
	}
	
	public void addProduct(Vector3 a, Vector3 b) {
		x+=a.x*b.x;
		y+=a.y*b.y;
		z+=a.z*b.z;
	}
	
	public void addProduct(Vector3 a, Vector3 b, double factor) {
		x+=a.x*b.x*factor;
		y+=a.y*b.y*factor;
		z+=a.z*b.z*factor;
	}
	
	public void subtractMultiple(Vector3 v, double factor) {
		x-=v.x*factor;
		y-=v.y*factor;
		z-=v.z*factor;
	}
	
	@Override
	public void add(AVector v) {
		if (v instanceof Vector3) {
			add((Vector3)v);
		} else {
			v.checkLength(3);
			x+=v.unsafeGet(0);
			y+=v.unsafeGet(1);
			z+=v.unsafeGet(2);
		}
	}
	
	@Override
	public Vector3 addCopy(AVector v) {
		if (v instanceof Vector3) return addCopy((Vector3)v);
		v.checkLength(3);
		return new Vector3(x+v.unsafeGet(0),y+v.unsafeGet(1),z+v.unsafeGet(2));
	}
	
	public Vector3 addCopy(Vector3 v) {
		return new Vector3(x+v.x,y+v.y,z+v.z);
	}
	
	public void add(Vector3 v) {
		x+=v.x;
		y+=v.y;
		z+=v.z;
	}
	
	public void sub(Vector3 v) {
		x-=v.x;
		y-=v.y;
		z-=v.z;
	}
	
	public void subMultiple(Vector3 v, double factor) {
		addMultiple(v,-factor);
	}
	
	public double dotProduct(Vector3 a) {
		return (x*a.x) + (y*a.y) + (z*a.z);
	} 
	
	@Override
	public double dotProduct(AVector v) {
		return x*v.unsafeGet(0)+y*v.unsafeGet(1)+z*v.unsafeGet(2);
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		return x*data[offset+0]+y*data[offset+1]+z*data[offset+2];
	}
	
	@Override
	public void crossProduct(AVector a) {
		if (a instanceof Vector3) {
			crossProduct((Vector3) a);
			return;
		}
		double x2=a.unsafeGet(0);
		double y2=a.unsafeGet(1);
		double z2=a.unsafeGet(2);
		double tx=y*z2-z*y2;
		double ty=z*x2-x*z2;
		double tz=x*y2-y*x2;			
		x=tx;
		y=ty;
		z=tz;		
	}
	
	@Override
	public void crossProduct(Vector3 a) {
		double tx=y*a.z-z*a.y;
		double ty=z*a.x-x*a.z;
		double tz=x*a.y-y*a.x;			
		x=tx;
		y=ty;
		z=tz;
	}
	
	@Override
	public void projectToPlane(AVector normal, double distance) {
		if (normal instanceof Vector3) {projectToPlane((Vector3)normal,distance); return;}
		super.projectToPlane(normal, distance);
	}
	
	public void projectToPlane(Vector3 normal, double distance) {
		assert(Tools.epsilonEquals(normal.magnitude(), 1.0));
		double d=dotProduct(normal);
		addMultiple(normal,distance-d);
	}

	@Override
	public int length() {
		return 3;
	}
	
	@Override
	public double elementSum() {
		return x+y+z;
	}
	
	@Override
	public double elementProduct() {
		return x*y*z;
	}
	
	@Override
	public void scaleAdd(double factor, double constant) {
		x=(x*factor)+constant;
		y=(y*factor)+constant;
		z=(z*factor)+constant;
	}
	
	@Override
	public void scaleAdd(double factor, AVector constant) {
		if (constant instanceof Vector3) {scaleAdd(factor,(Vector3)constant); return; }
		x=(x*factor)+constant.unsafeGet(0);
		y=(y*factor)+constant.unsafeGet(1);
		z=(z*factor)+constant.unsafeGet(2);
	}
	
	public void scaleAdd(double factor, Vector3 constant) {
		x=(x*factor)+constant.x;
		y=(y*factor)+constant.y;
		z=(z*factor)+constant.z;
	}

	@Override
	public void add(double constant) {
		x=x+constant;
		y=y+constant;
		z=z+constant;
	}

	@Override
	public double get(int i) {
		switch (i) {
		case 0: return x;
		case 1: return y;
		case 2: return z;
		default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		}
	}
	
	@Override
	public double unsafeGet(int i) {
		switch (i) {
		case 0: return x;
		case 1: return y;
		default: return z;
		}
	}
	
	@Override 
	public void set(AVector v) {
		if (v.length()!=3) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));
		x=v.unsafeGet(0);
		y=v.unsafeGet(1);
		z=v.unsafeGet(2);
	}
	
	@Override 
	public void fill(double v) {
		x=v;
		y=v;
		z=v;
	}

	@Override
	public void set(int i, double value) {
		switch (i) {
		case 0: x=value; return;
		case 1: y=value; return;
		case 2: z=value; return;
		default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		}
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		switch (i) {
		case 0: x=value; return;
		case 1: y=value; return;
		default: z=value; return;
		}
	}
	
	@Override
	public void addAt(int i, double value) {
		switch (i) {
		case 0: x+=value; return;
		case 1: y+=value; return;
		default: z+=value; return;
		}
	}
	
	public void setValues(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	@Override
	public void negate() {
		x=-x;
		y=-y;
		z=-z;
	}
	
	@Override
	public Vector3 negateCopy() {
		return new Vector3(-x,-y,-z);
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		data[offset]=x;
		data[offset+1]=y;
		data[offset+2]=z;
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(x);
		dest.put(y);
		dest.put(z);
	}
	
	@Override
	public double[] toDoubleArray() {
		return new double[] {x,y,z};
	}
	
	@Override
	public Vector3 toNormal() {
		double d=this.magnitude();
		return (d==0)?new Vector3():new Vector3(x/d,y/d,z/d);
	}
	
	@Override
	public Vector3 clone() {
		return new Vector3(x,y,z);	
	}
	
	@Override
	public Vector3 copy() {
		return clone();	
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
	public Vector3 exactClone() {
		return clone();
	}
	
	@Override 
	public boolean equals(AVector v) {
		if (v==this) return true;
		if (v instanceof Vector3) {
			return equals((Vector3)v);
		}
		return (v.length()==3)&&(x==v.unsafeGet(0))&&(y==v.unsafeGet(1))&&(z==v.unsafeGet(2));
	}
	
	public boolean equals(Vector3 v) {
		return (x==v.x)&&(y==v.y)&&(z==v.z);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return (x==data[offset])&&(y==data[offset+1])&&(z==data[offset+2]);
	}
}
