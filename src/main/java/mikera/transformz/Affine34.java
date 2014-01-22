package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix33;
import mikera.transformz.marker.ISpecialisedTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector3;

/**
 * Specialised 3*4 affine transformation class
 * 
 * Intended for composing 3d affine transformations.
 * 
 * @author Mike
 */
public final class Affine34 extends AAffineTransform  implements ISpecialisedTransform {
	public double m00,m01,m02,tr0,
                  m10,m11,m12,tr1,
                  m20,m21,m22,tr2;
	
	public Affine34() {	
	}
	
	public Affine34(double m00, double m01, double m02, double tr0, 
			        double m10, double m11, double m12, double tr1,
			        double m20, double m21, double m22, double tr2) {
		this.m00=m00;
		this.m01=m01;
		this.m02=m02;
		this.m10=m10;
		this.m11=m11;
		this.m12=m12;
		this.m20=m20;
		this.m21=m21;
		this.m22=m22;
		
		this.tr0=tr0;
		this.tr1=tr1;
		this.tr2=tr2;
	}
	
	public Affine34(AMatrix matrix, ATranslation trans) {
		this(matrix,trans.getTranslationVector());
	}
	
	public Affine34(AMatrix m, AVector v) {
		if ((v.length()!=3)||(m.columnCount()!=3)||(m.rowCount()!=3)) {
			throw new IllegalArgumentException("Wrong source sizes for Affine34");
		}
		m00=m.unsafeGet(0,0);
		m01=m.unsafeGet(0,1);
		m02=m.unsafeGet(0,2);
		m10=m.unsafeGet(1,0);
		m11=m.unsafeGet(1,1);
		m12=m.unsafeGet(1,2);
		m20=m.unsafeGet(2,0);
		m21=m.unsafeGet(2,1);
		m22=m.unsafeGet(2,2);
		
		tr0=v.unsafeGet(0);
		tr1=v.unsafeGet(1);
		tr2=v.unsafeGet(2);
	}
	
