package mikera.matrixx.impl;

import java.nio.DoubleBuffer;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.impl.BufferVector;

/**
 * A matrix class implemented using a java.nio.DoubleBuffer
 * 
 * Intended for use with native libraries that require interop with buffer memory
 * 
 * @author Mike
 *
 */
public class BufferMatrix extends ARectangularMatrix {
	private static final long serialVersionUID = 2933979132279936135L;
	
	
	final DoubleBuffer buffer;
	
	protected BufferMatrix(int rows, int cols) {
		this(DoubleBuffer.allocate(rows*cols), rows, cols);
	}

	protected BufferMatrix(DoubleBuffer buf, int rows, int cols) {
		super(rows,cols);
		this.buffer=buf;
	}
	
	public static BufferMatrix wrap(double[] source, int rows, int cols) {
		if (source.length!=(rows*cols)) throw new IllegalArgumentException("Wrong array size for matrix of shape "+Index.of(rows,cols));
		return new BufferMatrix(DoubleBuffer.wrap(source),rows,cols);
	}
	
	public static BufferMatrix wrap(DoubleBuffer source, int rows, int cols) {
		return new BufferMatrix(source,rows,cols);
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
	public double unsafeGet(int i, int j) {
		return buffer.get(i*cols+j);
	}

	@Override
	public void unsafeSet(int i, int j, double value) {
		buffer.put(i*cols+j,value);
	}
	
	@Override
	public BufferVector getRowView(int i) {
		int cols=this.cols;
		int t=i*cols;
		buffer.position(t);
		buffer.limit(t+cols);
		DoubleBuffer subBuffer=buffer.slice();
		buffer.clear();
		return BufferVector.wrap(subBuffer, cols);
	}
	
	@Override
	public BufferVector asVector() {
		return BufferVector.wrap(buffer.duplicate(), rows*cols);
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public BufferMatrix clone() {
		return exactClone();
	}

	@Override
	public BufferMatrix exactClone() {
		int ec=buffer.capacity();
		double[] newArray=new double[ec];
		buffer.get(newArray);
		buffer.clear();
		return BufferMatrix.wrap(newArray,rows,cols);
	}

	@Override
	public boolean isZero() {
		return asVector().isZero();
	}


}
