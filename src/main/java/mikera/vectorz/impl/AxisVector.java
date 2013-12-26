package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector2;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Specialised immutable unit axis vector. Has a 1.0 in one element, 0.0 everywhere else.
 * 
 * @author Mike
 */
public class AxisVector extends ASparseVector {
	private static final long serialVersionUID = 6767495113060894804L;
	
	private final int axis;
	private final int length;
	
	public AxisVector(int axisIndex, int length) {
		assert(length>=1);
		assert((axisIndex>=0)&&(axisIndex<length));
		this.axis=axisIndex;
		this.length=length;
	}
	
	public static AxisVector create(int axisIndex, int dimensions) {
		return new AxisVector(axisIndex,dimensions);
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
	public void abs() {
		// no effect
	}
	
	@Override
	public void sqrt() {
		// no effect
	}
	
	@Override
	public void signum() {
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
	public boolean isZero() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public boolean isUnitLengthVector() {
		return true;
	}
	
	@Override 
	public double dotProduct(AVector v) {
		if (v.length()!=length) throw new IllegalArgumentException("Mismatched vector sizes");
		return v.unsafeGet(axis);
	}
	
	@Override 
	public double dotProduct(double[] data, int offset) {
		return data[offset+axis];
	}
	
	@Override 
	public double dotProduct(Vector v) {
		assert(length==v.length());
		return v.data[axis];
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
		if((i<0)||(i>=length)) throw new IndexOutOfBoundsException();
		return (i==axis)?1.0:0.0;
	}
	
	@Override
	public double unsafeGet(int i) {
		return (i==axis)?1.0:0.0;
	}
	
	@Override
	public final ImmutableScalar slice(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		if (i==axis) return ImmutableScalar.ONE;
		return ImmutableScalar.ZERO;
	}
	
	@Override
	public Vector toNormal() {
		return toVector();
	}
	
	@Override
	public Vector toVector() {
		Vector v=Vector.createLength(length);
		v.data[axis]=1.0;
		return v;
	}
	
	@Override
	public AVector subVector(int start, int length) {
		if ((start<0)||(start+length>this.length)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, start, length));
		}
		if (length==this.length) return this;
		
		if ((start<=axis)&&(start+length>axis)) {
			return AxisVector.create(axis-start,length);
		} else {
			return Vectorz.createZeroVector(length);
		}
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
		if (length<=0) throw new VectorzException("Axis vector length is too small: "+length);
		if ((axis<0)||(axis>length)) throw new VectorzException("Axis index out of bounds");
		super.validate();
	}

	@Override
	public int nonSparseElementCount() {
		return 1;
	}

	private static final ImmutableVector NON_SPARSE=ImmutableVector.of(1.0);
	
	@Override
	public AVector nonSparseValues() {
		return NON_SPARSE;
	}

	@Override
	public Index nonSparseIndexes() {
		return Index.of(axis);
	}

	@Override
	public boolean includesIndex(int i) {
		return (i==axis);
	}

	@Override
	public void set(int i, double value) {
		// TODO Auto-generated method stub
		
	}


}
