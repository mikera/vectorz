package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public final class Translation extends ATranslation {
	private final AVector translation;
	private final int dimensions;
	
	public Translation(AVector source) {
		translation=source;
		dimensions=source.length();
	}
	
	public Translation(ATranslation t) {
		this(Vectorz.createMutableVector(t.getTranslationComponent().getTranslationVector()));
	}

	@Override
	public AVector getTranslationVector() {
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
	
	@Override 
	public int dimensions() {
		return dimensions;
	}
	
	@Override 
	public void composeWith(ATransform t) {
		if (t instanceof ATranslation) {
			composeWith((ATranslation) t);
			return;
		}
		super.composeWith(t);
	}
	
	public void composeWith(ATranslation t) {
		if (t instanceof Translation) {
			composeWith((Translation) t);
			return;
		}
		AVector v=t.getTranslationVector();
		translation.add(v);
	}
	
	public void composeWith(Translation t) {
		assert(t.dimensions()==this.dimensions());
		translation.add(t.translation);
	}
}
