package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Abstract base class for affine transformations
 * 
 * @author Mike
 */
public abstract class AAffineTransform extends ATransform {
	// ===========================================
	// Abstract interface
	public abstract AMatrix getMatrix();

	public abstract ATranslation getTranslation();

	// ===========================================
	// Standard implementation
	
	/**
	 * Returns a deep copy of the translation vector for this affine transform
	 */
	public AVector copyOfTranslationVector() {
		return getTranslation().getTranslationVector().clone();
	}
	
	/**
	 * Returns a deep copy of the transformation matrix for this affine transform
	 */
	public AMatrix copyOfMatrix() {
		return getMatrix().clone();
	}
	
	public MatrixTransform getMatrixTransform() {
		return new MatrixTransform(getMatrix());
	}
	
	/**
	 * Gets the translation offset that is part of this affine transform.
	 * @return
	 */
	public AVector getTranslationVector() {
		return getTranslation().getTranslationVector();
	}
	
	@Override 
	public boolean isIdentity() {
		return getMatrix().isIdentity()
		    && getTranslation().isIdentity();
	}
	
	@Override
	public ATransform compose(ATransform a) {
		if (a instanceof AAffineTransform) return compose((AAffineTransform)a);
		return super.compose(a);
	}
	
	public ATransform compose(AAffineTransform a) {
		AVector v=a.copyOfTranslationVector();
		AMatrix thisM=getMatrix();
		thisM.transformInPlace(v);
		v.add(getTranslation().getTranslationVector());
		
		AMatrix m=thisM.innerProduct(a.getMatrix());
		
		return Transformz.createAffineTransform(m, v);
	}
	
	@Override 
	public void transform(AVector source, AVector dest) {
		getMatrix().transform(source,dest);
		getTranslation().transformInPlace(dest);
	}
	
	public void transformNormal(AVector source, AVector dest) {
		getMatrix().transform(source,dest);
		dest.normalise();
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		return getMatrix().rowDotProduct(i,v)
				+getTranslation().getTranslationComponent(i);
	}


	
	@Override 
	public void transformInPlace(AVector v) {
		getMatrix().transformInPlace(v);
		getTranslation().transformInPlace(v);
	}

	public AAffineTransform toAffineTransform() {
		return new AffineMN(this);
	}
	
	@Override
	public int hashCode() {
		return getMatrix().hashCode()+getTranslation().hashCode();
	}
	
	@Override
	public boolean  equals(Object o) {
		if (!(o instanceof AAffineTransform)) return false;
		return equals((AAffineTransform)o);
	}
	
	public boolean equals(AAffineTransform a) {
		
		return a.getMatrix().equals(getMatrix()) &&
			   a.getTranslation().equals(getTranslation());	
	}

	@Override
	public AAffineTransform inverse() {
		AMatrix m=getMatrix().inverse();
		AVector v=getTranslation().getTranslationVector().clone();
		v.negate();
		m.transformInPlace(v);
		return Transformz.createAffineTransform(m, v);
	}
	
	@Override 
	public boolean isLinear() {
		return true;
	}
	
	@Override
	public boolean isInvertible() {
		return getMatrix().isInvertible();
	}
}
