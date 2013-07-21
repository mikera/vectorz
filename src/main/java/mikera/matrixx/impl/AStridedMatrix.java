package mikera.matrixx.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public abstract class AStridedMatrix extends AArrayMatrix {

	protected AStridedMatrix(double[] data, int rows, int cols) {
		super(data, rows, cols);
	}

	public abstract int getArrayOffset();

	public abstract int rowStride();
	
	public abstract int columnStride();	
	
	@Override
	public AVector getRow(int i) {
		return Vectorz.wrapStrided(data, getArrayOffset()+i*rowStride(), cols, columnStride());
	}
	
	@Override
	public AVector getColumn(int i) {
		return Vectorz.wrapStrided(data, getArrayOffset()+i*columnStride(), rows, rowStride());
	}
}
