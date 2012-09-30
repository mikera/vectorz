package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

public final class AffineMN extends AAffineTransform {
	private final AMatrix matrix;
	private final ATranslation translation;
	private final int inputDimensions;
	private final int outputDimensions;
	
	public AffineMN(AMatrix matrix, ATranslation translation) {
		this.matrix=matrix;
		this.translation=translation;
		inputDimensions=matrix.inputDimensions();
		outputDimensions=matrix.outputDimensions();
		if (outputDimensions!=translation.inputDimensions()) {
			throw new IllegalArgumentException("matrix and translation have incompatible dimensionality");
		}
	}
	
	@Override
	public AMatrix getMatrixComponent() {
		return matrix;
	}

	@Override
	public ATranslation getTranslationComponent() {
		return translation;
	}

	@Override
	public void transform(AVector source, AVector dest) {
		matrix.transform(source,dest);
		translation.transformInPlace(dest);
	}

	@Override
	public int inputDimensions() {
		return inputDimensions;
	}

	@Override
	public int outputDimensions() {
		return outputDimensions;
	}

}
