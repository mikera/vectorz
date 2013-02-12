package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.util.VectorzException;

/**
 * Indexed sparse vector. Mutable only in the elements included in the index
 * @author Mike
 *
 */
public class SparseIndexedVector extends ASparseVector {
	private static final long serialVersionUID = 750093598603613879L;

	final int length;
	final Index index;
	final double[] data;
	
	public SparseIndexedVector(int length, Index index, double[] data) {
		assert(index.length()==data.length);
		this.length=length;
		this.index=index;
		this.data=data;
	}
	
	public SparseIndexedVector(int length, Index index, AVector data) {
		this.length=length;
		this.index=index;
		this.data=new double[index.length()];
		data.copyTo(this.data, 0);
	}
	
	@Override
	public int nonSparseElementCount() {
		return data.length;
	}
	
	@Override
	public int length() {
		return length;
	}
	
	/**
	 * Looks up an index in the index vector, returning its position or -1 if not found
	 * @param i
	 * @return
	 */
	public int indexPosition(int i) {
		int min=0; int max=data.length;
		while (min<max) {
			int mid=(min+max)>>1;
			int mi=index.data[mid];
			if (i==mi) return mid;
			if (i<mi) {
				max=mid;
			} else {
				min=mid+1;
			}
		}
		return -1;
	}
	
	@Override
	public double get(int i) {
		int ip=indexPosition(i);
		if (ip<0) return 0.0;
		return data[ip];
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public void set(int i, double value) {
		int ip=indexPosition(i);
		if (ip<0) {
			throw new VectorzException("Can't set SparseIndexedVector at non-indexed position: "+i);
		}
		data[ip]=value;
	}

}
