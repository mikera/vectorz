package mikera.transformz.impl;

import mikera.transformz.ATransform;
import mikera.vectorz.AVector;

/**
 * Class represnting a transform that returns a constant
 * @author Mike
 *
 */
public class Constant extends ATransform {
	private final int inputDimensions;
	private final int outputDimensions;
	private double[] constant;
	
	/**
	 * Creates a new constant transform, using the provided vector as the constant value
	 * Does *not* take a defensive copy
	 * @param inputDimensions
	 * @param value
	 */
	public Constant(int inputDimensions, AVector value) {
		this.inputDimensions=inputDimensions;
		outputDimensions=value.length();
		constant=new double[outputDimensions];
		value.copyTo(constant, 0);
	}

	@Override
	public int inputDimensions() {
		return inputDimensions;
	}

	@Override
	public int outputDimensions() {
		return outputDimensions;
	}

	@Override
	public void transform(AVector source, AVector dest) {
		assert(source.length()==inputDimensions);
		dest.setValues(constant);
	}

}
