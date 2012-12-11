package mikera.transformz.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.transformz.AAffineTransform;

public abstract class AConstantTransform extends AAffineTransform {
	private final int inputDimensions;

	public AAffineTransform inverse() {
		throw new UnsupportedOperationException("Cannot get inverse of a constant transform!");
	}
	
	AConstantTransform(int inputDimensions) {
		this.inputDimensions=inputDimensions;
	}
	
	@Override
	public boolean isIdentity() {
		return false;
	}

	
	@Override
	public int inputDimensions() {
		return inputDimensions;
	}
	
	@Override
	public AMatrix getMatrixComponent() {
		return Matrixx.createImmutableZeroMatrix(outputDimensions(), inputDimensions());
	}

}
