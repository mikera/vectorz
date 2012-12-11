package mikera.transformz.impl;

import mikera.transformz.ATranslation;
import mikera.transformz.Translation;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector4;

/**
 * Class represnting a transform that returns a 4D constant
 * @author Mike
 *
 */
public final class ConstantTransform4 extends AConstantTransform {
	private double x,y,z,t;
	
	/**
	 * Creates a new constant transform, using the provided vector as the constant value
	 * Does *not* take a defensive copy
	 * @param inputDimensions
	 * @param value
	 */
	public ConstantTransform4(int inputDimensions, AVector value) {
		super(inputDimensions);
		x=value.get(0);
		y=value.get(1);
		z=value.get(2);
		t=value.get(3);
	}

	@Override
	public int outputDimensions() {
		return 4;
	}

	@Override
	public void transform(AVector source, AVector dest) {
		if (dest instanceof Vector4) {
			transform(source,(Vector4)dest);
			return;
		}
		assert(source.length()==inputDimensions());
		dest.set(0,x);
		dest.set(1,y);
		dest.set(2,z);
		dest.set(3,t);
	}
	
	public void transform(AVector source, Vector4 dest) {
		assert(source.length()==inputDimensions());
		dest.x=x;
		dest.y=y;
		dest.z=z;
		dest.t=t;
	}


	@Override
	public ATranslation getTranslationComponent() {
		return new Translation(new double[]{x,y,z,t});
	}

	@Override
	public AVector getConstantValue() {
		return new Vector4(x,y,z,y);
	}

}
