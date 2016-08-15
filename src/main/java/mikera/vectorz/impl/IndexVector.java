package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * A constrained immutable vector implementation wrapping an integer Index
 * 
 * @author Mike
 */
@Deprecated @SuppressWarnings("serial")
public class IndexVector extends ASizedVector {
	private final Index index; 
	
	private IndexVector(Index index) {
		super(index.length());
		this.index=index;
	}

	public static IndexVector of(int... values) {
		return new IndexVector(Index.of(values));
	}
	
	public IndexVector ofDoubles(double... values) {
		return new IndexVector(Index.wrap(IntArrays.create(values)));
	}
	
	/**
	 * Creates an IndexVector wrapping the given immutable index
	 * 
	 * WARNING: Index will be used as internal storage for the IndexVector
	 * @param a
	 * @return
	 */
	public static IndexVector wrap(Index a) {
		return new IndexVector(a);		
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
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public boolean isElementConstrained() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		IntArrays.copyIntsToDoubles(index.data,0,data,offset,length);
	}
	
	@Override
	public boolean isMutable() {
		return false;
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

	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i]*index.unsafeGet(i);
		}
		return result;
	}

}
