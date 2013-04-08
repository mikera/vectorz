package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Abstract base class for diagonal matrices
 * @author Mike
 *
 */
public abstract class ADiagonalMatrix extends AMatrix implements ISparse {
	protected final int dimensions;
	
	public ADiagonalMatrix(int dimensions) {
		this.dimensions=dimensions;
	}

	@Override
	public boolean isSquare() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public double determinant() {
		double det=1.0;
		for (int i=0; i<dimensions; i++) {
			det*=get(i,i);
		}
		return det;
	}
	
	/**
	 * Returns the number of dimensions of this diagonal matrix
	 * @return
	 */
	public int dimensions() {
		return dimensions;
	}
	
	public AMatrix compose(ADiagonalMatrix a) {
		if (!(dimensions==a.dimensions)) throw new IllegalArgumentException("Matrix dimensions not compatible!");
		DiagonalMatrix result=DiagonalMatrix.create(dimensions);
		for (int i=0; i<dimensions; i++) {
			result.data[i]=getDiagonalValue(i)*a.getDiagonalValue(i);
		}
		return result;
	}
	
	@Override
	public AMatrix compose(AMatrix a) {
		if (a instanceof ADiagonalMatrix) {
			return compose((ADiagonalMatrix) a);
		}
		if (!(dimensions==a.rowCount())) throw new IllegalArgumentException("Matrix dimensions not compatible!");
		return super.compose(a);
	}
	
	@Override
	public void transformInPlace(AVector v) {
		for (int i=0; i<dimensions; i++) {
			v.set(i,v.get(i)*getDiagonalValue(i));
		}
	}
	
	@Override
	public int rowCount() {
		return dimensions;
	}

	@Override
	public int columnCount() {
		return dimensions;
	}
	
	@Override 
	public boolean isIdentity() {
		int dimensions=dimensions();
		for (int i=0; i<dimensions; i++ ) {
			if (get(i,i)!=1.0) return false;
			
		}
		return true;
	}
	
	@Override
	public void transposeInPlace() {
		// already done!
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		return v.get(i)*getDiagonalValue(i);
	}
	
	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException("Matrix set not supported by "+this.getClass());
	}

	public double getDiagonalValue(int i) {
		return get(i,i);
	}
	
	@Override
	public double density() {
		return 1.0/dimensions;
	}

}
