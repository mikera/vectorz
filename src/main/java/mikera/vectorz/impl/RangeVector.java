package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Immutable vector type representing a consecutive, increasing range of integers.
 * 
 * Must have length of 1 or more
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
	
	public static AVector create(int start, int length) {
		if (length==0) return Vector0.INSTANCE;
		if (length<0) {
			throw new IllegalArgumentException(ErrorMessages.illegalSize(length));
		}
		return new RangeVector(start,length);
	}
	
	@Override
	public double elementMin() {
		return start;
	}
	
	@Override
	public double elementMax() {
		return start+length-1;
	}
	
	@Override
	public double elementSum() {
		// compute sum of arithmetic progression directly
		return ((double)start*length) + ((length*(length-1))/2);
	}
	
	
	private static long sumOfSquares(int n) {
	   // formula for sum of the first n natural number squares
	   return (n*(n+1)*(2*n+1))/6;
	}
	
	@Override
	public double elementSquaredSum() {
		// compute sum of arithmetic progression directly
		return sumOfSquares(start+length-1)-sumOfSquares(start-1);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
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
		int len=checkRange(start,length);
		if (length==0) return Vector0.INSTANCE;
		if (length==len) return this;
		
		return create(this.start+start,length);
	}
	
	@Override
	public AVector tryEfficientJoin(AVector a) {
		if (a instanceof RangeVector) {
			RangeVector ra=(RangeVector) a;
			if (ra.start==this.start+this.length) return RangeVector.create(start,length+ra.length);
		}
		return null;
	}
}
