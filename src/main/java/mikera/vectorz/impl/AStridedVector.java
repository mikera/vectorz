package mikera.vectorz.impl;

import mikera.vectorz.AVector;

public abstract class AStridedVector extends AVector {
	private static final long serialVersionUID = -7239429584755803950L;

	public abstract double[] getArray();
	public abstract int getArrayOffset();
	public abstract int getStride();
}
