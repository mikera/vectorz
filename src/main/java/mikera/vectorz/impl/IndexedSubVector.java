package mikera.vectorz.impl;

import java.util.Arrays;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * Vector that addresses elements indexed into another source vector.
 * 
 * Indexed elements are not necessarily distinct: two elements n the vector can refer to the same source element.
 * 
 * @author Mike
 */
public final class IndexedSubVector extends BaseIndexedVector {
	private static final long serialVersionUID = -1411109918028367417L;

	private final AVector source;
	
	private IndexedSubVector(AVector source, int[] indexes) {
		super(indexes);
		this.source=source;
	}
	
	public static IndexedSubVector wrap(AVector source, int[] indexes) {
		return new IndexedSubVector(source,indexes);
	}
	
	@Override
	public void addToArray(double[] dest, int offset) {
		for (int i=0; i<length; i++) {
			dest[offset+i]+=source.unsafeGet(indexes[i]);
		}
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		source.getElements(dest,offset,indexes);
	}
	
	@Override
	public AVector selectView(int... inds) {
		int[] ci=IntArrays.select(indexes,inds);
		return replaceIndex(ci);
	}

	@Override
	public double get(int i) {
		return source.unsafeGet(indexes[i]);
	}

	@Override
	public void set(int i, double value) {
		source.unsafeSet(indexes[i],value);
	}
	
	@Override
	public double unsafeGet(int i) {
		return source.unsafeGet(indexes[i]);
	}

	@Override
	public void unsafeSet(int i, double value) {
		source.unsafeSet(indexes[i],value);
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		if ((offset<0)||((offset+length)>this.length)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, offset, length));
		}
		if (length==0) return Vector0.INSTANCE;
		if (length==this.length) return this;

		int[] newIndexes=Arrays.copyOfRange(indexes, offset, offset+length);
		return replaceIndex(newIndexes);
	}
	
	@Override 
	public IndexedSubVector exactClone() {
		return IndexedSubVector.wrap(source.exactClone(), indexes.clone());
	}
	
	@Override
	public void validate() {
		super.validate();
		int slen=source.length();
		for (int i=0; i<length; i++) {
			if ((indexes[i]<0)||(indexes[i]>=slen)) throw new VectorzException("Indexes out of range");
		}
	}

	@Override
	public void addAt(int i, double v) {
		source.addAt(indexes[i], v);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i]*unsafeGet(i);
		}
		return result;
	}
	
	@Override 
	public AVector join(AVector v) {
		if (v instanceof IndexedSubVector) {
			IndexedSubVector iv=(IndexedSubVector) v;
			if (iv.source==source) {
				return replaceIndex(IntArrays.concat(indexes,iv.indexes));
			}
		} 
		return super.join(v);
	}

	@Override
	protected IndexedSubVector replaceIndex(int[] newIndices) {
		return new IndexedSubVector(source,newIndices);
	}
}
