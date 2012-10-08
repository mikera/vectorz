package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.util.VectorzException;

/** 
 * A matrix class backed by a double[] array
 * @author Mike
 *
 */
public final class ArrayMatrix extends AMatrix{
	private final int rows;
	private final int columns;
	public final double[] data;
	
	public ArrayMatrix(int rowCount, int columnCount) {
		this(rowCount,columnCount,new double[rowCount*columnCount]);
	}
	
	public ArrayMatrix(AMatrix m) {
		this(m.rowCount(),m.columnCount());
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				data[(i * columns) + j] = m.get(i, j);
			}
		}
	}
	
	private ArrayMatrix(int rowCount, int columnCount, double[] data) {
		this.rows=rowCount;
		this.columns=columnCount;
		this.data=data;
	}
	
	public static ArrayMatrix wrap(int rowCount, int columnCount, double[] data) {
		if (data.length!=rowCount*columnCount) throw new VectorzException("data array is of wrong size: "+data.length);
		return new ArrayMatrix(rowCount,columnCount,data);
	}
	
	@Override
	public ArraySubVector getRow(int row) {
		return ArraySubVector.wrap(data,row*columns,columns);
	}

	@Override
	public int rowCount() {
		return rows;
	}

	@Override
	public int columnCount() {
		return columns;
	}

	@Override
	public double get(int row, int column) {
		return data[(row*columns)+column];
	}

	@Override
	public void set(int row, int column, double value) {
		data[(row*columns)+column]=value;
	}
}
