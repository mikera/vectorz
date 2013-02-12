package mikera.transformz.impl;

import mikera.indexz.Index;
import mikera.transformz.ATransform;
import mikera.vectorz.AVector;

/**
 * Transform wrapper that produces a subset of the output components of another transform
 * 
 * @author Mike
 */
public final class SubsetTransform extends ATransform {
	
	private ATransform source;
	private Index components;

	private SubsetTransform(ATransform trans, Index components) {
		this.source=trans;
		this.components=components;
	}
	
	public static SubsetTransform create(ATransform trans, Index components) {
		if (trans instanceof SubsetTransform) {
			return create((SubsetTransform)trans, components);
		}
		return new SubsetTransform(trans,components);
	}
	
	public static SubsetTransform create(SubsetTransform trans, Index components) {
		return new SubsetTransform(trans.source,components.compose(trans.components));
	}
	
	@Override
	public double calculateElement(int i, AVector source) {
		return this.source.calculateElement(components.get(i),source);
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		AVector v=this.source.transform(source);
		dest.set(v,components);
	}

	@Override
	public int inputDimensions() {
		return source.inputDimensions();
	}

	@Override
	public int outputDimensions() {
		return components.length();
	}

}
