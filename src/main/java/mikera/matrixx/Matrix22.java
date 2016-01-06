package mikera.matrixx;

import mikera.matrixx.impl.APrimitiveMatrix;
import mikera.transformz.marker.ISpecialisedTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector2;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised 2*2 Matrix for Vector2 maths, using primitive matrix elements
 * 
 * @author Mike
 *
 */
public final class Matrix22 extends APrimitiveMatrix implements ISpecialisedTransform {
	private static final long serialVersionUID = 2696617102233017028L;

	public double m00,m01,
	              m10,m11;
	
	public Matrix22() {
	}
	
	public Matrix22(Matrix22 source) {
		Matrix22 s=source;
		m00=s.m00; m01=s.m01; 
		m10=s.m10; m11=s.m11; 
	}
	
	public Matrix22(double m00, double m01, double m10, double m11) {
		this.m00=m00;
		this.m01=m01;
		this.m10=m10;
		this.m11=m11;
	}

	public Matrix22(AMatrix m) {
		if (m instanceof Matrix22) {
			set((Matrix22) m);
		} else {
			unsafeSet(m);
		}
	}
	
	public void set(Matrix22 a) {
		m00=a.m00; m01=a.m01; 
		m10=a.m10; m11=a.m11; 
	}
	
	@Override
	public void set(AMatrix m) {
		m.checkShape(2, 2);
		m00=m.unsafeGet(0,0);
		m01=m.unsafeGet(0,1);
		m10=m.unsafeGet(1,0);
		m11=m.unsafeGet(1,1);		
	}
	
	public void unsafeSet(AMatrix m) {
		m00=m.unsafeGet(0,0);
		m01=m.unsafeGet(0,1);
		m10=m.unsafeGet(1,0);
		m11=m.unsafeGet(1,1);		
	}
	
	public static Matrix22 create(double a, double b, double c, double d) {
		return new Matrix22(a,b,c,d);
	}
	
	public static Matrix22 createRotationMatrix(double angle) {
		double sa=Math.sin(angle);
		double ca=Math.cos(angle);
		return new Matrix22(
				ca,-sa,
				sa,ca);
	}
	
	public static Matrix22 createScaleMatrix(double d) {
		return new Matrix22(d,0,0,d);
	}
	
	/**
	 * Creates a new mutable 2D identity matrix
	 * @return
	 */
	public static Matrix22 createIdentity() {
		return new Matrix22(1,0,0,1);
	}
	
	public static Matrix22 createReflectionMatrix(AVector normal) {
		return createReflectionMatrix(Vector2.create(normal));
	}
	
	public static Matrix22 createReflectionMatrix(Vector2 normal) {
		double x=normal.x, y=normal.y;
		double ca=x*x-y*y;
		double sa=2*x*y;
		return new Matrix22(ca, sa, sa, -ca);
	}
	
	@Override
	public void multiply(double factor) {
		m00*=factor; m01*=factor;
		m10*=factor; m11*=factor;
	}

	@Override
	public double determinant() {
		return (m00*m11)-(m01*m10);
	}
	
	@Override
	public long elementCount() {
		return 4;
	}
	
	@Override
	public double elementSum() {
		return m00+m01+m10+m11;
	}
	
	@Override
	public double elementMin() {
		return Math.min(Math.min(m00, m01), Math.min(m10, m11));
	}
	
	@Override
	public double elementMax() {
		return Math.max(Math.max(m00, m01), Math.max(m10, m11));
	}
	
	@Override
	public double trace() {
		return m00+m11;
	}
	
	@Override
	public Matrix22 inverse() {
		double det=determinant();
		if (det==0.0) return null;
		double invDet=1.0/det;
		return new Matrix22( invDet*m11, -invDet*m01,
				            -invDet*m10,  invDet*m00);		
	}

	@Override
	public int rowCount() {
		return 2;
	}

	@Override
	public int columnCount() {
		return 2;
	}
	
	@Override
	public int checkSquare() {
		return 2;
	}

	@Override
	public void add(AMatrix a) {
		if (a instanceof Matrix22) {
			add((Matrix22)a); return;
		}
		a.checkShape(2, 2);
		m00+=a.unsafeGet(0, 0);
		m01+=a.unsafeGet(0, 1);
		m10+=a.unsafeGet(1, 0);
		m11+=a.unsafeGet(1, 1);
	}
	
	public void add(Matrix22 a) {
		m00+=a.m00;
		m01+=a.m01;
		m10+=a.m10;
		m11+=a.m11;		
	}
	
	public void sub(Matrix22 a) {
		m00-=a.m00;
		m01-=a.m01;
		m10-=a.m10;
		m11-=a.m11;		
	}
	