	public Affine34(Matrix33 m, AVector v) {
		assert(v.length()==3);
		assert(m.columnCount()==3);
		assert(m.rowCount()==3);
		m00=m.m00;
		m01=m.m01;
		m02=m.m02;
		m10=m.m10;
		m11=m.m11;
		m12=m.m12;
		m20=m.m20;
		m21=m.m21;
		m22=m.m22;
		
		tr0=v.get(0);
		tr1=v.get(1);
		tr2=v.get(2);
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
	public Matrix33 copyOfMatrix() {
		return new Matrix33(m00,m01,m02,m10,m11,m12,m20,m21,m22);
	}
	
	@Override
	public Vector3 copyOfTranslationVector() {
		return Vector3.of(tr0,tr1,tr2);
	}

	@Override
	public void transform(AVector source, AVector dest) {
		if ((source instanceof Vector3)&&(dest instanceof Vector3)) {
			transform((Vector3)source,(Vector3)dest);
			return;
		}
		double x=source.unsafeGet(0), y=source.unsafeGet(1), z=source.get(2);
		dest.set(0,((m00*x)+(m01*y)+(m02*z)+tr0));
		dest.set(1,((m10*x)+(m11*y)+(m12*z)+tr1));
		dest.set(2,((m20*x)+(m21*y)+(m22*z)+tr2));
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
		double x=source.x, y=source.y, z=source.z;
		dest.set(0,((m00*x)+(m01*y)+(m02*z)));
		dest.set(1,((m10*x)+(m11*y)+(m12*z)));
		dest.set(2,((m20*x)+(m21*y)+(m22*z)));
		dest.normalise();
	}
	
	@Override
	public void transformInPlace(AVector dest) {
		if (dest instanceof Vector3) {
			transformInPlace((Vector3)dest);
			return;
		}
		double x=dest.unsafeGet(0), y=dest.unsafeGet(1), z=dest.get(2); // only last get needs to be safe
		dest.set(0,((m00*x)+(m01*y)+(m02*z)+tr0));
		dest.set(1,((m10*x)+(m11*y)+(m12*z)+tr1));
		dest.set(2,((m20*x)+(m21*y)+(m22*z)+tr2));
	}
	
	public void transform(Vector3 source, Vector3 dest) {
		Vector3 s=source;
		dest.x=((m00*s.x)+(m01*s.y)+(m02*s.z)+tr0);
		dest.y=((m10*s.x)+(m11*s.y)+(m12*s.z)+tr1);
		dest.z=((m20*s.x)+(m21*s.y)+(m22*s.z)+tr2);
	}
	
	public void transformInPlace(Vector3 dest) {
		Vector3 s=dest;
		double tx=((m00*s.x)+(m01*s.y)+(m02*s.z)+tr0);
		double ty=((m10*s.x)+(m11*s.y)+(m12*s.z)+tr1);
		double tz=((m20*s.x)+(m21*s.y)+(m22*s.z)+tr2);
		s.x=tx; s.y=ty; s.z=tz;
	}

	@Override
	public int inputDimensions() {
		return 3;
	}

	@Override
	public int outputDimensions() {
		return 3;
	}
	
	@Override
	public void composeWith(ATransform a) {
		if (a instanceof Affine34) {
			composeWith((Affine34)a);
			return;
		} else if (a instanceof Translation3) {
			composeWith((Translation3)a);
			return;
		}
		super.composeWith(a);
	}
	
	public void composeWith(Affine34 a) {
		double t00=(m00*a.m00)+(m01*a.m10)+(m02*a.m20);
		double t01=(m00*a.m01)+(m01*a.m11)+(m02*a.m21);
		double t02=(m00*a.m02)+(m01*a.m12)+(m02*a.m22);
		double t10=(m10*a.m00)+(m11*a.m10)+(m12*a.m20);
		double t11=(m10*a.m01)+(m11*a.m11)+(m12*a.m21);
		double t12=(m10*a.m02)+(m11*a.m12)+(m12*a.m22);
		double t20=(m20*a.m00)+(m21*a.m10)+(m22*a.m20);
		double t21=(m20*a.m01)+(m21*a.m11)+(m22*a.m21);
		double t22=(m20*a.m02)+(m21*a.m12)+(m22*a.m22);
		
		double t03=(m00*a.tr0)+(m01*a.tr1)+(m02*a.tr2)+tr0;
		double t13=(m10*a.tr0)+(m11*a.tr1)+(m12*a.tr2)+tr1;
		double t23=(m20*a.tr0)+(m21*a.tr1)+(m22*a.tr2)+tr2;
		m00=t00; m01=t01; m02=t02; tr0=t03;
		m10=t10; m11=t11; m12=t12; tr1=t13;
		m20=t20; m21=t21; m22=t22; tr2=t23;
	}
	
	public void composeWith(Matrix33 a) {
		double t00=(m00*a.m00)+(m01*a.m10)+(m02*a.m20);
		double t01=(m00*a.m01)+(m01*a.m11)+(m02*a.m21);
		double t02=(m00*a.m02)+(m01*a.m12)+(m02*a.m22);
		double t10=(m10*a.m00)+(m11*a.m10)+(m12*a.m20);
		double t11=(m10*a.m01)+(m11*a.m11)+(m12*a.m21);
		double t12=(m10*a.m02)+(m11*a.m12)+(m12*a.m22);
		double t20=(m20*a.m00)+(m21*a.m10)+(m22*a.m20);
		double t21=(m20*a.m01)+(m21*a.m11)+(m22*a.m21);
		double t22=(m20*a.m02)+(m21*a.m12)+(m22*a.m22);
		
		m00=t00; m01=t01; m02=t02; 
		m10=t10; m11=t11; m12=t12; 
		m20=t20; m21=t21; m22=t22; 
	}
	
	public void composeWith(Translation3 a) {
		tr0+=a.dx;
		tr1+=a.dy;
		tr2+=a.dz;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Affine34) {
			return equals((Affine34)o);
		}
		return super.equals(o);
	}
	
	public boolean equals(Affine34 m) {
		return
			(m00==m.m00) &&
			(m01==m.m01) &&
			(m02==m.m02) &&
			(m10==m.m10) &&
			(m11==m.m11) &&
			(m12==m.m12) &&
			(m20==m.m20) &&
			(m21==m.m21) &&
			(m22==m.m22) &&
			(tr0==m.tr0) &&
			(tr1==m.tr1) &&
			(tr2==m.tr2);
	}
}
