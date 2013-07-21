package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Matrix that transforms to a subset of elements of the source vector
 * i.e. has exactly one 1.0 in each row
 * 
 * @author Mike
 */
public final class SubsetMatrix extends AMatrix implements ISparse {
	private int inputDims;
	private Index components;

	private SubsetMatrix(int inputDimensions, Index components) {
		inputDims=inputDimensions;
		this.components=components;
	}
	
	public static SubsetMatrix create(Index components, int inputDimensions) {
		return new SubsetMatrix(inputDimensions,components);
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		dest.set(source, components);
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}

	@Override
	public int inputDimensions() {
		return inputDims;
	}

	@Override
	public int outputDimensions() {
		return components.length();
	}
	
	@Override
	public double elementSum() {
		return rowCount();
	}
	
	@Override
	public long nonZeroCount() {
		return rowCount();
	}

	@Override
	public int rowCount() {
		return outputDimensions();
	}

	@Override
	public int columnCount() {
		return inputDimensions();
	}
	
	@Override
	public double density() {
		return 1.0/inputDims;
	}

	@Override
	public double get(int row, int column) {
		return (column==components.get(row))?1.0:0.0;
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return (column==components.get(row))?1.0:0.0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException(ErrorMessages.notFullyMutable(this, row, column));
	}

	@Override
	public SubsetMatrix exactClone() {
		return SubsetMatrix.create(components.clone(),inputDims);
	}
}
