package mikera.vectorz.impl;

import mikera.vectorz.util.ErrorMessages;

/**
 * Immutable vector type representing a consecutive, increasing range of integers.
 * 
 * @author Mike
 *
 */
public class RangeVector extends AComputedVector {
	private static final long serialVersionUID = 2068299118332621781L;

	private final int start;
	private final int length;
	
	private RangeVector(int start, int length) {
		this.start=start;
		this.length=length;
	}
	
	public static RangeVector create(int start, int length) {
		if (length<0) throw new IllegalArgumentException(ErrorMessages.illegalSize(length));
		return new RangeVector(start,length);
	}
	
	@Override
	public int length() {
		return length;
	}

	@Override
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return start+i;
	}
	
	@Override
	public double unsafeGet(int i) {
		return start+i;
	}
	
	@Override
	public RangeVector subVector(int start, int length) {
		if ((start<0)||(start+length>this.length)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, start, length));
		}
		return create(this.start+start,length);
	}
}
