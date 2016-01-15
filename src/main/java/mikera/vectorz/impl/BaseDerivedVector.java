package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Derived vector delegates all calls element-wise to an underlying vector
 * @author Mike
 */
public abstract class BaseDerivedVector extends AWrappedVector<AVector> {
	private static final long serialVersionUID = -9039112666567131812L;

	protected final AVector source;
	
	protected BaseDerivedVector(AVector source) {
		super(source.length());
		this.source=source;
	}

	@Override
	public double get(int i) {
		return source.get(i);
	}
	
	@Override
	public double unsafeGet(int i) {
		return source.unsafeGet(i);
	}
	
	@Override 
	public double dotProduct(double[] data, int offset) {
		return source.dotProduct(data,offset);
	}
	
	@Override 
	public double dotProduct(double[] data, int offset, int stride) {
		return source.dotProduct(data,offset,stride);
	}

	@Override
	public void set(int i, double value) {
		source.set(i,value);
	}
	
	@Override
	public double elementSum() {
		return source.elementSum();
	}
	
	@Override
	public double elementSquaredSum() {
		return source.elementSquaredSum();
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		source.unsafeSet(i,value);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		source.getElements(dest, offset);
	}
	
	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return source.isFullyMutable();
	}
	
	@Override
	public boolean isMutable() {
		return source.isMutable();
	}
	
	@Override
	public boolean equals(AVector v) {
		return source.equals(v);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return source.equalsArray(data, offset);
	}

}
