package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;
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
	public IndexedArrayVector selectView(int... inds) {
		int[] ci=IntArrays.select(indexes,inds);
		return new IndexedArrayVector(data,ci);
	}

	@Override
	public void set(int i, double value) {
		data[indexes[i]]=value;
	}
	
	@Override
	public void addAt(int i, double value) {
		data[indexes[i]]+=value;
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		if ((offset<0)||((offset+length)>this.length)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, offset, length));
		}
		if (length==0) return Vector0.INSTANCE;
		if (length==this.length) return this;

		int[] newIndexes=new int[length];
		System.arraycopy(indexes, offset, newIndexes, 0, length);
		return wrap(this.data,newIndexes);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		for (int i=0; i<length; i++) {
			dest[offset+i]=data[indexes[i]];
		}
	}

	@Override 
	public IndexedArrayVector exactClone() {
		return IndexedArrayVector.wrap(data.clone(), indexes.clone());
	}
}
