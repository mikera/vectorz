package mikera.vectorz.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Abstract Vector class representing a view over a matrix
 * 
 * Supports arbitrary indexing into the underlying matrix
 * 
 * @author Mike
 */
@SuppressWarnings("serial")
public abstract class AMatrixViewVector extends ASizedVector {
	protected AMatrix source;
	
	protected AMatrixViewVector(AMatrix source, int length) {
		super(length);
		this.source=source;
	}

	/**
	 * Calculates the row in the source matrix that corresponds to an index in this vector.
	 * 
	 * This method is unsafe: caller is assumed to have checked that the index is in range
	 * @param i
	 * @return
	 */
	protected abstract int calcRow(int i);
	
	/**
	 * Calculates the row in the source matrix that corresponds to an index in this vector
	 * 
	 * This method is unsafe: caller is assumed to have checked that the index is in range
	 * @param i
	 * @return
	 */
	protected abstract int calcCol(int i);
		
	@Override
	public void addAt(int i, double v) {
		int r=calcRow(i);
		int c=calcCol(i);
		source.unsafeSet(r,c,source.unsafeGet(r,c)+v);
	}
	
	@Override 
	public void set(int i, double value) {
		checkIndex(i);
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
		checkIndex(i);
		// we assume unsafe is OK, i.e. calculations are correct given correct i
		return source.unsafeGet(calcRow(i),calcCol(i));
	}
	
	@Override
	public double dotProduct(AVector v) {
		if (v instanceof ADenseArrayVector) return dotProduct((ADenseArrayVector)v);
 		if (v instanceof ASparseVector) return ((ASparseVector)v).dotProduct(this);
 		
 		int len=checkSameLength(v);
		double total=0.0;
		for (int i=0; i<len; i++) {
			total+=unsafeGet(i)*v.unsafeGet(i);
		}
		return total;
	}
	
	@Override 
	public double unsafeGet(int i) {
		// we assume unsafe is OK, i.e. calculations are correct given correct i
		return source.unsafeGet(calcRow(i),calcCol(i));
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		for (int i=0; i<length; i++) {
			data[offset+i]=source.unsafeGet(calcRow(i), calcCol(i));
		}
	}
	
	@Override
	public MatrixIndexScalar slice(int i) {
		checkIndex(i);
		return MatrixIndexScalar.wrap(source, calcRow(i), calcCol(i));
	}
	
	@Override
	public boolean equals(AVector v) {
		if (v==this) return true;
		if (v.length()!=length) return false;
		for (int i=0; i<length; i++) {
			if (v.unsafeGet(i)!=source.unsafeGet(calcRow(i), calcCol(i))) return false;
		}
		return true;
	}
}
