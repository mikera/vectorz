package mikera.transformz;

import mikera.matrixx.AMatrix;

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
}
