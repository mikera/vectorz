package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.util.DoubleArrays;


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
	
	abstract double[] getInternalData();
	
	abstract Index getInternalIndex();
	
	@Override
	public boolean isZero() {
		return DoubleArrays.isZero(getInternalData());
	}
	
	@Override
	public boolean isRangeZero(int start, int length) {
		int end=start+length;
		Index index=getInternalIndex();
		double[] data=getInternalData();
		int si=index.seekPosition(start);
		int di=index.seekPosition(end);
		for (int i=si; i<di; i++) {
			if (data[i]!=0.0) return false;
		}
		return true;
	}
	
	@Override
	public double elementSum() {
		return DoubleArrays.elementSum(getInternalData());
	}
	
	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(getInternalData());
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		double[] tdata=this.getInternalData();
		int[] ixs=getInternalIndex().data;
		for (int j=0; j<tdata.length; j++) {
			result+=tdata[j]*data[offset+ixs[j]];
		}
		return result;
	}
	
	@Override
	public double dotProduct(AVector v) {
		if (v instanceof ADenseArrayVector) return dotProduct((ADenseArrayVector)v);
		double result=0.0;
		double[] data=getInternalData();
		int[] ixs=getInternalIndex().data;
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
}
