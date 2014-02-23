package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix22;
import mikera.transformz.marker.ISpecialisedTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector2;

/**
 * Specialised 2x3 affine transformation class
 * 
 * Intended for composing 2d affine transformations.
 * 
 * @author Mike
 */
public final class Affine23 extends AAffineTransform  implements ISpecialisedTransform {
	public double m00,m01,tr0,
                  m10,m11,tr1;
	
	public Affine23() {	
	}
	
	public Affine23(double m00, double m01, double tr0, 
			        double m10, double m11, double tr1) {
		this.m00=m00;
		this.m01=m01;
		this.m10=m10;
		this.m11=m11;
		
		this.tr0=tr0;
		this.tr1=tr1;
	}
	
	public Affine23(AMatrix matrix, ATranslation trans) {
		this(matrix,trans.getTranslationVector());
	}
	
	public Affine23(AMatrix m, AVector v) {
		if ((v.length()!=2)||(m.columnCount()!=2)||(m.rowCount()!=2)) {
			throw new IllegalArgumentException("Wrong source sizes for Affine23");
		}
		m00=m.unsafeGet(0,0);
		m01=m.unsafeGet(0,1);
		m10=m.unsafeGet(1,0);
		m11=m.unsafeGet(1,1);
		tr0=v.unsafeGet(0);
		tr1=v.unsafeGet(1);
	}
	
	public Affine23(Matrix22 m, AVector v) {
		assert(v.length()==2);
		assert(m.columnCount()==2);
		assert(m.rowCount()==2);
		m00=m.m00;
		m01=m.m01;
		m10=m.m10;
		m11=m.m11;
		tr0=v.unsafeGet(0);
		tr1=v.unsafeGet(1);
	}

	@Override
	public AMatrix getMatrix() {
		return copyOfMatrix();
	}

	@Override
	public ATranslation getTranslation() {
		return Transformz.createTranslation(copyOfTranslationVector());
	}
	
	@Override
	public Matrix22 copyOfMatrix() {
		return new Matrix22(m00,m01,m10,m11);
	}
	
	@Override
	public Vector2 copyOfTranslationVector() {
		return Vector2.of(tr0,tr1);
	}

	@Override
	public void transform(AVector source, AVector dest) {
		if ((source instanceof Vector2)&&(dest instanceof Vector2)) {
			transform((Vector2)source,(Vector2)dest);
			return;
		}
		double x=source.unsafeGet(0), y=source.unsafeGet(1);
		dest.set(0,((m00*x)+(m01*y)+tr0));
		dest.set(1,((m10*x)+(m11*y)+tr1));
	}
	
	@Override
	public void transformNormal(AVector source, AVector dest) {
		if ((source instanceof Vector2)&&(dest instanceof Vector2)) {
			transformNormal((Vector2)source,(Vector2)dest);
			return;
		}		
		transform(source, dest);
		dest.normalise();
	}
	
	public void transformNormal(Vector2 source, Vector2 dest) {
		double x=source.x, y=source.y;
		dest.set(0,((m00*x)+(m01*y)));
		dest.set(1,((m10*x)+(m11*y)));
		dest.normalise();
	}
	
	@Override
	public void transformInPlace(AVector dest) {
		if (dest instanceof Vector2) {
			transformInPlace((Vector2)dest);
			return;
		}
		double x=dest.unsafeGet(0), y=dest.get(1); // only last get needs to be safe
		dest.set(0,((m00*x)+(m01*y)+tr0));
		dest.set(1,((m10*x)+(m11*y)+tr1));
	}
	
	public void transform(Vector2 source, Vector2 dest) {
		Vector2 s=source;
		dest.x=((m00*s.x)+(m01*s.y)+tr0);
		dest.y=((m10*s.x)+(m11*s.y)+tr1);
	}
	
	public void transformInPlace(Vector2 dest) {
		Vector2 s=dest;
		double tx=((m00*s.x)+(m01*s.y)+tr0);
		double ty=((m10*s.x)+(m11*s.y)+tr1);
		s.x=tx; s.y=ty;
	}

	@Override
	public int inputDimensions() {
		return 2;
	}

	@Override
	public int outputDimensions() {
		return 2;
	}
	
	@Override
	public void composeWith(ATransform a) {
		if (a instanceof Affine23) {
			composeWith((Affine23)a);
			return;
		} 
		super.composeWith(a);
	}
	
	public void composeWith(Affine23 a) {
		double t00=(m00*a.m00)+(m01*a.m10);
		double t01=(m00*a.m01)+(m01*a.m11);
		double t10=(m10*a.m00)+(m11*a.m10);
		double t11=(m10*a.m01)+(m11*a.m11);

		double t02=(m00*a.tr0)+(m01*a.tr1)+tr0;
		double t12=(m10*a.tr0)+(m11*a.tr1)+tr1;
		m00=t00; m01=t01; tr0=t02;
		m10=t10; m11=t11; tr1=t12;
	}
	
	public void composeWith(Matrix22 a) {
		double t00=(m00*a.m00)+(m01*a.m10);
		double t01=(m00*a.m01)+(m01*a.m11);
		double t10=(m10*a.m00)+(m11*a.m10);
		double t11=(m10*a.m01)+(m11*a.m11);
		
		m00=t00; m01=t01; 
		m10=t10; m11=t11; 
	}
		
	@Override
	public boolean equals(Object o) {
		if (o instanceof Affine23) {
			return equals((Affine23)o);
		}
		return super.equals(o);
	}
	
	public boolean equals(Affine23 m) {
		return
			(m00==m.m00) &&
			(m01==m.m01) &&
			(tr0==m.tr0) &&
			(m10==m.m10) &&
			(m11==m.m11) &&
			(tr1==m.tr1);
	}
}
