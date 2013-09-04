package mikera.vectorz.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

public abstract class AMatrixViewVector extends AVector {
	protected AMatrix source;
	protected int length;
	
	protected AMatrixViewVector(AMatrix source, int length) {
		this.source=source;
		this.length=length;
	}

	protected abstract int calcRow(int i);
	
	protected abstract int calcCol(int i);
	
	@Override 
	public void set(int i, double value) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
		source.set(calcRow(i),calcCol(i),value);
	}
	
	@Override 
	public void unsafeSet(int i, double value) {
		source.set(calcRow(i),calcCol(i),value);
	}
	
	@Override 
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
		return source.get(calcRow(i),calcCol(i));
	}
	
	@Override 
	public double unsafeGet(int i) {
		return source.unsafeGet(calcRow(i),calcCol(i));
	}
	
	@Override
	public int length() {
		return length;
	}
}
