package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 * Abstract base class for vectors backed by a double[] array with a constant stride
 * 
 * The double array can be directly accessed for performance purposes
 * 
 * @author Mike
 */
public abstract class AStridedVector extends AVector {
	private static final long serialVersionUID = -7239429584755803950L;

	public abstract double[] getArray();
	public abstract int getArrayOffset();
	public abstract int getStride();
	
	@Override
	public AStridedVector ensureMutable() {
		return clone();
	}
	
	@Override
	public AStridedVector clone() {
		return Vector.create(this);
	}

}
