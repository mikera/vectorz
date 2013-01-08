package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector3;

public class AxisVector extends ComputedVector {
	private static final long serialVersionUID = 6767495113060894804L;
	
	private final int axis;
	private final int length;
	
	public AxisVector(int axisIndex, int length) {
		this.axis=axisIndex;
		this.length=length;
	}
	
	@Override
	public int length() {
		return length;
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

	@Override
	public double get(int i) {
		assert((i>=0)&&(i<length));
		return (i==axis)?1.0:0.0;
	}

}
