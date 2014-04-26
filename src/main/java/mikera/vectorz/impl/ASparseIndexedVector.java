package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;


/**
 * Base class containing common implementations for sparse indexed vectors
 * @author Mike
 *
 */
public abstract class ASparseIndexedVector extends ASparseVector {
	private static final long serialVersionUID = -8106136233328863653L;
	
	public ASparseIndexedVector(int length) {
		super(length);
	}
	
	abstract double[] internalData();
	
	abstract Index internalIndex();
	
	int[] internalIndexArray() {
		return internalIndex().data;
	}
	
	@Override
	public boolean isZero() {
		return DoubleArrays.isZero(internalData());
	}
	
	@Override
	public boolean isRangeZero(int start, int length) {
		int end=start+length;
		Index index=internalIndex();
		double[] data=internalData();
		int si=index.seekPosition(start);
		int di=index.seekPosition(end);
		for (int i=si; i<di; i++) {
			if (data[i]!=0.0) return false;
		}
		return true;
	}
	
	@Override
	public double elementSum() {
		return DoubleArrays.elementSum(internalData());
	}
	
	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(internalData());
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		double[] tdata=this.internalData();
		int[] ixs=internalIndex().data;
		for (int j=0; j<tdata.length; j++) {
			result+=tdata[j]*data[offset+ixs[j]];
		}
		return result;
	}
	
	@Override
	public double dotProduct(AVector v) {
		if (v instanceof ADenseArrayVector) return dotProduct((ADenseArrayVector)v);
		double result=0.0;
		double[] data=internalData();
		int[] ixs=internalIndex().data;
		for (int j=0; j<data.length; j++) {
			result+=data[j]*v.unsafeGet(ixs[j]);
		}
		return result;
	}
	
	@Override
	public double dotProduct(ADenseArrayVector v) {
		double[] array=v.getArray();
		int offset=v.getArrayOffset();
		return dotProduct(array,offset);
	}
	
	@Override
	public int[] nonZeroIndices() {
		int n=(int)nonZeroCount();
		double[] data=internalData();
		Index index=internalIndex();
		int[] ret=new int[n];
		int di=0;
		for (int i=0; i<data.length; i++) {
			if (data[i]!=0.0) ret[di++]=index.get(i);
		}
		if (di!=n) throw new VectorzException("Invalid non-zero index count. Maybe concurrent modification of vector?");
		return ret;
	}
	
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		assert((offset>=0)&&(offset+length<=this.length));
		double[] data=internalData();
		Index index=internalIndex();
		
		int start=index.seekPosition(offset);
		int[] ixs=index.data;
		int dataLength=data.length;
		for (int j=start; j<dataLength; j++) {
			int di=ixs[j]-offset; // index relative to offset
			if (di>=length) return;
			array[arrayOffset+di]+=data[j];
		}
	}
	
	@Override
	public void addToArray(double[] dest, int offset) {
		double[] data=internalData();
		int[] ixs=internalIndexArray();
		int dataLength=data.length;
		for (int i=0; i<dataLength; i++) {
			dest[offset+ixs[i]]+=data[i];
		}
	}
	
	@Override
	public void addToArray(double[] dest, int offset, int stride) {
		double[] data=internalData();
		int[] ixs=internalIndexArray();
		for (int i=0; i<data.length; i++) {
			dest[offset+ixs[i]*stride]+=data[i];
		}
	}
	
	@Override
	public boolean equalsArray(double[] ds, int offset) {
		double[] data=internalData();
		int[] ixs=internalIndexArray();
		int n=data.length;
		if (n==0) return DoubleArrays.isZero(ds, offset, length);
		int di=0;
		int i=0;
		while (di<n) {
			int t=ixs[di];
			while (i<t) {
				if (ds[offset+i]!=0.0) return false;
				i++;
			}
			if (ds[offset+t]!=data[di]) return false;
			i++;
			di++;
		}
		// check any remaining segment of array
		return DoubleArrays.isZero(ds, offset+i, length-i);
	}
	
	/**
	 * Create a clone of this sparse indexed vector including the new indexes specified
	 * Intended to allow fast subsequent modification
	 */
	public final SparseIndexedVector cloneIncludingIndices(int [] ixs) {
		Index index=internalIndex();
		int[] nixs = IntArrays.mergeSorted(index.data,ixs);
		double[] data=internalData();
		int nl=nixs.length;
		double[] ndata=new double[nl];
		int si=0;
		for (int i=0; i<nl; i++) {
			int z=index.data[si];
			if (z==nixs[i]) {
				ndata[i]=data[si];
				si++; 
				if (si>=data.length) break;
			}
		}
		return SparseIndexedVector.wrap(length, nixs, ndata);
	}
	
	/**
	 * Copy only the sparse values in this vector to a target array. Other values in the target array are unchanged
	 * @param array
	 * @param offset
	 */
	protected final void copySparseValuesTo(double[] array, int offset) {
		Index index=internalIndex();
		int[] ixs = index.data;
		double[] data=internalData();
		for (int i=0; i<data.length; i++) {
			int di=ixs[i];
			array[offset+di]=data[i];
		}	
	}
}
