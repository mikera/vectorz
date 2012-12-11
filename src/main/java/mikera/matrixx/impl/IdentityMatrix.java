package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Specialised identity matrix class. Immutable.
 * 
 * @author Mike
 *
 */
public class IdentityMatrix extends AMatrix {
	private final int dimensions;
	
	public IdentityMatrix(int dimensions) {
		this.dimensions=dimensions;
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
	public double get(int row, int column) {
		assert(row>=0);
		assert(column>=0);
		assert(row<dimensions);
		assert(column<dimensions);
		return (row==column)?1.0:0.0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException("Identity matrix is immutable!");
	}
	
	@Override 
	public void transform(AVector source, AVector dest) {
		dest.set(source);
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public AVector getLeadingDiagonal() {
		AVector v= Vectorz.newVector(dimensions);
		v.fill(1.0);
		return v;
	}
	
	@Override
	public boolean isSquare() {
		return true;
	}
	
	@Override 
	public boolean isIdentity() {
		return true;
	}
	
	@Override 
	public AMatrix inverse() {
		return this;
	}
	
	@Override
	public void transposeInPlace() {
		// Nothing to do!
	}
}
