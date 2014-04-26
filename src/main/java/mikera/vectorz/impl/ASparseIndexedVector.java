package mikera.vectorz.impl;

import mikera.indexz.Index;


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
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		double[] tdata=this.getInternalData();
		int[] ixs=getInternalIndex().data;
		for (int j=0; j<tdata.length; j++) {
			result+=tdata[j]*data[offset+ixs[j]];
		}
		return result;
	}
}
