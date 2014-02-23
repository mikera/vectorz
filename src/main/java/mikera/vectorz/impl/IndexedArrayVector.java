package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Vector that addresses elements indexed into double[] array
 * @author Mike
 *
 */
public final class IndexedArrayVector extends BaseIndexedVector {
	private static final long serialVersionUID = -1411109918028367417L;

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
		for (int i=0; i<length; i++) {
			newIndexes[i]=indexes[offset+i];
		}
		return wrap(this.data,newIndexes);
	}

	@Override 
	public IndexedArrayVector exactClone() {
		return IndexedArrayVector.wrap(data.clone(), indexes.clone());
	}
}
