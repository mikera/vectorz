package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Abstract base class for affine transformations
 * @author Mike
 *
 */
public abstract class AAffineTransform extends ATransform {
	// ===========================================
	// Abstract interface
	public abstract AMatrix getMatrixComponent();

	public abstract ATranslation getTranslationComponent();

	// ===========================================
	// Standard implementation
	
	@Override
	public boolean isLinear() {
		return true;
	}
	
	@Override 
	public void transform(AVector source, AVector dest) {
		getMatrixComponent().transform(source,dest);
		getTranslationComponent().transformInPlace(dest);
	}
	
	@Override 
	public void transformInPlace(AVector v) {
		getMatrixComponent().transformInPlace(v);
		getTranslationComponent().transformInPlace(v);
	}

	public AAffineTransform toAffineTransform() {
		return new AffineMN(this);
	}
}
