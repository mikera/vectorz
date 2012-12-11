package mikera.transformz.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.transformz.AAffineTransform;
import mikera.transformz.ATranslation;
import mikera.transformz.Translation;
import mikera.vectorz.AVector;

/**
 * Class represnting a transform that returns a constant
 * @author Mike
 *
 */
public class Constant extends AAffineTransform {
	private final int inputDimensions;
	private final int outputDimensions;
	private AVector constant;
	
	public Constant(int inputDimensions, AVector value) {
		this.inputDimensions=inputDimensions;
		constant=value.isMutable()?value.clone():value;
		outputDimensions=value.length();
	}
	
	@Override
	public AMatrix getMatrixComponent() {
		return new ZeroMatrix(outputDimensions,inputDimensions);
	}

	@Override
	public ATranslation getTranslationComponent() {
		// TODO: consider defensive copy?
		return new Translation(constant);
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
