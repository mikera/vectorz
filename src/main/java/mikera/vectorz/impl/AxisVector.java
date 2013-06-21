package mikera.vectorz.impl;

import mikera.arrayz.ISparse;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector2;
import mikera.vectorz.Vector3;
import mikera.vectorz.util.VectorzException;

/**
 * Specialized unit axis vector. Has a 1.0 in one element, 0.0 everywhere else.
 * 
 * @author Mike
 */
public class AxisVector extends ComputedVector implements ISparse {
	private static final long serialVersionUID = 6767495113060894804L;
	
	private final int axis;
	private final int length;
	
	public AxisVector(int axisIndex, int length) {
		assert(length>=1);
		assert((axisIndex>=0)&&(axisIndex<length));
		this.axis=axisIndex;
		this.length=length;
	}
	
	@Override
	public int length() {
		return length;
	}
	
	@Override
	public double magnitude() {
		return 1.0;
	}
	
	@Override
	public double magnitudeSquared() {
		return 1.0;
	}
	
	@Override 
	public double normalise() {
		// nothing to do, already unit length
		return 1.0;
	}
	
	@Override 
	public void square() {
		// no effect
	}
	
	@Override
	public double elementSum() {
		return 1.0;
	}
	
	@Override
	public long nonZeroCount() {
		return 1;
	}
	
	@Override
	public boolean isMutable() {
		return false; // i.e. immutable
	}

	@Override
	public boolean isZeroVector() {
		return false;
	}

	
	@Override
	public boolean isUnitLengthVector() {
		return true;
	}
	
	@Override 
	public double dotProduct(AVector v) {
		assert(length==v.length());
		return v.get(axis);
	}
	
	public double dotProduct(Vector3 v) {
		assert(length==3);
		switch (axis) {
			case 0: return v.x;
			case 1: return v.y;
			case 2: return v.z;
			default: throw new IndexOutOfBoundsException();
		}
	}
	
	public double dotProduct(Vector2 v) {
		assert(length==2);
		switch (axis) {
			case 0: return v.x;
			case 1: return v.y;
			default: throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public double get(int i) {
		assert((i>=0)&&(i<length));
		return (i==axis)?1.0:0.0;
	}
	
	@Override
	public AxisVector exactClone() {
		// immutable, so return self
		return this;
	}
	
	@Override
	public double density() {
		return 1.0/length;
	}
	
	@Override
	public void validate() {
		if ((axis<0)||(axis>length)) throw new VectorzException("Axis index out of bounds");
		super.validate();
	}
}
