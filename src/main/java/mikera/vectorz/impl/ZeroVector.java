package mikera.vectorz.impl;

import mikera.vectorz.APrimitiveVector;
import mikera.vectorz.Tools;

/**
 * Specialised vector containing nothing but zeros.
 * 
 * @author Mike
 */
public final class ZeroVector extends APrimitiveVector {
	private static final long serialVersionUID = -7928191943246067239L;
	
	private int length;
	
	public ZeroVector(int dimensions) {
		length=dimensions;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public double get(int i) {
		return 0;
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException("Cannot set on immutable ZeroVector");
	}
	
	@Override
	public double magnitudeSquared() {
		return 0.0;
	}
	
	@Override
	public double magnitude() {
		return 0.0;
	}


	@Override
	public boolean isZeroVector() {
		return true;
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isUnitLengthVector() {
		return false;
	}
	
	@Override
	public int hashCode() {
		return Tools.zeroVectorHash(length);
	}

}
