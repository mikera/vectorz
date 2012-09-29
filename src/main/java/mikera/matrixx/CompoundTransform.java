package mikera.matrixx;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Class to represent a compound transform when it is not know how to combine the transforms
 * directly.
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
	public void transform(AVector source, AVector dest) {
		AVector temp=Vectorz.createLength(inner.outputDimensions());
		inner.transform(source,temp);
		outer.transform(temp,dest);
	}

	@Override
	public int inputDimensions() {
		return inner.inputDimensions();
	}

	@Override
	public int outputDimensions() {
		// TODO Auto-generated method stub
		return outer.outputDimensions();
	}
}
