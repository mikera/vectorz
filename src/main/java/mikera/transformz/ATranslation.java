package mikera.transformz;

import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;

public abstract class ATranslation extends AAffineTransform {
	// =========================================
	// Abstract interface
	
	public abstract AVector getTranslationVector();
	
	// =========================================
	// Standard implementations

	public int dimensions() {
		return getTranslationVector().length();
	}
	
	@Override
	public void transform(AVector source,AVector dest) {
		dest.set(source);
		dest.add(getTranslationVector());
	}
	
	@Override
	public void transformInPlace(AVector v) {
		v.add(getTranslationVector());
	}
	
	@Override
	public AAffineTransform toAffineTransform() {
		return new AffineMN(Matrixx.createIdentityMatrix(dimensions()),this);
	}

	public ATranslation toMutableTranslation() {
		return Transformz.createMutableTranslation(this);
	}

	/**
	 * Returns true if this transform is an identity transform
	 */
	@Override
	public boolean isIdentity() {
		return getTranslationVector().isZeroVector();
	}
	
	public boolean equals(ATranslation a) {
		return this.getTranslationVector().equals(a.getTranslationVector());
	}
	
	@Override
	public boolean equals(AAffineTransform a) {
		return this.equals(a.getTranslationComponent())&&
				a.getMatrixComponent().isIdentity();
	}
	
	@Override
	public ATranslation inverse() {
		AVector v=getTranslationVector().clone();
		v.negate();
		return Transformz.createTranslation(v);
	}
}