	@Override
	public void applyOp(Op op) {
		m00=op.apply(m00);
		m01=op.apply(m01);
		m10=op.apply(m10);
		m11=op.apply(m11);
	}
	
	@Override
	public Vector2 getRowClone(int row) {
		switch (row) {
			case 0: return Vector2.of(m00,m01);
			case 1: return Vector2.of(m10,m11);
			default: throw new IndexOutOfBoundsException("Row index = "+row);
		}
	}
	
	@Override
	public Vector2 getColumnClone(int column) {
		switch (column) {
			case 0: return Vector2.of(m00,m10);
			case 1: return Vector2.of(m01,m11);
			default: throw new IndexOutOfBoundsException("Column index = "+column);
		}
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		if (row==0) {
			dest[destOffset++]=m00;
			dest[destOffset++]=m01;
		} else {
			dest[destOffset++]=m10;
			dest[destOffset++]=m11;
		}
	}

	@Override
	public double get(int row, int column) {
		switch (row) {
		case 0:
			switch (column) {
			case 0: return m00;
			case 1: return m01;
			default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
			}
		case 1:
			switch (column) {
			case 0: return m10;
			case 1: return m11;
			default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
			}
		default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		}
	}

	@Override
	public void set(int row, int column, double value) {
		switch (row) {
		case 0:
			switch (column) {
			case 0: m00=value; return;
			case 1: m01=value; return;
			default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
			}
		case 1:
			switch (column) {
			case 0: m10=value; return;
			case 1: m11=value; return;
			default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
			}
		default: throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		}	
	}
	
	@Override
	public AMatrix innerProduct(AMatrix a) {
		if (a instanceof Matrix22) {
			return innerProduct((Matrix22)a);
		}
		return super.innerProduct(a);
	}
	
	@Override
	public AVector innerProduct(AVector a) {
		if (a instanceof Vector2) {
			return innerProduct((Vector2)a);
		}
		return super.innerProduct(a);
	}
	
	public Vector2 innerProduct(Vector2 a) {
		return transform(a);
	}
	
	public Matrix22 innerProduct(Matrix22 a) {
		Matrix22 r=new Matrix22();
		r.m00=m00*a.m00+m01*a.m10;
		r.m01=m00*a.m01+m01*a.m11;
		r.m10=m10*a.m00+m11*a.m10;
		r.m11=m10*a.m01+m11*a.m11;
		return r;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		if (source instanceof Vector2) {transform((Vector2)source,dest); return;}
		super.transform(source,dest);
	}
	
	public void transform(Vector2 source, AVector dest) {
		if (dest instanceof Vector2) {transform(source,(Vector2)dest); return;}
		Vector2 s=source;
		dest.set(0,(m00*s.x)+(m01*s.y));
		dest.set(1,(m10*s.x)+(m11*s.y));
	}
	
	public void transform(Vector2 source, Vector2 dest) {
		Vector2 s=source;
		dest.x=((m00*s.x)+(m01*s.y));
		dest.y=((m10*s.x)+(m11*s.y));
	}
	
	public Vector2 transform(Vector2 source) {
		Vector2 s=source;
		Vector2 result=new Vector2(
				((m00*s.x)+(m01*s.y)),
				((m10*s.x)+(m11*s.y)));
		return result;
	}
	
	public void transformInPlace(Vector2 dest) {
		Vector2 s=dest;
		double tx=((m00*s.x)+(m01*s.y));
		double ty=((m10*s.x)+(m11*s.y));
		s.x=tx; s.y=ty;
	}
		
	@Override
	public boolean isSymmetric() {
		return m01==m10;
	}
	
	@Override
	public Vector toVector() {
		return Vector.of(m00,m01,m10,m11);
	}

	@Override
	public Matrix22 getTranspose() {
		return new Matrix22(m00,m10,
				            m01,m11);
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		data[offset++]=m00;
		data[offset++]=m01;
		data[offset++]=m10;
		data[offset++]=m11;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Matrix22) {
			return equals((Matrix22)o);
		}
		return super.equals(o);
	}
	
	public boolean equals(Matrix22 m) {
		return
			(m00==m.m00) &&
			(m01==m.m01) &&
			(m10==m.m10) &&
			(m11==m.m11);
	}
	
	@Override
	public Matrix22 clone() {
		return new Matrix22(this);
	}
	
	@Override
	public Matrix22 exactClone() {
		return new Matrix22(this);
	}
	
	@Override
	public double[] toDoubleArray() {
		return new double[] {m00,m01,m10,m11};
	}

	@Override
	public boolean isZero() {
		return (m00==0.0)&&(m01==0.0)&&(m10==0.0)&&(m11==0.0);
	}

}
