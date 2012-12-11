package mikera.transformz.impl;

import mikera.transformz.ATransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Class to represent a compound transform when it is not known how to 
 * combine the transforms directly.
 * 
 * @author Mike
 *
 */
public class CompoundTransform extends ATransform {
	private ATransform outer;
	private ATransform inner;
	
	public CompoundTransform(ATransform outer, ATransform inner) {
		if (inner.outputDimensions()!=outer.inputDimensions()) {
			throw new IllegalArgumentException("Transform dimensionality not compatible");
		}
		this.outer=outer;
		this.inner=inner;
	}

	@Override
	public boolean isLinear() {
		return inner.isLinear()&&outer.isLinear();
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		AVector temp=Vectorz.newVector(inner.outputDimensions());
		inner.transform(source,temp);
		outer.transform(temp,dest);
	}

	@Override
	public int inputDimensions() {
		return inner.inputDimensions();
	}
	
	@Override
	public CompoundTransform compose(ATransform trans) {
		return new CompoundTransform(outer,inner.compose(trans));
	}

	@Override
	public int outputDimensions() {
		return outer.outputDimensions();
	}
}
