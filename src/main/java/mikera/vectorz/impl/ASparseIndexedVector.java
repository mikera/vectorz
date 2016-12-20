package mikera.vectorz.impl;

import java.util.Arrays;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
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
	public boolean includesIndex(int i) {
		return internalIndex().indexPosition(i)>=0;
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
    public void multiply(double factor) {
        double[] data = internalData();
		for (int i = 0; i < data.length; i++) {
			unsafeSet(i,unsafeGet(i)*factor);
		}	
	}
	
	@Override
	public double elementSquaredSum() {
		return DoubleArrays.elementSquaredSum(internalData());
	}
	
	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(internalData());
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double[] tdata=this.internalData();
		int[] ixs=internalIndex().data;
		double result=0.0;
		for (int j=0; j<tdata.length; j++) {
			result+=tdata[j]*data[offset+ixs[j]];
		}
		return result;
	}
	
	@Override
	public double dotProduct(double[] data, int offset, int stride) {
		double[] tdata=this.internalData();
		int[] ixs=internalIndex().data;
		double result=0.0;
		for (int j=0; j<tdata.length; j++) {
			result+=tdata[j]*data[offset+ixs[j]*stride];
		}
		return result;
	}
	
	@Override
	public AVector innerProduct(AMatrix a) {
		// we assume sufficient sparsity to make specialised implementation worthwhile?
		// TODO should we go in a different order and calculate via non-zero elements in this? (i.e. avoid iterating over all columns of a)
		int cc=a.columnCount();
		GrowableIndexedVector dest=GrowableIndexedVector.createLength(cc);
		for (int i=0; i<cc; i++) {
			double v=this.dotProduct(a.getColumn(i));
			if (v!=0.0) dest.append(i, v);
		}
		return dest.toSparseIndexedVector();
	}
	
	@Override
	public final double dotProduct(AVector v) {
		if (v instanceof ADenseArrayVector) return dotProduct((ADenseArrayVector)v);
		if (v instanceof ASparseVector) return dotProduct((ASparseVector)v);
		
		// no quick implementation, so use indexed gets
		double result=0.0;
		double[] data=internalData();
		int[] ixs=internalIndexArray();
		for (int j=0; j<data.length; j++) {
			result+=data[j]*v.unsafeGet(ixs[j]);
		}
		return result;
	}
	
	/**
	 * Multiply this sparse indexed vector with another sparse vector.
	 * @param v
	 * @return
	 */
	public final double dotProduct(ASparseVector v) {
		int vNonSparse=v.nonSparseElementCount();
		if (vNonSparse==0) return 0.0; // zero vector
		if (vNonSparse==1) { // single non-saprse element
			if (v instanceof ASingleElementVector) {
				ASingleElementVector av=(ASingleElementVector)v;
				int ix=av.index(); // non-sparse index
				return av.value()*unsafeGet(ix);
			}
		}
		
		double result=0.0;
		double[] data=internalData();
		int[] ixs=internalIndexArray();
		AVector vvalues=v.nonSparseValues();
		double[] vdata=vvalues.asDoubleArray();
		if (vdata==null) vdata=vvalues.toDoubleArray();
		int[] vixs=v.nonSparseIndex().data;
		if (data.length==0) return 0.0;
		
		int ti=0;
		int vi=0;
		while ((ti<data.length)&&(vi<vdata.length)) {
			int tv=ixs[ti];
			int vv=vixs[vi];
			if (tv==vv) {
				result+=data[ti]*vdata[vi];
				ti++;
				vi++;
			} else {
				if (tv<vv) {
					ti++;
				} else {
					vi++;
				}
			}
		}
		
		return result;
	}

	@Override
	public int[] nonZeroIndices() {
		int n=(int)nonZeroCount();
        if (n==0) {
            return IntArrays.EMPTY_INT_ARRAY;
        }
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
	public void addMultipleToArray(double factor,int offset, double[] array, int arrayOffset, int length) {
		int aOffset=arrayOffset-offset;
		
		double[] data=internalData();
		Index index=internalIndex();
		int[] ixs=index.data;
		int start=index.seekPosition(offset);
		for (int i=start; i<data.length; i++) {
			int di=ixs[i];
			// if (di<offset) continue; not needed because of seekPosition!
			if (di>=(offset+length)) return;
			array[di+aOffset]+=factor*data[i];
		}
	}
	
	@Override
	public void addProductToArray(double factor, int offset, AVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		if (other instanceof ADenseArrayVector) {
			addProductToArray(factor,offset,(ADenseArrayVector)other,otherOffset,array,arrayOffset,length);
			return;
		}
		assert(offset>=0);
		assert(offset+length<=length());
		
		double[] data=internalData();
		Index index=internalIndex();
		int[] ixs=index.data;
		int dataLength=data.length;
		for (int j=index.seekPosition(offset); j<dataLength; j++) {
			int i =ixs[j]-offset; // index relative to offset
			if (i>=length) return;
			array[i+arrayOffset]+=factor*data[j]*other.get(i+otherOffset);
		}		
	}
	
	@Override
	public void addProductToArray(double factor, int offset, ADenseArrayVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		assert(offset>=0);
		assert(offset+length<=length());
		double[] otherArray=other.getArray();
		otherOffset+=other.getArrayOffset();
		
		double[] data=internalData();
		Index index=internalIndex();
		int[] ixs=index.data;
		int dataLength=data.length;
		for (int j=index.seekPosition(offset); j<dataLength; j++) {
			int i =ixs[j]-offset; // index relative to offset
			if (i>=length) return;
			array[i+arrayOffset]+=factor*data[j]*otherArray[i+otherOffset];
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
	
	@Override
	public boolean equals(AVector v) {
		int len=length();
		if (v.length()!=len) return false;
		
		if (v instanceof ADenseArrayVector) {
			ADenseArrayVector vv=(ADenseArrayVector) v;
			return equalsArray(vv.getArray(),vv.getArrayOffset());
		}
		
		double[] data=internalData();
		int[] ixs=internalIndexArray();
		int n=ixs.length;
		int start=0;
		for (int i=0; i<n; i++) {
			int pos=ixs[i];
			if (!v.isRangeZero(start, pos-start)) return false;
			if (v.unsafeGet(pos)!=data[i]) return false;
			start=pos+1;
		}
		return v.isRangeZero(start, len-start);
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
		
	@Override 
	public final void getElements(double[] array, int offset) {
		Arrays.fill(array,offset,offset+length,0.0);
		copySparseValuesTo(array,offset);
	}
	
	@Override public final void copyTo(AVector v, int offset) {
		if (v instanceof ADenseArrayVector) {
			ADenseArrayVector av=(ADenseArrayVector)v;
			getElements(av.getArray(),av.getArrayOffset()+offset);
		}
		v.fillRange(offset,length,0.0);
		double[] data=internalData();
		int[] ixs=internalIndexArray();
		for (int i=0; i<data.length; i++) {
			v.unsafeSet(offset+ixs[i],data[i]);
		}	
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
