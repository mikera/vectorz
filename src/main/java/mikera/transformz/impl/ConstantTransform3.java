package mikera.transformz.impl;

import mikera.transformz.ATranslation;
import mikera.transformz.Translation3;
import mikera.transformz.marker.ISpecialisedTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;

/**
 * Class representing a transform that returns a 3D constant
 * @author Mike
 *
 */
public final class ConstantTransform3 extends AConstantTransform implements ISpecialisedTransform {
	private double x,y,z;
	
	/**
	 * Creates a new constant transform, using the provided vector as the constant value
	 * Does *not* take a defensive copy
	 * @param inputDimensions
	 * @param value
	 */
	public ConstantTransform3(int inputDimensions, AVector value) {
		super(inputDimensions);
		x=value.get(0);
		y=value.get(1);
		z=value.get(2);
	}
	
	@Override
	public double calculateElement(int i, AVector inputVector) {
		switch (i) {
			case 0: return x;
			case 1: return y;
			case 2: return z;
			default: throw new IndexOutOfBoundsException("Index: "+i);
		}
	}

	@Override
	public int outputDimensions() {
		return 3;
	}

	@Override
	public void transform(AVector source, AVector dest) {
		if (dest instanceof Vector3) {
			transform(source,(Vector3)dest);
			return;
		}
		assert(source.length()==inputDimensions());
		dest.set(0,x);
		dest.set(1,y);
		dest.set(2,z);
	}
	
	@Override
	public Vector transform(AVector source) {
		return Vector.of(x,y,z);
	}
	
	public Vector3 transform(Vector3 source) {
		return Vector3.of(x,y,z);
	}
	
	public void transform(AVector source, Vector3 dest) {
		assert(source.length()==inputDimensions());
		dest.x=x;
		dest.y=y;
		dest.z=z;
	}


	@Override
	public ATranslation getTranslation() {
		return new Translation3(x,y,z);
	}

	@Override
	public AVector getConstantValue() {
		return new Vector3(x,y,z);
	}

}
