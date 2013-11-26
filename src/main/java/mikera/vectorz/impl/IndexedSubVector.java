package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Vector that addresses elements indexed into another source vector
 * @author Mike
 *
 */
public final class IndexedSubVector extends AIndexedVector {
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
		return data.unsafeGet(indexes[i]);
	}

	@Override
	public void set(int i, double value) {
		data.unsafeSet(indexes[i],value);
	}
	
	@Override
	public double unsafeGet(int i) {
		return data.unsafeGet(indexes[i]);
	}

	@Override
	public void unsafeSet(int i, double value) {
		data.unsafeSet(indexes[i],value);
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
	public IndexedSubVector exactClone() {
		return IndexedSubVector.wrap(data.exactClone(), indexes.clone());
	}
	
	@Override
	public void validate() {
		super.validate();
		int slen=data.length();
		for (int i=0; i<length; i++) {
			if ((indexes[i]<0)||(indexes[i]>=slen)) throw new VectorzException("Indexes out of range");
		}
	}
}
