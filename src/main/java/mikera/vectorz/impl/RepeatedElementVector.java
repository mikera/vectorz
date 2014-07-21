package mikera.vectorz.impl;

import java.util.Iterator;

import mikera.vectorz.AVector;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;


/**
 * An immutable vector that always has a single repeated component.
 *
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public final class RepeatedElementVector extends ASizedVector {
	private final double value;
	
	private RepeatedElementVector(int length, double value) {
		super(length);
		this.value=value;
	}
	
	public static RepeatedElementVector create(int length, double value) {
		if (length<1) throw new IllegalArgumentException("RepeatedElementVector must have at least one element");
		return new RepeatedElementVector(length,value);
	}

	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isZero() {
		return value==0.0;
	}
	
	@Override
	public boolean isRangeZero(int start, int length) {
		return (length==0)||(value==0.0);
	}
	
	@Override
	public boolean isElementConstrained() {
		return true;
	}
	
	@Override
	public double get(int i) {
		checkIndex(i);
		return value;
	}
	
	@Override
	public double unsafeGet(int i) {
		return value;
	}
	
	@Override
	public double elementSum() {
		return length*value;
	}
	
	@Override
	public double elementProduct() {
		return Math.pow(value, length);
	}
	
	@Override
	public double elementMax(){
		return value;
	}
	
	@Override
	public double elementMin(){
		return value;
	}
	
	@Override
	public double magnitudeSquared() {
		return length*value*value;
	}
	
	@Override
	public double dotProduct(AVector v) {
		return value*v.elementSum();
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		return value*DoubleArrays.elementSum(data, offset, length);
	}
	
	@Override 
	public double dotProduct(Vector v) {
		return value*v.elementSum();
	}
	
	@Override
	public AVector innerProduct(double d) {
		return RepeatedElementVector.create(length,d*value);
	}
	
	@Override
	public Scalar innerProduct(AVector v) {
		return Scalar.create(dotProduct(v));
	}
	
	@Override
	public AVector reorder(int dim, int[] order) {
		if (dim!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this, dim));
		return reorder(order);
	}	
	
	@Override
	public AVector reorder(int[] order) {
		int n=order.length;
		if (n==length) return this;
		return create(n,value);
	}	
	
	@Override
	public AVector reciprocalCopy() {
		return create(length,1.0/value);
	}
	
	@Override
	public AVector absCopy() {
		return create(length,Math.abs(value));
	}
	
	@Override
	public AVector negateCopy() {
		return create(length,-value);
	}

	@Override
	public AVector addCopy(AVector v) {
		return v.addCopy(value);
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		DoubleArrays.add(data, offset, length, value);
	}
	
	@Override
	public long nonZeroCount() {
		return (value==0.0)?0:length;
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public Iterator<Double> iterator() {
		return new RepeatedElementIterator(length,value);
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		int len=checkRange(offset,length);
		if (length==len) return this;
		if (length==0) return Vector0.INSTANCE;
		return RepeatedElementVector.create(length,value);
	}
	
	@Override
	public AVector tryEfficientJoin(AVector a) {
		if (a instanceof RepeatedElementVector) {
			RepeatedElementVector ra=(RepeatedElementVector) a;
			if (ra.value==this.value) return RepeatedElementVector.create(length+ra.length, value);
		}
		return null;
	}

	@Override 
	public RepeatedElementVector exactClone() {
		return new RepeatedElementVector(length,value);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		for (int i=0; i<length; i++) {
			if (data[offset+i]!=value) return false;
		}
		return true;
	}
	
	@Override
	public boolean elementsEqual(double value) {
		return this.value==value;
	}

	@Override
	public boolean hasUncountable() {
		return Double.isNaN(value) || Double.isInfinite(value);
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return length*Math.pow(value, p);
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return length*Math.pow(Math.abs(value), p);
    }
}
