package mikera.transformz.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.randomz.Hash;
import mikera.transformz.ATranslation;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;

/**
 * Immutable identity translation
 * 
 * @author Mike
 *
 */
public final class IdentityTranslation extends ATranslation {
	private static final int INSTANCE_COUNT=6;

	private final int dimensions;
	
	private IdentityTranslation(int dims) {
		this.dimensions=dims;
	}
	
	private static final  IdentityTranslation[] INSTANCES=new IdentityTranslation[INSTANCE_COUNT];
	static {
		for (int i=0; i<INSTANCE_COUNT; i++) {
			INSTANCES[i]=new IdentityTranslation(i);
		}
	}
	
	public static IdentityTranslation create(int i) {
		if (i<INSTANCE_COUNT) return INSTANCES[i];
		return new IdentityTranslation(i);
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
	public void transform(AVector source, AVector dest) {
		dest.set(source);		
	}
	
	@Override
	public Vector transform(AVector source) {
		return source.toVector();		
	}
	
	@Override
	public void transformInPlace(AVector v) {
		// no change!
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		return v.get(i);
	}
	
	@Override
	public boolean isIdentity() {
		return true;
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
	public AVector getTranslationVector() {
		return Vectorz.immutableZeroVector(dimensions);
	}
	
	@Override 
	public int hashCode() {
		return Hash.zeroVectorHash(dimensions);
	}
}
