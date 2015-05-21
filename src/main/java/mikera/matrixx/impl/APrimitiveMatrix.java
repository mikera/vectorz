package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Abstract base class for the small square primitive backed matrices
 * @author Mike
 *
 */
public abstract class APrimitiveMatrix extends AMatrix {
	private static final long serialVersionUID = -6061660451592522674L;

	@Override
	public boolean isSquare() {
		return true;
	}
	
	@Override
	public abstract int checkSquare();
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public AVector getRow(int i) {
		return getRowClone(i);
	}
	
	@Override
	public AVector getColumn(int j) {
		return getColumnClone(j);
	}
	
	@Override
	public AMatrix copy() {
		return clone();
	}
}
