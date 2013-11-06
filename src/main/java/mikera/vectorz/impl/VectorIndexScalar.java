package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.Scalar;
import mikera.vectorz.util.VectorzException;

public class VectorIndexScalar extends AScalar {
	final AVector vector;
	final int index;
	
	public VectorIndexScalar(AVector vector, int index) {
		// don't check - should be checked by caller
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
		return vector.isFullyMutable();
	}
	
	@Override
	public boolean isFullyMutable() {
		return vector.isFullyMutable();
	}
	
	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public Scalar clone() {
		return new Scalar(get());
	}
	
	@Override
	public VectorIndexScalar exactClone() {
		return new VectorIndexScalar(vector.clone(),index);
	}
	
	@Override
	public void validate() {
		if ((index<0)||(index>=vector.length())) throw new VectorzException("Index out of bounds");
		super.validate();
	}
}
