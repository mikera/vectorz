package mikera.vectorz.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;


/**
 * An immutable vector that has the same value for elements.
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
	public AVector applyOpCopy(Op op) {
		if (op.isStochastic()) return super.applyOpCopy(op);
		
		double v=op.apply(value);
		return Vectorz.createRepeatedElement(length, v);
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
	public double elementSquaredSum() {
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
	public double dotProduct(double[] data, int offset, int stride) {
		return value*DoubleArrays.elementSum(data, offset, stride, length);
	}
	
	@Override
	public AVector multiplyCopy(double d) {
		return RepeatedElementVector.create(length,d*value);
	}
	
	@Override
	public AVector innerProduct(AMatrix m) {
		int rc=m.rowCount();
		int cc=m.columnCount();
		this.checkLength(rc);
		AVector r=Vector.createLength(cc);
		List<AVector> cols=m.getColumns();
		for (int i=0; i<cc; i++) {
			AVector col=cols.get(i);
			r.unsafeSet(i, value*col.elementSum());
		}
		return r;
	}
	
	@Override
	public AVector select(int... order) {
		int n=order.length;
		for (int i:order) checkIndex(i);
		if (n==length) return this;
		if (n==0) return Vector0.INSTANCE;
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
	public AVector multiplyCopy(AVector v) {
		checkSameLength(v);
		return v.scaleCopy(value);
	}
	
	@Override
	public AVector addCopy(double v) {
		return Vectorz.createRepeatedElement(length, value+v);
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
		return Vectorz.createRepeatedElement(length,value);
	}
	
	@Override
	public AVector tryEfficientJoin(AVector a) {
		if (a instanceof RepeatedElementVector) {
			RepeatedElementVector ra=(RepeatedElementVector) a;
			if (ra.value==this.value) return Vectorz.createRepeatedElement(length+ra.length, value);
		}
		return null;
	}

	@Override 
	public RepeatedElementVector exactClone() {
		return new RepeatedElementVector(length,value);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		Arrays.fill(dest, offset,offset+length, value);
	}
	
	@Override
	public boolean equals(AVector v) {
		if (v==this) return true;
		if (length!=v.length()) return false;
		return v.elementsEqual(value);
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
		return Vectorz.isUncountable(value);
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
