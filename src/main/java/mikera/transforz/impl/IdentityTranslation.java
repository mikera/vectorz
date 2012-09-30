package mikera.transforz.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.transformz.ATranslation;
import mikera.vectorz.AVector;

public final class IdentityTranslation extends ATranslation {

	private final int dimensions;
	
	private IdentityTranslation(int dims) {
		this.dimensions=dims;
	}

	@Override
	public AMatrix getMatrixComponent() {
		return Matrixx.createIdentityMatrix(dimensions);
	}

	@Override
	public ATranslation getTranslationComponent() {
		return this;
	}

	@Override
	public void transform(AVector source, AVector dest) {
		dest.set(source);		
	}

	@Override
	public int inputDimensions() {
		return dimensions;
	}

	@Override
	public int outputDimensions() {
		return dimensions;
	}

	public static IdentityTranslation getInstance(int dimensions) {
		return new IdentityTranslation(dimensions);
	}

}
