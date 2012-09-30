package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix33;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector3;

public final class Affine34 extends AAffineTransform {
	public double m00,m01,m02,m03,
                  m10,m11,m12,m13,
                  m20,m21,m22,m23;
	
	public Affine34() {	
	}
	
	public Affine34(double m00, double m01, double m02, double m03, 
			        double m10, double m11, double m12, double m13,
			        double m20, double m21, double m22, double m23) {
		this.m00=m00;
		this.m01=m01;
		this.m02=m02;
		this.m03=m03;
		this.m10=m10;
		this.m11=m11;
		this.m12=m12;
		this.m13=m13;
		this.m20=m20;
		this.m21=m21;
		this.m22=m22;
		this.m23=m23;
	}
	
	public Affine34(AMatrix matrix, ATranslation trans) {
		this(matrix,trans.getTranslationVector());
	}
	
	public Affine34(AMatrix m, AVector v) {
		assert(v.length()==3);
		assert(m.inputDimensions()==3);
		assert(m.outputDimensions()==3);
		m00=m.get(0,0);
		m01=m.get(0,1);
		m02=m.get(0,2);
		m10=m.get(1,0);
		m11=m.get(1,1);
		m12=m.get(1,2);
		m20=m.get(2,0);
		m21=m.get(2,1);
		m22=m.get(2,2);
		m03=v.get(0);
		m13=v.get(1);
		m23=v.get(2);
	}

	@Override
	public AMatrix getMatrixComponent() {
		return new Matrix33(m00,m01,m02,m10,m11,m12,m20,m21,m22);
	}

	@Override
	public ATranslation getTranslationComponent() {
		return Transformz.createTranslation(Vector3.of(m03,m13,m23));
	}

	@Override
	public void transform(AVector source, AVector dest) {
		if ((source instanceof Vector3)&&(dest instanceof Vector3)) {
			transform((Vector3)source,(Vector3)dest);
			return;
		}
		double x=source.get(0), y=source.get(1), z=source.get(2);
		dest.set(0,((m00*x)+(m01*y)+(m02*z)+m03));
		dest.set(1,((m10*x)+(m11*y)+(m12*z)+m13));
		dest.set(2,((m20*x)+(m21*y)+(m22*z)+m23));
	}
	
	@Override
	public void transformInPlace(AVector dest) {
		if (dest instanceof Vector3) {
			transformInPlace((Vector3)dest);
			return;
		}
		double x=dest.get(0), y=dest.get(1), z=dest.get(2);
		dest.set(0,((m00*x)+(m01*y)+(m02*z)+m03));
		dest.set(1,((m10*x)+(m11*y)+(m12*z)+m13));
		dest.set(2,((m20*x)+(m21*y)+(m22*z)+m23));
	}
	
	public void transform(Vector3 source, Vector3 dest) {
		Vector3 s=source;
		dest.x=((m00*s.x)+(m01*s.y)+(m02*s.z)+m03);
		dest.y=((m10*s.x)+(m11*s.y)+(m12*s.z)+m13);
		dest.z=((m20*s.x)+(m21*s.y)+(m22*s.z)+m23);
	}
	
	public void transformInPlace(Vector3 dest) {
		Vector3 s=dest;
		double tx=((m00*s.x)+(m01*s.y)+(m02*s.z)+m03);
		double ty=((m10*s.x)+(m11*s.y)+(m12*s.z)+m13);
		double tz=((m20*s.x)+(m21*s.y)+(m22*s.z)+m23);
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

}
