package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * A constrained vector implementation wrapping an integer Index
 * @author Mike
 */
@SuppressWarnings("serial")
public class IndexVector extends AConstrainedVector {
	private final Index index; 
	private final int length;
	
	private IndexVector(Index index) {
		this.index=index;
		length=index.length();
	}

	public static IndexVector of(int... values) {
		return new IndexVector(Index.of(values));
	}
	
	public IndexVector ofDoubles(double... values) {
		return new IndexVector(Index.wrap(IntArrays.create(values)));
	}
	
	public static IndexVector wrap(Index a) {
		return new IndexVector(a);		
	}
	
	@Override
	public int length() {
		return length;
	}

	@Override
	public double get(int i) {
		return index.get(i);
	}
	
	@Override
	public double unsafeGet(int i) {
		return index.unsafeGet(i);
	}

	@Override
	public void set(int i, double value) {
		int v=(int)value;
		if (v==value) {
			index.set(i, v);
		} else {
			throw new IllegalArgumentException("Can't convert to an integer index value: "+value);
		}
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		int v=(int)value;
		index.set(i, v);
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		for (int i=0; i<length; i++) {
			data[offset+i]=index.unsafeGet(i);
		}
	}

	@Override
	public AVector exactClone() {
		return new IndexVector(index.clone());
	}
	
	@Override
	public void validate() {
		if (length!=index.length()) throw new VectorzException("Incorrect index length!!");
		super.validate();
	}

}
