package mikera.matrixx;

import mikera.transformz.Affine34;
import mikera.transformz.marker.ISpecialisedTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector3;

/**
 * Specialised 3*3 Matrix for Vector3 maths, using primitive matrix elements
 * 
 * @author Mike
 *
 */
public final class Matrix33 extends AMatrix implements ISpecialisedTransform {
	public double m00,m01,m02,
	              m10,m11,m12,
	              m20,m21,m22;
	
	/**
	 * Create a new (zero-initialised) 3x3 Matrix
	 */
	public Matrix33() {
	}
	
	public Matrix33(Matrix33 source) {
		Matrix33 s=source;
		m00=s.m00; m01=s.m01; m02=s.m02;
		m10=s.m10; m11=s.m11; m12=s.m12;
		m20=s.m20; m21=s.m21; m22=s.m22;
	}
	
	public Matrix33(double m00, double m01, double m02, double m10,
			double m11, double m12, double m20, double m21, double m22) {
		this.m00=m00;
		this.m01=m01;
		this.m02=m02;
		this.m10=m10;
		this.m11=m11;
		this.m12=m12;
		this.m20=m20;
		this.m21=m21;
		this.m22=m22;
	}

	public Matrix33(AMatrix m) {
		assert(m.rowCount()==3);
		assert(m.columnCount()==3);
		m00=m.get(0,0);
		m01=m.get(0,1);
		m02=m.get(0,2);
		m10=m.get(1,0);
		m11=m.get(1,1);
		m12=m.get(1,2);
		m20=m.get(2,0);
		m21=m.get(2,1);
		m22=m.get(2,2);
	}

	@Override
	public double determinant() {
		return (m00*m11*m22)+(m01*m12*m20)+(m02*m10*m21)
		      -(m00*m12*m21)-(m01*m10*m22)-(m02*m11*m20);
	}
	
	@Override
	public void scale(double factor) {
		m00*=factor; m01*=factor; m02*=factor;
		m10*=factor; m11*=factor; m12*=factor;
		m20*=factor; m21*=factor; m22*=factor;
	}

	@Override
	public int rowCount() {
		return 3;
	}

	@Override
	public int columnCount() {
		return 3;
	}

	@Override
	public double get(int row, int column) {
		switch (row) {
		case 0:
			switch (column) {
			case 0: return m00;
			case 1: return m01;
			case 2: return m02;
			default: throw new IndexOutOfBoundsException("Column: "+row);
			}
		case 1:
			switch (column) {
			case 0: return m10;
			case 1: return m11;
			case 2: return m12;
			default: throw new IndexOutOfBoundsException("Column: "+row);
			}
		case 2:
			switch (column) {
			case 0: return m20;
			case 1: return m21;
			case 2: return m22;
			default: throw new IndexOutOfBoundsException("Column: "+row);
			}

		default: throw new IndexOutOfBoundsException("Row: "+row);
		}
	}

	@Override
	public void set(int row, int column, double value) {
		switch (row) {
		case 0:
			switch (column) {
			case 0: m00=value; return;
			case 1: m01=value; return;
			case 2: m02=value; return;
			default: throw new IndexOutOfBoundsException("Column: "+row);
			}
		case 1:
			switch (column) {
			case 0: m10=value; return;
			case 1: m11=value; return;
			case 2: m12=value; return;
			default: throw new IndexOutOfBoundsException("Column: "+row);
			}
		case 2:
			switch (column) {
			case 0: m20=value; return;
			case 1: m21=value; return;
			case 2: m22=value; return;
			default: throw new IndexOutOfBoundsException("Column: "+row);
			}

		default: throw new IndexOutOfBoundsException("Row: "+row);
		}	
	}
	
	@Override
	public AMatrix compose(AMatrix a) {
		if (a instanceof Matrix33) {
			return compose((Matrix33)a);
		}
		return super.compose(a);
	}
	
