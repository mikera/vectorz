package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Vector that addresses elements indexed into another source vector
 * @author Mike
 *
 */
public final class IndexedSubVector extends IndexedVector {
	private static final long serialVersionUID = -1411109918028367417L;

	private final AVector data;
	
	private IndexedSubVector(AVector source, int[] indexes) {
		super(indexes);
		this.data=source;
	}
	
	public static IndexedSubVector wrap(AVector source, int[] indexes) {
		return new IndexedSubVector(source,indexes);
	}

	@Override
	public double get(int i) {
		return data.get(indexes[i]);
	}

	@Override
	public void set(int i, double value) {
		data.set(indexes[i],value);
	}
	
	@Override
	public IndexedSubVector subVector(int offset, int length) {
		if (offset<0) throw new IndexOutOfBoundsException("Start Index: "+offset);
		if ((offset+length)>this.length) throw new IndexOutOfBoundsException("End Index: "+(offset+length));

		int[] newIndexes=new int[length];
		for (int i=0; i<length; i++) {
			newIndexes[i]=indexes[offset+i];
		}
		return wrap(this.data,newIndexes);
	}

}
