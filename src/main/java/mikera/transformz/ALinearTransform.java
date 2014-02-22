package mikera.transformz;

import mikera.transformz.impl.IdentityTranslation;

/**
 * Abstract base class representing a linear transfrom.
 * 
 * AMatrix is the main implementation
 * 
 * @author Mike
 *
 */

public abstract class ALinearTransform extends AAffineTransform {
	@Override
	public boolean isLinear() {
		return true;
	}

	@Override
	public IdentityTranslation getTranslation() {
		return Transformz.identityTranslation(outputDimensions());
	}
}
