package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.StridedVector;
import mikera.vectorz.util.VectorzException;

public final class StridedMatrix extends AStridedMatrix {
	private final int rowStride;
	private final int colStride;
	private final int offset;

	private StridedMatrix(double[] data, int rowCount, int columnCount,
			int offset, int rowStride, int columnStride) {
		super(data,rowCount,columnCount);
		this.offset = offset;
		this.rowStride = rowStride;
		this.colStride = columnStride;
	}

	public static StridedMatrix create(int rowCount, int columnCount) {
		double[] data = new double[rowCount * columnCount];
		return new StridedMatrix(data, rowCount, columnCount, 0, columnCount, 1);
	}

	@Override
	public int rowCount() {
		return rows;
	}

	@Override
	public int columnCount() {
		return cols;
	}

	@Override
	public AVector getRow(int i) {
		return StridedVector.wrap(data, offset+i*rowStride, cols, colStride);
	}
	
	@Override
	public AVector getColumn(int i) {
		return StridedVector.wrap(data, offset+i*colStride, rows, rowStride);
	}
	
	@Override
	public int rowStride() {
		return rowStride;
	}
	
	@Override
	public int columnStride() {
		return colStride;
	}
	
	@Override
	public boolean isPackedArray() {
		return (offset == 0) 
				&& (colStride == 1)
				&& (rowStride == cols)
				&& (data.length == rows * cols);
	}
	
	@Override
	public AArrayMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		if ((rowStart<0)||(rowStart>=rows)||(colStart<0)||(colStart>=cols)) throw new IndexOutOfBoundsException("Invalid submatrix start position");
		if ((rowStart+rows>this.rows)||(colStart+cols>this.cols)) throw new IndexOutOfBoundsException("Invalid submatrix end position");
		if ((rows<1)||(cols<1)) throw new IllegalArgumentException("Submatrix has no elements");
		return new StridedMatrix(data, rows, cols, offset+rowStart*rowStride+colStart*colStride, rowStride, colStride);
	}

	@Override
	public void applyOp(Op op) {
		int rc = rowCount();
		int cc = columnCount();
		for (int row = 0; row < rc; row++) {
			for (int col = 0; col < cc; col++) {
				int index = index(row,col);
				double v = data[index];
				data[index] = op.apply(v);
			}
		}
	}
	
	@Override
	public void getElements(double[] dest, int destOffset) {
		int rc = rowCount();
		int cc = columnCount();
		int di=destOffset;
		for (int row = 0; row < rc; row++) {
			for (int col = 0; col < cc; col++) {
				int index = index(row,col);
				double v = data[index];
				dest[di++] = v;
			}
		}
	}

	@Override
	public StridedMatrix getTranspose() {
		return StridedMatrix.wrap(data, cols, rows, offset,
				colStride, rowStride);
	}

	@Override
	public double get(int row, int column) {
		if ((row < 0) || (column < 0) || (row >= rows)
				|| (column >= cols))
			throw new IndexOutOfBoundsException("[" + row + "," + column + "]");
		return data[index(row,column)];
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return data[index(row,column)];
	}
	
	@Override
	public AVector asVector() {
		if (isPackedArray()) {
			return Vector.wrap(data);
		} else if (cols==1) {
			return Vectorz.wrapStrided(data, offset, rows, rowStride);
		} else if (rows ==1){
			return Vectorz.wrapStrided(data, offset, cols, colStride);			
		}
		return super.asVector();
	}

	@Override
	public void set(int row, int column, double value) {
		if ((row < 0) || (column < 0) || (row >= rows)
				|| (column >= cols))
			throw new IndexOutOfBoundsException("[" + row + "," + column + "]");
		data[index(row,column)] = value;
	}
	
	@Override
	public void unsafeSet(int row, int column, double value) {
		data[index(row,column)] = value;
	}

	@Override
	public AMatrix exactClone() {
		return new StridedMatrix(data.clone(), rows, cols, offset,
				rowStride, colStride);
	}

	public static StridedMatrix create(AMatrix m) {
		StridedMatrix sm = StridedMatrix.create(m.rowCount(), m.columnCount());
		sm.set(m);
		return sm;
	}

	public static StridedMatrix wrap(Matrix m) {
		return new StridedMatrix(m.data, m.rowCount(), m.columnCount(), 0,
				m.columnCount(), 1);
	}

	public static StridedMatrix wrap(double[] data, int rows, int columns,
			int offset, int rowStride, int columnStride) {
		return new StridedMatrix(data, rows, columns, offset, rowStride,
				columnStride);
	}
	
	@Override
	public void validate() {
		super.validate();
		if (!equals(this)) throw new VectorzException("Universe destroyed: thing not equal to itself");
		if (offset<0) throw new VectorzException("Negative offset! ["+offset+"]");
		if (index(rows-1,cols-1)>=data.length) throw new VectorzException("Negative offset! ["+offset+"]");
	}

	private final int index(int row, int col) {
		return offset+(row*rowStride)+(col*colStride);
	}
	
	@Override
	public Matrix clone() {
		return Matrix.create(this);
	}
}
