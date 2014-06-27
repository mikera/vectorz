package mikera.vectorz.impl;

import java.util.Arrays;

import mikera.vectorz.AVector;
import mikera.vectorz.util.IntArrays;

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
	
	public static IndexedArrayVector wrap(double[] data, int[] indexes) {
		return new IndexedArrayVector(data,indexes);
	}

	@Override
	public double get(int i) {
		return data[indexes[i]];
	}
	
	@Override
	public double unsafeGet(int i) {
		return data[indexes[i]];
	}
	
	@Override
	public IndexedArrayVector selectView(int... inds) {
		int[] ci=IntArrays.select(indexes,inds);
		return new IndexedArrayVector(data,ci);
	}

	@Override
	public void set(int i, double value) {
		data[indexes[i]]=value;
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		data[indexes[i]]=value;
	}
	
	@Override
	public void addAt(int i, double value) {
		data[indexes[i]]+=value;
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		int len=checkRange(offset,length);
		if (length==0) return Vector0.INSTANCE;
		if (length==len) return this;

		int end=offset+length;
		int[] newIndexes=Arrays.copyOfRange(indexes, offset, end);
		return wrap(this.data,newIndexes);
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
	public IndexedArrayVector exactClone() {
		return IndexedArrayVector.wrap(data.clone(), indexes.clone());
	}
}
