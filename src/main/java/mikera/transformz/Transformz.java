package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix33;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.transformz.impl.IdentityTranslation;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector3;

public class Transformz {
	public static DiagonalMatrix scale3D(double factor) {
		return Matrixx.createScaleMatrix(3, factor);
	}
	
	public static IdentityTranslation identityTransform(int dimensions) {
		return IdentityTranslation.getInstance(dimensions);
	}
	
	public static ATranslation createTranslation(AVector v) {
		if (v.length()==3) {
			return new Translation3(v);
		}
		return new Translation(v);
	}
	
	public static Translation3 createTranslation(Vector3 v) {
		return new Translation3(v);
	}
	
	public static Affine34 createAffineTransform(Matrix33 m, AVector v) {
		assert(v.length()==3);
		return new Affine34(m,v);
	}
	
	public static Affine34 createAffineTransform(Matrix33 m, Vector3 v) {
		return new Affine34(m,v);
	}

	public static ATranslation createMutableTranslation(ATranslation t) {
		if (t.dimensions()==3) {
			return new Translation3(t);
		} else {
			return new Translation(t);
		}
	}

	public static AAffineTransform createAffineTransform(AMatrix m, AVector v) {
		if (m instanceof Matrix33) {
			return createAffineTransform((Matrix33)m,v);
		}
		return new AffineMN(m,v);
	}
}
