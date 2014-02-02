package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.AStridedVector;
import mikera.vectorz.impl.StridedVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

public final class StridedMatrix extends AStridedMatrix {
	private static final long serialVersionUID = -7928115802247422177L;

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
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public AStridedVector getRowView(int i) {
		return StridedVector.wrap(data, offset+i*rowStride, cols, colStride);
	}
	
	@Override
	public AStridedVector getColumnView(int i) {
		return StridedVector.wrap(data, offset+i*colStride, rows, rowStride);
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		int rowOffset=offset+row*rowStride;
		for (int i=0;i<cols; i++) {
			dest[destOffset+i]=data[rowOffset+i*colStride];
		}
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		int colOffset=offset+col*colStride;
		for (int i=0;i<rows; i++) {
			dest[destOffset+i]=data[colOffset+i*rowStride];
		}
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
	public int getArrayOffset() {
		return offset;
	}
	
	@Override
	public boolean isPackedArray() {
		return (offset == 0) 
				&& (colStride == 1)
				&& (rowStride == cols)
				&& (data.length == rows * cols);
	}
	
	@Override
	public AStridedMatrix subMatrix(int rowStart, int rowCount, int colStart, int colCount) {
		if ((rowStart<0)||(rowStart>=this.rows)||(colStart<0)||(colStart>=this.cols)) throw new IndexOutOfBoundsException(ErrorMessages.position(rowStart,colStart));
		if ((rowStart+rowCount>this.rows)||(colStart+colCount>this.cols)) throw new IndexOutOfBoundsException(ErrorMessages.position(rowStart+rowCount,colStart+colCount));
		if ((rowCount<1)||(colCount<1)) throw new IllegalArgumentException(ErrorMessages.illegalSize(rowCount,colCount));
		return new StridedMatrix(data, rowCount, colCount, offset+rowStart*rowStride+colStart*colStride, rowStride, colStride);
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
	public AMatrix getTranspose() {
		return Matrixx.wrapStrided(data, cols, rows, offset,
				colStride, rowStride);
	}
	
	@Override
	public AMatrix getTransposeView() {
		return Matrixx.wrapStrided(data, cols, rows, offset,
				colStride, rowStride);
	}

	@Override
	public double get(int row, int column) {
		if ((row < 0) || (column < 0) || (row >= rows)
				|| (column >= cols))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
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
		if (!equals(this.exactClone())) throw new VectorzException("Thing not equal to itself");
		if (offset<0) throw new VectorzException("Negative offset! ["+offset+"]");
		if (index(rows-1,cols-1)>=data.length) throw new VectorzException("Negative offset! ["+offset+"]");
	}

	@Override
	protected final int index(int row, int col) {
		return offset+(row*rowStride)+(col*colStride);
	}
	
	@Override
	public Matrix clone() {
		return Matrix.create(this);
	}

	@Override
	public boolean equals(AMatrix a) {
		if (a==this) return true;	
		if ((rows != a.rowCount())||(cols != a.columnCount())) return false;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (data[index(i, j)] != a.unsafeGet(i, j))
					return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		for (int i = 0; i < rows; i++) {
			int si=this.offset+i*rowStride;
			for (int j = 0; j < cols; j++) {
				if (this.data[si] != data[offset++]) return false;
				si+=colStride;
			}
		}
		return true;
	}
}
