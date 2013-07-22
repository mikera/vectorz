package mikera.vectorz.impl;

import mikera.arrayz.impl.IStridedArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract base class for vectors backed by a double[] array with a constant stride
 * 
 * The double array can be directly accessed for performance purposes
 * 
 * @author Mike
 */
public abstract class AStridedVector extends AVector implements IStridedArray {
	private static final long serialVersionUID = -7239429584755803950L;

	public abstract double[] getArray();
	public abstract int getArrayOffset();
	public abstract int getStride();
	
	@Override
	public AStridedVector ensureMutable() {
		return clone();
	}
	
	@Override public double dotProduct(double[] data, int offset) {
		double[] array=getArray();
		int thisOffset=getArrayOffset();
		int stride=getStride();
		int length=length();
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=array[i*stride+thisOffset]*data[i+offset];
		}
		return result;
	}
	
	@Override
	public AStridedVector clone() {
		return Vector.create(this);
	}
	
	public void add(Vector v) {
		int length=length();
		if(length!=v.length()) throw new IllegalArgumentException("Mismatched vector sizes");
		for (int i = 0; i < length; i++) {
			addAt(i,v.data[i]);
		}
	}
	
	@Override
	public int[] getStrides() {
		return new int[] {getStride()};
	}
	
	@Override
	public int getStride(int dimension) {
		switch (dimension) {
		case 0: return getStride();
		default: throw new IllegalArgumentException(ErrorMessages.invalidDimension(this, dimension));
		}
	}
}
