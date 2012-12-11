package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;

/**
 * Lightweight immutable zero matrix class
 */
public class ZeroMatrix extends AMatrix {
	private final int inputDimensions;
	private final int outputDimensions;

	@Override public 
	boolean isFullyMutable() {
		return false;
	}
	
	public ZeroMatrix(int rows, int columns) {
		outputDimensions=rows;
		inputDimensions=columns;
	}
	
	@Override
	public int inputDimensions() {
		return inputDimensions;
	}

	@Override
	public int outputDimensions() {
		return outputDimensions;
	}

	@Override
	public int rowCount() {
		return outputDimensions;
	}

	@Override
	public int columnCount() {
		return inputDimensions;
	}

	@Override
	public double get(int row, int column) {
		return 0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException("ZeroMatrix is immutable!");
	}
	
	@Override
	public AMatrix clone() {
		return Matrixx.newMatrix(outputDimensions, inputDimensions);
	}
}
