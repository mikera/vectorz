package mikera.vectorz.impl;

import mikera.vectorz.AVector;
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
	
	private RangeVector(int start, int length) {
		super(length);
		this.start=start;
	}
	
	public static RangeVector create(int start, int length) {
		if (length<0) throw new IllegalArgumentException(ErrorMessages.illegalSize(length));
		return new RangeVector(start,length);
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
	public double dotProduct(double[] data, int offset) {
		double res=0;
		for (int i=0; i<length; i++) {
			res+=(i+start)*data[i+offset];
		}
		return res;
	}
	
	@Override
	public AVector subVector(int start, int length) {
		if ((start<0)||(start+length>this.length)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, start, length));
		}
		if (length==0) return Vector0.INSTANCE;
		if (length==this.length) return this;
		
		return create(this.start+start,length);
	}
}
