package mikera.transformz.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.transformz.ATranslation;
import mikera.transformz.Transformz;
import mikera.vectorz.AVector;

/**
 * Class represnting a transform that returns a constant
 * @author Mike
 *
 */
public final class Constant extends AConstantTransform {
	private final int outputDimensions;
	private double[] constant;
	
	/**
	 * Creates a new constant transform, using the provided vector as the constant value
	 * Does *not* take a defensive copy
	 * @param inputDimensions
	 * @param value
	 */
	public Constant(int inputDimensions, AVector value) {
		super(inputDimensions);
		outputDimensions=value.length();
		constant=new double[outputDimensions];
		value.copyTo(constant, 0);
	}

	

	@Override
	public int outputDimensions() {
		return outputDimensions;
	}

	@Override
	public void transform(AVector source, AVector dest) {
		assert(source.length()==inputDimensions());
		dest.setValues(constant);
	}

	@Override
	public AMatrix getMatrixComponent() {
		return Matrixx.createImmutableZeroMatrix(outputDimensions, inputDimensions());
	}

	@Override
	public ATranslation getTranslationComponent() {
		return Transformz.createTranslation(constant);
	}

}
