package mikera.transformz.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.transformz.AAffineTransform;
import mikera.transformz.ATransform;
import mikera.vectorz.AVector;

/**
 * Abstract base class for transforms that produce a constant result.
 * @author Mike
 */
public abstract class AConstantTransform extends AAffineTransform {
	private final int inputDimensions;

	@Override
	public AAffineTransform inverse() {
		throw new UnsupportedOperationException("Cannot get inverse of a constant transform!");
	}
	
	AConstantTransform(int inputDimensions) {
		this.inputDimensions=inputDimensions;
	}
	
	@Override
	public boolean isIdentity() {
		// constant transform can't be identity!
		return false;
	}
	
	@Override
	public AConstantTransform compose(ATransform trans) {
		// constant transforms map everything to the same constant!
		return this;
	}

	
	@Override
	public int inputDimensions() {
		return inputDimensions;
	}
	
	@Override
	public AMatrix getMatrix() {
		return Matrixx.createImmutableZeroMatrix(outputDimensions(), inputDimensions());
	}

	public abstract AVector getConstantValue();

}
