package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Represents a translation by a fixed vector
 * 
 * @author Mike
 */
public final class Translation extends ATranslation {
	private final AVector translationVector;
	private final int dimensions;
	
	public Translation(AVector source) {
		translationVector=source;
		dimensions=source.length();
	}
	
	public Translation(ATranslation t) {
		this(Vectorz.create(t.getTranslation().getTranslationVector()));
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		return v.get(i)+translationVector.get(i);
	}

	public Translation(double[] v) {
		dimensions=v.length;
		translationVector=Vectorz.create(v);
	}

	@Override
	public AVector getTranslationVector() {
		return translationVector;
	}
	
	@Override
	public void transform(AVector source,AVector dest) {
		dest.set(source);
		dest.add(translationVector);
	}
	
	@Override
	public void transformInPlace(AVector v) {
		v.add(translationVector);
	}
	

	@Override
	public AMatrix getMatrix() {
		return Matrixx.createImmutableIdentityMatrix(dimensions);
	}

	@Override
	public ATranslation getTranslation() {
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
		translationVector.add(v);
	}
	
	public void composeWith(Translation t) {
		assert(t.dimensions()==this.dimensions());
		translationVector.add(t.translationVector);
	}
}
