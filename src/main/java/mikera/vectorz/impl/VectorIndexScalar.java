package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;

public class VectorIndexScalar extends AScalar {
	final AVector vector;
	final int index;
	
	public VectorIndexScalar(AVector vector, int index) {
		assert((index>=0)&&(index<vector.length()));
		this.vector=vector;
		this.index=index;
	}
	
	@Override
	public double get() {
		return vector.get(index);
	}
	
	@Override
	public void set(double value) {
		vector.set(index,value);
	}
	
	@Override
	public boolean isMutable() {
		return vector.isMutable();
	}

}
