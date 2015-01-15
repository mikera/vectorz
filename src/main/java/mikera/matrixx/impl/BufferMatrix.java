package mikera.matrixx.impl;

import java.nio.DoubleBuffer;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;

public class BufferMatrix extends ARectangularMatrix {

	final DoubleBuffer buffer;
	
	protected BufferMatrix(int rows, int cols) {
		this(DoubleBuffer.allocate(rows*cols), rows, cols);
	}

	private BufferMatrix(DoubleBuffer buf, int rows, int cols) {
		super(rows,cols);
		this.buffer=buf;
	}
	
	public static BufferMatrix wrap(double[] source, int rows, int cols) {
		return new BufferMatrix(DoubleBuffer.wrap(source),rows,cols);
	}
	
	public static AMatrix create(AMatrix m) {
		return wrap(m.toDoubleArray(),m.rowCount(),m.columnCount());
	}

	@Override
	public double get(int i, int j) {
		checkColumn(j);
		return buffer.get(i*cols+j);
	}

	@Override
	public void set(int i, int j, double value) {
		checkColumn(j);
		buffer.put(i*cols+j,value);
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}

	@Override
	public AMatrix exactClone() {
		int ec=buffer.capacity();
		double[] newArray=new double[ec];
		buffer.get(newArray);
		buffer.clear();
		return BufferMatrix.wrap(newArray,rows,cols);
	}


}
