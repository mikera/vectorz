package mikera.vectorz.impl;

import java.util.Arrays;
import java.util.List;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op2;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;


/**
 * Abstract base class for Sparse vector implementations
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ASparseVector extends ASizedVector implements ISparseVector {

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
	
	@Override
	public abstract AVector nonSparseValues();
	
	@Override
	public abstract Index nonSparseIndex();
	
	@Override
	public double visitNonZero(IndexedElementVisitor elementVisitor) {
		Index ix=nonSparseIndex();
		int cnt=ix.length();
		if (cnt==0) return 0.0;
		AVector vs=nonSparseValues();
		for (int k=0; k<cnt; k++) {
			int i=ix.get(k);
			double v=vs.unsafeGet(k);
			v=elementVisitor.visit(i, v);
			if (v!=0.0) return v;
		}
		return 0.0;
	}
	
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
	public void copyTo(int offset, double[] destData, int destOffset, int length, int stride) {
		checkRange(offset,length);
		SparseIndexedVector sv=toSparseIndexedVector();
		Vector svs=sv.nonSparseValues();
		if (svs.length==0) {
			for (int i=0; i<length; i++) {
				destData[destOffset+i*stride]=0.0;
			}
		} else {
			int[] ixs=sv.nonSparseIndex().data;
			int ii=0;
			while (ixs[ii]<offset) ii++;
			int ind=ixs[ii];
			for (int i=0; i<length; i++) {
				int di=destOffset+i*stride;
				if (ind==(offset+i)) {
					destData[di]=svs.unsafeGet(ii);
					ii++;
					ind=(ii<ixs.length)?ixs[ii]:0; // set to 0 if no more indexes to access
				} else {
					destData[di]=0.0;
				}
			}
		}
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
	public double reduce(Op2 op, double init) {
		return toSparseIndexedVector().reduce(op,init);
	}

	@Override
	public double reduce(Op2 op) {
		return toSparseIndexedVector().reduce(op);
	}
	
	@Override
	public final AVector reduceSlices(Op2 op, double init) {
		// can't do anything if op won't result in sparse vector
		if (op.isStochastic()||(op.apply(init,0.0)!=0.0)) return super.reduceSlices(op, init);
		
		// TODO: replace with applyOpCopy when we have it
		Index ni=nonSparseIndex();
		AVector result=clone();
		int len=ni.length();
		for (int i=0; i<len; i++) {
			int ix=ni.get(i);
			result.unsafeSet(ix,op.apply(init,result.unsafeGet(ix)));
		}
		return result;
	}

	@Override
	public double distanceSquared(AVector v) {
		AVector d=this.subCopy(v);
		return d.elementSquaredSum();
	}
	
	@Override
	public abstract double dotProduct(AVector v);
	
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
	public double density() {
		return ((double)nonZeroCount())/elementCount();
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
    
    @Override
    public double elementSum() {
    	return nonSparseValues().elementSum();
    }
    
    @Override
    public double elementSquaredSum() {
    	return nonSparseValues().elementSquaredSum();
    }
    
    @Override
	public double distanceL1(AVector v) {
		AVector t=this.subCopy(v).mutable();
		t.abs();
		return t.elementSum();
	}
	
	@Override
	public double distanceLinf(AVector v) {
		AVector t=this.subCopy(v);
		return t.maxAbsElement();
	}

	@Override
	public SparseIndexedVector toSparseIndexedVector() {
		return SparseIndexedVector.create(this);
	}
}