	public Matrix33 compose(Matrix33 a) {
		Matrix33 r=new Matrix33();
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				double acc=0.0;
				for (int k=0; k<3; k++) {
					acc+=this.get(i, k)*a.get(k, j);
				}
				r.set(i,j,acc);
			}
		}
		return r;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		if (source instanceof Vector3) {transform((Vector3)source,dest); return;}
		super.transform(source,dest);
	}
	
	public void transform(Vector3 source, AVector dest) {
		if (dest instanceof Vector3) {transform(source,(Vector3)dest); return;}
		Vector3 s=source;
		dest.set(0,(m00*s.x)+(m01*s.y)+(m02*s.z));
		dest.set(1,(m10*s.x)+(m11*s.y)+(m12*s.z));
		dest.set(2,(m20*s.x)+(m21*s.y)+(m22*s.z));
	}
	
	public void transform(Vector3 source, Vector3 dest) {
		double x=source.x, y=source.y, z=source.z;
		dest.x=((m00*x)+(m01*y)+(m02*z));
		dest.y=((m10*x)+(m11*y)+(m12*z));
		dest.z=((m20*x)+(m21*y)+(m22*z));
	}
	
	@Override
	public void transformNormal(AVector source, AVector dest) {
		if ((source instanceof Vector3)&&(dest instanceof Vector3)) {
			transformNormal((Vector3)source,(Vector3)dest);
			return;
		}		
		transform(source, dest);
		dest.normalise();
	}
	
	public void transformNormal(Vector3 source, Vector3 dest) {
		transform(source,dest);
		dest.normalise();
	}
	
	public Vector3 transform(Vector3 source) {
		Vector3 s=source;
		Vector3 result=new Vector3(
				((m00*s.x)+(m01*s.y)+(m02*s.z)),
				((m10*s.x)+(m11*s.y)+(m12*s.z)),
				((m20*s.x)+(m21*s.y)+(m22*s.z))
				);
		return result;
	}
	
	@Override
	public void transformInPlace(AVector dest) {
		if (dest instanceof Vector3) {
			transformInPlace((Vector3)dest);
			return;
		}
		double sx=dest.get(0), sy=dest.get(1), sz=dest.get(2);
		double tx=((m00*sx)+(m01*sy)+(m02*sz));
		double ty=((m10*sx)+(m11*sy)+(m12*sz));
		double tz=((m20*sx)+(m21*sy)+(m22*sz));
		dest.set(0,tx);
		dest.set(1,ty);
		dest.set(2,tz);
	}
	
	public void transformInPlace(Vector3 dest) {
		Vector3 s=dest;
		double tx=((m00*s.x)+(m01*s.y)+(m02*s.z));
		double ty=((m10*s.x)+(m11*s.y)+(m12*s.z));
		double tz=((m20*s.x)+(m21*s.y)+(m22*s.z));
		s.x=tx; s.y=ty; s.z=tz;
	}
	
	@Override
	public boolean isSquare() {
		return true;
	}

	@Override
	public Affine34 toAffineTransform() {
		return new Affine34(m00,m01,m02,0.0,
				            m10,m11,m12,0.0,
				            m20,m21,m22,0.0);
	}
	
	@Override
	public Matrix33 getTranspose() {
		return new Matrix33(m00,m10,m20,
				            m01,m11,m21,
				            m02,m12,m22);
	}
	
	/**
	 * Returns a row of the matrix as a cloned vector
	 */
	@Override
	public Vector3 cloneRow(int row) {
		switch (row) {
			case 0: return Vector3.of(m00,m01,m02);
			case 1: return Vector3.of(m10,m11,m12);
			case 2: return Vector3.of(m20,m21,m22);
			default: throw new IndexOutOfBoundsException("Row index = "+row);
		}
	}
	
	@Override
	public Matrix33 inverse() {
		double det=determinant();
		if (det==0.0) throw new IllegalArgumentException("Matrix has zero determinant: not invertible");
		double invDet=1.0/det;
		return new Matrix33(
				invDet*((m11*m22-m12*m21)),
				invDet*((m02*m21-m01*m22)),
				invDet*((m01*m12-m02*m11)),
				invDet*((m12*m20-m10*m22)),
				invDet*((m00*m22-m02*m20)),
				invDet*((m02*m10-m00*m12)),
				invDet*((m10*m21-m11*m20)),
				invDet*((m01*m20-m00*m21)),
				invDet*((m00*m11-m01*m10)));		
	}
	
	@Override
	public Matrix33 clone() {
		return new Matrix33(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Matrix33) {
			return equals((Matrix33)o);
		}
		return super.equals(o);
	}
	
	public boolean equals(Matrix33 m) {
		return
			(m00==m.m00) &&
			(m01==m.m01) &&
			(m02==m.m02) &&
			(m10==m.m10) &&
			(m11==m.m11) &&
			(m12==m.m12) &&
			(m20==m.m20) &&
			(m21==m.m21) &&
			(m22==m.m22);
	}

	public static Matrix33 createIdentityMatrix() {
		return new Matrix33(
				1.0,0.0,0.0,
				0.0,1.0,0.0,
				0.0,0.0,1.0);
	}
}
