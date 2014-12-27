package mikera.vectorz.impl;

import java.util.Arrays;
import java.util.List;

import mikera.arrayz.ISparse;
import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract base class for Sparse vector implementations
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ASparseVector extends ASizedVector implements ISparse {

	protected ASparseVector(int length) {
		super(length);
	}

	/**
	 * Returns the number of non-sparse elements in the sparse vector.
	 * @return
	 */
	public abstract int nonSparseElementCount();
	
	/**
	 * Returns the non-sparse values as a compacted vector view
	 * @return
	 */
	public abstract AVector nonSparseValues();
	
	@Override
	public abstract Index nonSparseIndex();
	
	/**
	 * Returns true iff the sparse vector contains the index i 
	 * @param i
	 * @return
	 */
	public abstract boolean includesIndex(int i);
	
	// ================================================
	// Superclass methods that must be overridden
	// (superclass implementation is bad for sparse arrays)
	
	@Override
	public void copyTo(int offset, double[] destData, int destOffset, int length) {
		Arrays.fill(destData, destOffset, destOffset+length, 0.0);
		addToArray(offset, destData, destOffset, length);
	}
	
	@Override
	public boolean isZero() {
		return nonZeroCount()==0L;
	}
	
	@Override
	public boolean isView() {
		return false;
	}
		
	// ================================================
	// standard implementations
	
	@Override
	public double dotProduct(AVector v) {
		double result=0.0;
		Index ni=nonSparseIndex();
		for (int i=0; i<ni.length(); i++) {
			int ii=ni.get(i);
			result+=unsafeGet(ii)*v.unsafeGet(ii);
		}		
		return result;
	}
	
	@Override
	public final boolean isSparse() {
		return true;
	}
	
	@Override
	public void add(AVector v) {
		if (v instanceof ASparseVector) {
			add((ASparseVector)v);
			return;
		}
		super.add(v);
	}
	
	@Override
	public void addMultiple(AVector src, double factor) {
		add(src.multiplyCopy(factor));
	}

	public abstract void add(ASparseVector v);
	
	@Override
	public List<Double> getSlices() {
		// we prefer a ListWrapper for sparse vectors since it is O(1) to create.
		// downside: causes boxing on individual element accesses
		return new ListWrapper(this);
	}
	
	@Override
	public double elementProduct() {
		int n=nonSparseElementCount();
		if (n<length) return 0.0;
		return nonSparseValues().elementProduct();
	}
	
	@Override
	public ASparseVector sparse() {
		return this;
	}
	
	public boolean equals(ASparseVector v) {
		if (v==this) return true;
		if (v.length!=length) return false;
		
		Index ni=nonSparseIndex();
		for (int i=0; i<ni.length(); i++) {
			int ii=ni.get(i);
			if (unsafeGet(ii)!=v.unsafeGet(ii)) return false;
		}
		
		Index ri=v.nonSparseIndex();
		for (int i=0; i<ri.length(); i++) {
			int ii=ri.get(i);
			if (unsafeGet(ii)!=v.unsafeGet(ii)) return false;
		}
		
		return true;
	}
	
	@Override
	public double[] toDoubleArray() {
		double[] data=new double[length];
		addToArray(data,0);
		return data;
	}
	
	@Override
	public boolean equals(AVector v) {
		if (v instanceof ASparseVector) {
			return equals((ASparseVector)v);
		}
		return super.equals(v);
	}

	@Override
	public boolean hasUncountable() {
		return nonSparseValues().hasUncountable();
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        Index ni = nonSparseIndex();
        double result = 0;
        for(int i=0; i<ni.length(); i++) {
            int ii = ni.get(i);
            result += Math.pow(unsafeGet(ii), p);
        }
        return result;
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        Index ni = nonSparseIndex();
        double result = 0;
        for(int i=0; i<ni.length(); i++) {
            int ii = ni.get(i);
            result += Math.pow(Math.abs(unsafeGet(ii)), p);
        }
        return result;
    }
}
