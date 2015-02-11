package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Matrix that transforms to a selection of specific elements of the source vector
 * i.e. has exactly one 1.0 in each row
 * 
 * @author Mike
 */
public final class SubsetMatrix extends ABooleanMatrix implements ISparse, IFastRows {
	private static final long serialVersionUID = 4937375232646236833L;

	private int inputDims;
	private Index components;

	private SubsetMatrix(int inputDimensions, Index components) {
		inputDims=inputDimensions;
		this.components=components;
	}
	
	public static SubsetMatrix create(Index components, int inputDimensions) {
		SubsetMatrix sm=new SubsetMatrix(inputDimensions,components);
		if (!sm.components.allInRange(0,sm.inputDims)) {
			throw new IllegalArgumentException("SubsetMatrix with input dimensionality "+sm.inputDims+" not valid for component indexes: "+sm.components);
		}
		return sm;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		dest.set(source, components);
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
		return components.length();
	}

	@Override
	public int columnCount() {
		return inputDims;
	}
	
	@Override
	public double density() {
		return 1.0/inputDims;
	}
	
	@Override
	public AxisVector getRowView(int i) {
		return AxisVector.create(components.get(i), inputDims);
	}
	
	@Override
	public double calculateElement(int i, AVector inputVector) {
		return inputVector.unsafeGet(components.get(i));
	}
	
	@Override
	public double calculateElement(int i, Vector inputVector) {
		return inputVector.unsafeGet(components.get(i));
	}

	@Override
	public double get(int row, int column) {
		if (column<0||column>=inputDims) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		}
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
	
	@Override
	public void validate() {
		int rc=rowCount();
		int cc=columnCount();
		for (int i=0; i<rc; i++) {
			int s=components.get(i);
			if ((s<0)||(s>=cc)) throw new VectorzException("Component out of range at row "+i);
		}
		super.validate();
	}

	@Override
	public boolean isZero() {
		return false;
	}
}
