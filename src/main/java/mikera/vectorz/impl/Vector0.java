package mikera.vectorz.impl;

import java.io.ObjectStreamException;

import mikera.vectorz.APrimitiveVector;

/**
 * Special singleton zero length vector class.
 * 
 * Mainly for convenience when doing vector construction / appending etc.
 * 
 * @author Mike
 */
public final class Vector0 extends APrimitiveVector {
	private static final long serialVersionUID = -8153360223054646075L;

	private Vector0() {
	}
	
	public static Vector0 of() {
		return INSTANCE;
	}
	
	public static Vector0 of(double... values) {
		if (values.length!=0) throw new IllegalArgumentException("Vector0 cannot have components!");
		return INSTANCE;
	}
	
	public static Vector0 INSTANCE=new Vector0();
	
	@Override
	public int length() {
		return 0;
	}

	@Override
	public double get(int i) {
		throw new IndexOutOfBoundsException("Attempt to get on zero length vector!");
	}

	@Override
	public void set(int i, double value) {
		throw new IndexOutOfBoundsException("Attempt to set on zero length vector!");
	}
	
	@Override 
	public Vector0 clone() {
		return this;
	}

	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public int hashCode() {
		// 1 is hashcode for zero-length double array
		return 1;
	}
	
	@Override
	public boolean isZeroVector() {
		return true;
	}
	
	@Override
	public double magnitudeSquared() {
		return 0.0;
	}
	
	@Override
	public double magnitude() {
		return 0.0;
	}
	
	/**
	 * Readresolve method to ensure we always use the singleton
	 */
	private Object readResolve() throws ObjectStreamException {
		return INSTANCE; 
	}
}
