package mikera.vectorz.impl;

import java.util.Arrays;
import java.util.List;

import mikera.arrayz.ISparse;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.util.VectorzException;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.Vectorz;


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
	 * 
	 * This will be greater or equal to the number of non-zero elements
	 * 
	 * @return
	 */
	public abstract int nonSparseElementCount();
	
	/**
	 * Returns the non-sparse elements as a compacted vector
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

    /**
     * Returns a vector after replacing all elements with absolute values less-than-or-equal to precision with zeros.
     * 
     * May return either this vector or a new vector. If this vector is return, it may have been mutated.
     * 
     * @param precision
     * @return
     */
	public ASparseVector roundToZero(double precision) {
        throw new VectorzException(ErrorMessages.notYetImplemented());
    }
    
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
	public double distanceSquared(AVector v) {
		AVector d=this.subCopy(v);
		return d.elementSquaredSum();
	}
	
	@Override
	public double dotProduct(AVector v) {
		checkSameLength(v);
		double result=0.0;
		Index ni=nonSparseIndex();
		for (int i=0; i<ni.length(); i++) {
			int ii=ni.get(i);
			result+=unsafeGet(ii)*v.unsafeGet(ii);
		}		
		return result;
	}
	
	@Override
	public final double dotProduct(ADenseArrayVector v) {
		checkSameLength(v);
		double[] array=v.getArray();
		int offset=v.getArrayOffset();
		return dotProduct(array,offset);
	}
	
	@Override
	public AVector innerProduct(AMatrix m) {
		int cc=m.columnCount();
		int rc=m.rowCount();
		checkLength(rc);
		AVector r=Vectorz.createSparseMutable(cc);
		Index ni=nonSparseIndex();
		for (int i=0; i<ni.length(); i++) {
			int ti=ni.get(i);
			double v=unsafeGet(ti);
			if (v!=0.0) r.addMultiple(m.getRow(ti),v);
		}		
		return r;
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

    //    public abstract void sub(ASparseVector v);
	
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
	
	@Override
	public AVector clone() {
		// TODO: figure out a better heuristic?
		if ((length<20)||(nonSparseElementCount()>(elementCount()*0.25))) return super.clone();
		return SparseIndexedVector.create(this);
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
	public long nonZeroCount() {
		return nonSparseValues().nonZeroCount();
	}
	
	@Override
	public boolean equals(AVector v) {
		if (v instanceof ASparseVector) {
			return equals((ASparseVector)v);
		}
		
		if (v.length()!=length) return false;
		Index ni=nonSparseIndex();
		int n=ni.length();
		AVector nv=nonSparseValues();
		int offset=0;
		for (int i=0; i<n; i++) {
			int ii=ni.get(i);
			if (!v.isRangeZero(offset, ii-offset)) return false;
			if (nv.unsafeGet(i)!=v.unsafeGet(ii)) return false;
			offset=ii+1;
		}
		return v.isRangeZero(offset,length-offset);
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
        return nonSparseValues().elementPowSum(p);
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
    	return nonSparseValues().elementAbsPowSum(p);
    }
}
