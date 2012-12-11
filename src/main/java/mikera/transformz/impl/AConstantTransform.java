package mikera.transformz.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.transformz.AAffineTransform;

public abstract class AConstantTransform extends AAffineTransform {
	private final int inputDimensions;

	AConstantTransform(int inputDimensions) {
		this.inputDimensions=inputDimensions;
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
