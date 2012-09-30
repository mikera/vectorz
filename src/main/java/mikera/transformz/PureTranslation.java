package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;

public final class PureTranslation extends ATranslation {
	private final AVector translation;
	private final int dimensions;
	
	public PureTranslation(AVector source) {
		translation=source;
		dimensions=source.length();
	}
	
	@Override
	public AVector translationVector() {
		return translation;
	}
	
	@Override
	public void transform(AVector source,AVector dest) {
		dest.set(source);
		dest.add(translation);
	}
	
	@Override
	public void transformInPlace(AVector v) {
		v.add(translation);
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
	public int inputDimensions() {
		return dimensions;
	}

	@Override
	public int outputDimensions() {
		return dimensions;
	}

}
