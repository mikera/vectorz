package mikera.vectorz.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract Vector class representing a view over a matrix
 * 
 * Supports arbitrary indexing into the underlying matrix
 * 
 * @author Mike
 */
@SuppressWarnings("serial")
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
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		// we assume unsafe is OK, i.e. calculations are correct given correct i
		source.unsafeSet(calcRow(i),calcCol(i),value);
	}
	
	@Override 
	public void unsafeSet(int i, double value) {
		// we assume unsafe is OK, i.e. both i and calculations are correct
		source.unsafeSet(calcRow(i),calcCol(i),value);
	}
	
	@Override 
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		// we assume unsafe is OK, i.e. calculations are correct given correct i
		return source.unsafeGet(calcRow(i),calcCol(i));
	}
	
	@Override 
	public double unsafeGet(int i) {
		return source.unsafeGet(calcRow(i),calcCol(i));
	}
	
	@Override
	public MatrixIndexScalar slice(int i) {
		return MatrixIndexScalar.wrap(source, calcRow(i), calcCol(i));
	}
	
	@Override
	public int length() {
		return length;
	}
}
