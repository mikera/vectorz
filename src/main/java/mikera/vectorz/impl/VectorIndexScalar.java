package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.Scalar;
import mikera.vectorz.util.VectorzException;

public class VectorIndexScalar extends AScalar {
	private static final long serialVersionUID = -5999714886554631904L;

	final AVector vector;
	final int index;
	
	private VectorIndexScalar(AVector vector, int index) {
		// don't check index - should be checked by caller
		this.vector=vector;
		this.index=index;
	}
	
	public static VectorIndexScalar wrap(AVector vector, int index) {
		vector.checkIndex(index);
		return new VectorIndexScalar(vector,index);
	}
	
	@Override
	public double get() {
		return vector.unsafeGet(index);
	}
	
	@Override
	public void set(double value) {
		vector.unsafeSet(index,value);
	}
	
	@Override
	public boolean isMutable() {
		return vector.isMutable();
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
	public AScalar mutable() {
		if (vector.isFullyMutable()) {
			return this;
		} else {
			return Scalar.create(get());
		}
	}
	
	@Override
	public void validate() {
		if ((index<0)||(index>=vector.length())) throw new VectorzException("Index out of bounds");
		super.validate();
	}
}
