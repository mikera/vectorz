package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix22;
import mikera.matrixx.Matrix33;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.transformz.impl.ConstantTransform;
import mikera.transformz.impl.ConstantTransform3;
import mikera.transformz.impl.ConstantTransform4;
import mikera.transformz.impl.IdentityTranslation;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector3;

/**
 * Class containing static utility methods for vector transformations.
 * 
 * @author Mike
 *
 */
public class Transformz {
	/**
	 * Creates a 3D scaling transform
	 */
	public static Matrix33 scale3D(double factor) {
		return Matrix33.createScaleMatrix(factor);
	}

	/**
	 * Creates a 2D scaling transform
	 */
	public static Matrix22 scale2D(double factor) {
		return Matrix22.createScaleMatrix(factor);
	}
	
	/**
	 * Creates a 2D rotation transform
	 */
	public static Matrix22 rotate2D(double radians) {
		return Matrix22.createRotationMatrix(radians);
	}
	
	/**
	 * Creates an n-dimensional scaling transform
	 */
	public static AMatrix scale(int dimensions, double factor) {
		return Matrixx.createScaleMatrix(dimensions,factor);
	}

	
	/**
	 * Creates an identity translation with the given number of dimensions
	 */
	public static IdentityTranslation identityTranslation(int dimensions) {
		return IdentityTranslation.create(dimensions);
	}
	
	/**
	 * Creates an identity transform with the given number of dimensions
	 */
	public static ATransform identityTransform(int dimensions) {
		return IdentityTranslation.create(dimensions);
	}
	
	/**
	 * Coerce to a transform:
	 *  - vectors translate into constant transforms.
	 * @param o
	 * @return
	 */
	public ATransform toTransform(Object o) {
		if (o instanceof ATransform) {
			return (ATransform)o;
		} else if (o instanceof AVector) {
			AVector v=(AVector)o;
			return constantTransform(v.length(),v);
		}
		throw new UnsupportedOperationException("Don't know to to convert to transform: "+o.getClass());
	}
	
	/**
	 * Creates a zero transform (maps every vector to zero)
	 */
	public static AMatrix zeroTransform(int inputDimensions, int outputDimensions) {
		return ZeroMatrix.create(outputDimensions,inputDimensions);
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
			// fast path for 3D translation
			return new Translation3(t);
		}
		
		return new Translation(t);
	}

	public static AAffineTransform createAffineTransform(AMatrix m, AVector v) {
		if (m instanceof Matrix33) {
			return createAffineTransform((Matrix33)m,v);
		}
		return new AffineMN(m,v);
	}

	public static ATransform constantTransform(int inputDimensions, AVector v) {
		int dims=v.length();
		switch (dims) {
			case 3: return new ConstantTransform3(inputDimensions,v);
			case 4: return new ConstantTransform4(inputDimensions,v);
			default: return new ConstantTransform(inputDimensions,v);
		}
	}

	public static ATranslation createTranslation(double[] v) {
		int dims=v.length;
		if (dims==3) {
			return new Translation3(v[0],v[1],v[2]);
		}
		return new Translation(v);
	}
}
