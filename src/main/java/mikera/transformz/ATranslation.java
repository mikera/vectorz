package mikera.transformz;

import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;

/**
 * Abstract base class for translations of arbitrary dimensionality.
 * 
 * @author Mike
 */
public abstract class ATranslation extends AAffineTransform {
	// =========================================
	// Abstract interface
	
	/**
	 * Gets the vector offset that this translation represents. Not guaranteed to be a reference.
	 * @return
	 */
	@Override
	public abstract AVector getTranslationVector();
	
	// =========================================
	// Standard implementations

	/**
	 * Returns the number of dimensions of this translation
	 */
	public int dimensions() {
		return getTranslationVector().length();
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		return v.unsafeGet(i)+getTranslationComponent(i);
	}
	
	public double getTranslationComponent(int i) {
		return getTranslationVector().unsafeGet(i);
	}
	
	@Override
	public void transform(AVector source,AVector dest) {
		dest.set(source);
		dest.add(getTranslationVector());
	}
	
	@Override
	public void transformNormal(AVector source, AVector dest) {
		// translation does not affect normal
		dest.set(source);
	}
	
	@Override
	public void transformInPlace(AVector v) {
		v.add(getTranslationVector());
	}
	
	@Override
	public AAffineTransform toAffineTransform() {
		return new AffineMN(Matrixx.createImmutableIdentityMatrix(dimensions()),this);
	}

	public ATranslation toMutableTranslation() {
		return Transformz.createMutableTranslation(this);
	}

	/**
	 * Returns true if this transform is an identity transform
	 */
	@Override
	public boolean isIdentity() {
		return getTranslationVector().isZero();
	}
	
	public boolean equals(ATranslation a) {
		return this.getTranslationVector().equals(a.getTranslationVector());
	}
	
	@Override
	public boolean equals(AAffineTransform a) {
		return this.equals(a.getTranslation())&&
				a.getMatrix().isIdentity();
	}
	
	@Override
	public ATranslation inverse() {
		AVector v=getTranslationVector().clone();
		v.negate();
		return Transformz.createTranslation(v);
	}
	
	@Override
	public boolean isSquare() {
		return true;
	}
	
	@Override
	public boolean isInvertible() {
		return true;
	}
	
	@Override
	public int hashCode() {
		return getTranslationVector().hashCode();
	}
}
