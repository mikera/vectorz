package mikera.vectorz.impl;

import java.util.Iterator;

import mikera.vectorz.AVector;
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
	public boolean isElementConstrained() {
		return true;
	}
	
	@Override
	public double get(int i) {
		if (!((i>=0)&&(i<length))) throw new IndexOutOfBoundsException();
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
		if ((offset<0)||(offset+length>this.length)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, offset, length));
		}
		if (length==this.length) return this;
		if (length==0) return Vector0.INSTANCE;
		return RepeatedElementVector.create(length,value);
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
}
