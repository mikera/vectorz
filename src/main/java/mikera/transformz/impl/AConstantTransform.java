package mikera.transformz.impl;

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
}
