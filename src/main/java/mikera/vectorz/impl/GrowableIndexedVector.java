package mikera.vectorz.impl;

import java.util.Iterator;

import mikera.indexz.GrowableIndex;
import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.GrowableVector;
import mikera.vectorz.Tools;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Implementation for a sparse indexed vector that can accumulate index/value pairs efficiently (in order) 
 * 
 * Length grows if elements are added beyond the end of the vector
 * 
 * @author Mike
 */
public class GrowableIndexedVector extends AVector implements ISparseVector {
	private static final long serialVersionUID = 441979517032171392L;

	private int length;
	private final GrowableIndex index;
	private final GrowableVector data;
	
	private GrowableIndexedVector(int length, GrowableIndex index, GrowableVector data) {
		this.length=length;
		this.index=index;
		this.data=data;
	}
	
	private GrowableIndexedVector(int length) {
		this.length=length;
		this.index=new GrowableIndex();
		this.data=new GrowableVector();
	}
	
	private GrowableIndexedVector() {
		this(0);
	}
	
	/**
	 * Creates a GrowableIndexedVector from the specified Iterable object
	 * @param iterable An Iterable containing java.lang.Number instances
	 * @return
	 */
	public static GrowableIndexedVector create(Iterable<?> iterable) {
		GrowableIndexedVector v=new GrowableIndexedVector();
		for (Object o: iterable) {
			v.length++;
			double d=Tools.toDouble(o);
			if (d!=0.0) {
				v.append(v.length-1,d);
			}
		}
		return v;
	}
	
	/**
	 * Creates a GrowableIndexedVector from the specified Iterable object
	 * @param iterable An Iterable containing java.lang.Number instances
	 * @return
	 */
	public static GrowableIndexedVector create(AVector v) {
		if (v instanceof ISparseVector) {
			return create((ISparseVector) v);
		}
		return new GrowableIndexedVector(v.length(),GrowableIndex.wrap(v.nonZeroIndices().clone()),GrowableVector.wrap(v.nonZeroValues().clone()));
	}
	
	public static GrowableIndexedVector create(ISparseVector v) {
		return new GrowableIndexedVector(v.length(),GrowableIndex.create(v.nonSparseIndex()),GrowableVector.create(v.nonSparseValues()));
	}

	/**
	 * Creates a GrowableIndexedVector from the specified Iterator object
	 * @param iterator An Iterator over java.lang.Number instances
	 * @return
	 */
	public static GrowableIndexedVector create(Iterator<?> iterator) {
		GrowableIndexedVector v=new GrowableIndexedVector();
		while (iterator.hasNext()) {
			v.length++;
			double d=Tools.toDouble(iterator.next());
			if (d!=0.0) {
				v.append(v.length-1,d);
			}
		}
		return v;
	}
	
	/**
	 * Creates a zero-filled GrowableIndexedVector with the specified initial length
	 * @param len
	 * @return
	 */
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
	
	/**
	 * Adds a value to this GrawableIndexedVector at the specified position.
	 * Grows the vector if required.
	 * @param i
	 * @param value
	 */
	public void append(int i, double value) {
		index.checkedAppend(i);
		data.append(value);
		if (i>=length) length=i+1;
	}

	@Override
	public void set(int i, double value) {
		if (i<0) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		if (i>=length) {
			append(i,value);
			return;
		}
		int ix=index.indexPosition(i);
		if (ix<0) {
			int pos=index.include(i);
			if (index.length()>data.length()) {
				data.insert(pos,value);
			} else {
				data.set(pos,value);
			}
		} else {
			data.unsafeSet(ix, value);
		}
	}
	
	public void append(double v) {
		set(length,v);
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return toSparseIndexedVector().dotProduct(data,offset);
	}

	@Override
	public SparseIndexedVector toSparseIndexedVector() {
		return SparseIndexedVector.wrap(length, Index.create(index), data.toDoubleArray());
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
	public SparseIndexedVector sparse() {
		// we do this to ensure a "full" implementation is used
		return toSparseIndexedVector();
	}

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

	public boolean includesIndex(int i) {
		return index.indexPosition(i)>=0;
	}

	@Override
	public void validate() {
		int il=index.length();
		int dl=data.length();
		if (il!=dl) throw new VectorzException("Mismatched index and data length!");
		if (il>length) throw new VectorzException("Index larger than length!");
		if (dl>length) throw new VectorzException("Data larger than length!");
		if (index.get(il-1)>=length) throw new VectorzException("Last element beyond length!");
	}

	@Override
	public int length() {
		return length;
	}
	
	/**
	 * Sets the length of this vector. 
	 * @return
	 */
	public void setLength(int length) {
		if ((length>this.length)||(index.last()<length)) {
			this.length=length;
			return;
		}
	}

	@Override
	public boolean equalsArray(double[] data, int offset) {
		return toSparseIndexedVector().equalsArray(data,offset);
	}

	@Override
	public double visitNonZero(IndexedElementVisitor elementVisitor) {
		return toSparseIndexedVector().visitNonZero(elementVisitor);
	}
}
