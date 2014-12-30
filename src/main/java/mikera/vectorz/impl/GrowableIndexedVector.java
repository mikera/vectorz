package mikera.vectorz.impl;

import mikera.indexz.GrowableIndex;
import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.GrowableVector;
import mikera.vectorz.util.VectorzException;

/**
 * Implementation for a sparse indexed vector that can accumulate index/value pairs efficiently (in order) 
 * @author Mike
 */
public class GrowableIndexedVector extends ASparseVector {
	private static final long serialVersionUID = 441979517032171392L;

	private final GrowableIndex index;
	private final GrowableVector data;
	
	private GrowableIndexedVector(int length, GrowableIndex index, GrowableVector data) {
		super(length);
		this.index=index;
		this.data=data;
	}
	
	private GrowableIndexedVector(int length) {
		super(length);
		this.index=new GrowableIndex();
		this.data=new GrowableVector();
	}
	
	public static GrowableIndexedVector createLength(int len) {
		return new GrowableIndexedVector(len);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		int ix=index.indexPosition(i);
		if (ix<0) return 0.0;
		return data.get(ix);
	}
	
	public void append(int i, double value) {
		index.checkedAppend(i);
		data.append(value);
	}

	@Override
	public void set(int i, double value) {
		int ix=index.indexPosition(i);
		if (ix<0) throw new UnsupportedOperationException("Can't set at index: "+i);
		data.unsafeSet(ix, value);
	}
	
	@Override
	public boolean isFullyMutable() {
		// TODO: consider making fully mutable?
		return false;
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return toSparseIndexedVector().dotProduct(data,offset);
	}

	public SparseIndexedVector toSparseIndexedVector() {
		return SparseIndexedVector.create(length, Index.create(index), data.toDoubleArray());
	}

	@Override
	public AVector exactClone() {
		return new GrowableIndexedVector(length,index.exactClone(), data.exactClone());
	}
	
	@Override
	public SparseIndexedVector sparseClone() {
		return toSparseIndexedVector();
	}

	@Override
	public int nonSparseElementCount() {
		return index.length();
	}

	@Override
	public AVector nonSparseValues() {
		return data;
	}

	@Override
	public Index nonSparseIndex() {
		return Index.create(index);
	}

	@Override
	public boolean includesIndex(int i) {
		return index.indexPosition(i)>=0;
	}

	@Override
	public void add(ASparseVector v) {
		Index ix=v.nonSparseIndex();
		AVector vs=v.nonSparseValues();
		int n=ix.length();
		for (int i=0; i<n; i++) {
			addAt(ix.get(i),vs.get(i));
		}
	}
	
	@Override
	public void validate() {
		if (index.length()!=data.length()) throw new VectorzException("Mismatched index and data length!");
	}
	
}
