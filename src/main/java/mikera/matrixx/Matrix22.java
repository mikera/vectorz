package mikera.matrixx;

import mikera.transformz.marker.ISpecialisedTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector2;
import mikera.vectorz.Vector4;

/**
 * Specialised 2*2 Matrix for Vector2 maths, using primitive matrix elements
 * 
 * @author Mike
 *
 */
public final class Matrix22 extends AMatrix implements ISpecialisedTransform {
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
		assert(m.rowCount()==2);
		assert(m.columnCount()==2);
		m00=m.get(0,0);
		m01=m.get(0,1);
		m10=m.get(1,0);
		m11=m.get(1,1);
	}
	
	@Override
	public void scale(double factor) {
		m00*=factor; m01*=factor;
		m10*=factor; m11*=factor;
	}

	@Override
	public double determinant() {
		return (m00*m11)-(m01*m10);
	}
	
	@Override
	public Matrix22 inverse() {
		double det=determinant();
		if (det==0.0) throw new IllegalArgumentException("Matrix has zero determinant: not invertible");
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
	public Vector2 cloneRow(int row) {
		switch (row) {
			case 0: return Vector2.of(m00,m01);
			case 1: return Vector2.of(m10,m11);
			default: throw new IndexOutOfBoundsException("Row index = "+row);
		}
	}

	@Override
	public double get(int row, int column) {
		switch (row) {
		case 0:
			switch (column) {
			case 0: return m00;
			case 1: return m01;
			default: throw new IndexOutOfBoundsException("Column: "+row);
			}
		case 1:
			switch (column) {
			case 0: return m10;
			case 1: return m11;
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
			default: throw new IndexOutOfBoundsException("Column: "+row);
			}
		case 1:
			switch (column) {
			case 0: m10=value; return;
			case 1: m11=value; return;
			default: throw new IndexOutOfBoundsException("Column: "+row);
			}
		default: throw new IndexOutOfBoundsException("Row: "+row);
		}	
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
	public boolean isSquare() {
		return true;
	}
	
	@Override
	public Vector4 toVector() {
		return new Vector4(m00,m01,m10,m11);
	}

	@Override
	public Matrix22 getTranspose() {
		return new Matrix22(m00,m10,
				            m01,m11);
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
}
