package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.AStridedVector;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.StridedMatrixViewVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * A strided matrix implementation with a column stride of 1
 * 
 * @author Mike
 */
public final class StridedRowMatrix extends AStridedMatrix {
	private static final long serialVersionUID = -7928115802247422177L;
	private final int offset;
	private final int rowStride;

	private StridedRowMatrix(double[] data, int rowCount, int columnCount,
			int offset, int rowStride) {
		super(data,rowCount,columnCount);
		this.rowStride=rowStride;
		this.offset=offset;
	}

	public static StridedRowMatrix create(int rowCount, int columnCount) {
		double[] data = new double[rowCount * columnCount];
		return new StridedRowMatrix(data, rowCount, columnCount, 0, columnCount);
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
		checkRow(i);
		return ArraySubVector.wrap(data, offset+i*rowStride, cols);
	}
	
	@Override
	public AStridedVector getColumnView(int i) {
		return Vectorz.wrapStrided(data, offset+i, rows, rowStride);
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		System.arraycopy(data,offset+row*rowStride,dest,destOffset,cols);
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		int colOffset=offset+col;
		for (int i=0;i<rows; i++) {
			dest[destOffset+i]=data[colOffset+i*rowStride];
		}
	}

	@Override
	public boolean isPackedArray() {
		return (offset == 0) 
				&& (rowStride == cols)
				&& (data.length == rows * cols);
	}
	
	@Override
	public AStridedMatrix subMatrix(int rowStart, int rowCount, int colStart, int colCount) {
		if ((rowStart<0)||(rowStart>=this.rows)||(colStart<0)||(colStart>=this.cols)) throw new IndexOutOfBoundsException(ErrorMessages.position(rowStart,colStart));
		if ((rowStart+rowCount>this.rows)||(colStart+colCount>this.cols)) throw new IndexOutOfBoundsException(ErrorMessages.position(rowStart+rowCount,colStart+colCount));
		return new StridedRowMatrix(data, rowCount, colCount, offset+rowStart*rowStride+colStart, rowStride);
	}

	@Override
	public void applyOp(Op op) {
		int rc = rowCount();
		int cc = columnCount();
		int o=offset;
		for (int row = 0; row < rc; row++) {
			int ro=o+row*rowStride();
			for (int col = 0; col < cc; col++) {
				int index = ro+col;
				double v = data[index];
				data[index] = op.apply(v);
			}
		}
	}
	
	@Override
	public void getElements(double[] dest, int destOffset) {
		int rc = rowCount();
		int cc = columnCount();
		for (int row = 0; row < rc; row++) {
			copyRowTo(row, dest, destOffset+row*cc);
		}
	}

	@Override
	public AStridedMatrix getTranspose() {
		return Matrixx.wrapStrided(data, cols, rows, offset,
				1, rowStride);
	}
	
	@Override
	public AStridedMatrix getTransposeView() {
		return Matrixx.wrapStrided(data, cols, rows, offset,
				1, rowStride);
	}
	
	@Override
	public AVector asVector() {
		if (isPackedArray()) {
			return Vector.wrap(data);
		} else if (cols==1) {
			return Vectorz.wrapStrided(data, offset, rows, rowStride);
		} else if (rows ==1){
			return Vectorz.wrapStrided(data, offset, cols, 1);			
		}
		return new StridedMatrixViewVector(this);
	}

	@Override
	public void set(int i, int j, double value) {
		checkIndex(i,j);
		data[index(i,j)] = value;
	}
	
	@Override
	public void unsafeSet(int i, int j, double value) {
		data[index(i,j)] = value;
	}

	@Override
	public AMatrix exactClone() {
		return new StridedRowMatrix(data.clone(), rows, cols, offset,
				rowStride);
	}

	public static StridedRowMatrix create(AMatrix m) {
		StridedRowMatrix sm = StridedRowMatrix.create(m.rowCount(), m.columnCount());
		sm.set(m);
		return sm;
	}

	public static StridedRowMatrix wrap(Matrix m) {
		return new StridedRowMatrix(m.data, m.rowCount(), m.columnCount(), 0,
				m.columnCount());
	}

	public static StridedRowMatrix wrap(double[] data, int rows, int columns,
			int offset, int rowStride) {
		return new StridedRowMatrix(data, rows, columns, offset, rowStride);
	}
	
	@Override
	public void validate() {
		super.validate();
		if (!equals(this.exactClone())) throw new VectorzException("Thing not equal to itself");
		if (offset<0) throw new VectorzException("Negative offset! ["+offset+"]");
		if (index(rows-1,cols-1)>=data.length) throw new VectorzException("Negative offset! ["+offset+"]");
	}
	
	@Override
	public Matrix clone() {
		return Matrix.create(this);
	}

	@Override
	public boolean equals(AMatrix a) {
		if (a==this) return true;	
		if (a instanceof ADenseArrayMatrix) return equals((ADenseArrayMatrix)a);
		
		if (!isSameShape(a)) return false;
		
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
				si++;
			}
		}
		return true;
	}

	@Override
	public int getArrayOffset() {
		return offset;
	}

	@Override
	public int rowStride() {
		return rowStride;
	}

	@Override
	public int columnStride() {
		return 1;
	}

	@Override
	protected int index(int i, int j) {
		return offset+(rowStride*i)+j;
	}
}
