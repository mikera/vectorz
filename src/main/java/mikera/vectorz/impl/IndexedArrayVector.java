package mikera.vectorz.impl;

import java.util.Arrays;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * Vector that addresses elements indexed into double[] array.
 * 
 * Useful for sub-selections views of array data
 * 
 * @author Mike
 */
public final class IndexedArrayVector extends BaseIndexedVector {
	private static final long serialVersionUID = 3220750778637547658L;

	private final double[] data;
	
	private IndexedArrayVector(double[] source, int[] indexes) {
		super(indexes);
		this.data=source;
	}
	
	/**
	 * Wraps the specified indexes into a double[] array as an IndexedArrayVector
	 * 
	 * The length of the vector will equal the number of indices provided.
	 * 
	 * @param data
	 * @param indexes
	 * @return
	 */
	public static IndexedArrayVector wrap(double[] data, int[] indexes) {
		return new IndexedArrayVector(data,indexes);
	}

	@Override
	public final double get(int i) {
		return data[indexes[i]];
	}
	
	@Override
	public final double unsafeGet(int i) {
		return data[indexes[i]];
	}
	
	@Override
	public IndexedArrayVector selectView(int... inds) {
		int[] ci=IntArrays.select(indexes,inds);
		return replaceIndex(ci);
	}

	@Override
	public final void set(int i, double value) {
		data[indexes[i]]=value;
	}
	
	@Override
	public final void unsafeSet(int i, double value) {
		data[indexes[i]]=value;
	}
	
	@Override
	public void addAt(int i, double value) {
		data[indexes[i]]+=value;
	}
	
	@Override
	public void applyOp(Op op) {
		for (int i=0; i<length; i++) {
			data[indexes[i]]=op.apply(data[indexes[i]]);
		}
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		int len=checkRange(offset,length);
		if (length==0) return Vector0.INSTANCE;
		if (length==len) return this;

		int end=offset+length;
		int[] newIndexes=Arrays.copyOfRange(indexes, offset, end);
		return replaceIndex(newIndexes);
	}
	
	@Override
	public ArrayIndexScalar slice(int i) {
		return ArrayIndexScalar.wrap(data,indexes[i]);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		for (int i=0; i<length; i++) {
			dest[offset+i]=data[indexes[i]];
		}
	}
	
	@Override
	public void copyTo(int offset, double[] dest, int destOffset, int length) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]=unsafeGet(i+offset);
		}
	}
	
	@Override
	public void copyTo(int offset, double[] dest, int destOffset, int length, int stride) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i*stride]=unsafeGet(i+offset);
		}
	}
	
	@Override
	public void addToArray(double[] dest, int offset) {
		for (int i=0; i<length; i++) {
			dest[offset+i]+=data[indexes[i]];
		}
	}
	
	@Override
	public void addMultipleToArray(double factor, int offset, double[] dest, int destOffset, int length) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]+=data[indexes[offset+i]]*factor;
		}
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=unsafeGet(i)*data[offset+i];
		}
		return result;
	}
	
	@Override
	public double dotProduct(double[] data, int offset, int stride) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=unsafeGet(i)*data[offset];
			offset+=stride;
		}
		return result;
	}

	@Override 
	public IndexedArrayVector exactClone() {
		return IndexedArrayVector.wrap(data.clone(), indexes.clone());
	}
	
	@Override 
	public AVector join(AVector v) {
		if (v instanceof IndexedArrayVector) {
			IndexedArrayVector iv=(IndexedArrayVector) v;
			if (iv.data==data) {
				return replaceIndex(IntArrays.concat(indexes,iv.indexes));
			}
		} 
		return super.join(v);
	}

	@Override
	protected IndexedArrayVector replaceIndex(int[] newIndices) {
		return new IndexedArrayVector(data,newIndices);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		for (int i=0; i<length; i++) {
			if (data[offset+i]!=this.data[indexes[i]]) return false;
		}
		return true;
	}
	
	@Override
	public void validate() {
		if (length!=indexes.length) throw new VectorzException("Invalid index length");
		super.validate();
	}
}
