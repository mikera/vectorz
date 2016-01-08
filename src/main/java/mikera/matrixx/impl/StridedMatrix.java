package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.AStridedVector;
import mikera.vectorz.impl.StridedMatrixViewVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * A general purpose strided matrix implementation
 * 
 * @author Mike
 */
public final class StridedMatrix extends BaseStridedMatrix {
	private static final long serialVersionUID = -7928115802247422177L;

	private StridedMatrix(double[] data, int rowCount, int columnCount,
			int offset, int rowStride, int columnStride) {
		super(data,rowCount,columnCount,offset,rowStride,columnStride);
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
	public AStridedVector getRow(int i) {
		checkRow(i);
		return Vectorz.wrapStrided(data, offset+i*rowStride, cols, colStride);
	}
	
	@Override
	public AStridedVector getColumn(int i) {
		return Vectorz.wrapStrided(data, offset+i*colStride, rows, rowStride);
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
		return new StridedMatrix(data, rowCount, colCount, offset+rowStart*rowStride+colStart*colStride, rowStride, colStride);
	}

	@Override
	public void applyOp(Op op) {
		int rc = rowCount();
		int cc = columnCount();
		int o=offset;
		for (int row = 0; row < rc; row++) {
			int ro=o+row*rowStride();
			for (int col = 0; col < cc; col++) {
				int index = ro+col*colStride;
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
				colStride, rowStride);
	}
	
	@Override
	public AStridedMatrix getTransposeView() {
		return Matrixx.wrapStrided(data, cols, rows, offset,
				colStride, rowStride);
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
		return new StridedMatrix(data.clone(), rows, cols, offset,
				rowStride, colStride);
	}

	public static StridedMatrix create(AMatrix m) {
		StridedMatrix sm = StridedMatrix.create(m.rowCount(), m.columnCount());
		sm.set(m);
		return sm;
	}

	public static StridedMatrix wrap(AStridedMatrix m) {
		return new StridedMatrix(m.getArray(), m.rowCount(), m.columnCount(), m.getArrayOffset(),
				m.rowStride(),m.columnStride());
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
	public boolean equals(AMatrix a) {
		if (a==this) return true;	
		if (!isSameShape(a)) return false;
		
		if (a instanceof ADenseArrayMatrix) {
			ADenseArrayMatrix da=(ADenseArrayMatrix)a;
			return equalsArray(da.getArray(),da.getArrayOffset());
		}
		
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
			int rowOffset=this.offset+i*rowStride;
			for (int j = 0; j < cols; j++) {
				if (this.data[rowOffset+j*colStride] != data[offset++]) return false;
			}
		}
		return true;
	}
}
