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
	public abstract AMatrix getMatrixComponent();

	public abstract ATranslation getTranslationComponent();

	// ===========================================
	// Standard implementation
	
	/**
	 * Returns a deep copy of the translation vector for this affine transform
	 */
	public AVector copyOfTranslationVector() {
		return getTranslationComponent().getTranslationVector().clone();
	}
	
	/**
	 * Returns a deep copy of the transformation matrix for this affine transform
	 */
	public AMatrix copyOfMatrix() {
		return getMatrixComponent().clone();
	}
	
	@Override 
	public boolean isIdentity() {
		return getMatrixComponent().isIdentity()
		    && getTranslationComponent().isIdentity();
	}
	
	@Override
	public ATransform compose(ATransform a) {
		if (a instanceof AAffineTransform) return compose((AAffineTransform)a);
		return super.compose(a);
	}
	
	public ATransform compose(AAffineTransform a) {
		AVector v=a.copyOfTranslationVector();
		AMatrix thisM=getMatrixComponent();
		thisM.transformInPlace(v);
		v.add(getTranslationComponent().getTranslationVector());
		
		AMatrix m=thisM.compose(a.getMatrixComponent());
		
		return Transformz.createAffineTransform(m, v);
	}
	
	@Override 
	public void transform(AVector source, AVector dest) {
		getMatrixComponent().transform(source,dest);
		getTranslationComponent().transformInPlace(dest);
	}
	
	public void transformNormal(AVector source, AVector dest) {
		getMatrixComponent().transform(source,dest);
		dest.normalise();
	}
	
	@Override
	public double calculateComponent(int i, AVector v) {
		return getMatrixComponent().calculateComponent(i,v)
				+getTranslationComponent().getTranslationComponent(i);
		
	}


	
	@Override 
	public void transformInPlace(AVector v) {
		getMatrixComponent().transformInPlace(v);
		getTranslationComponent().transformInPlace(v);
	}

	public AAffineTransform toAffineTransform() {
		return new AffineMN(this);
	}
	
	@Override
	public int hashCode() {
		return getMatrixComponent().hashCode()+getTranslationComponent().hashCode();
	}
	
	@Override
	public boolean  equals(Object o) {
		if (!(o instanceof AAffineTransform)) return false;
		return equals((AAffineTransform)o);
	}
	
	public boolean equals(AAffineTransform a) {
		
		return a.getMatrixComponent().equals(getMatrixComponent()) &&
			   a.getTranslationComponent().equals(getTranslationComponent());	
	}

	@Override
	public AAffineTransform inverse() {
		AMatrix m=getMatrixComponent().inverse();
		AVector v=getTranslationComponent().getTranslationVector().clone();
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
		return getMatrixComponent().isInvertible();
	}
}
