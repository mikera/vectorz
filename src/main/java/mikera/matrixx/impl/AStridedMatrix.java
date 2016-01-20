package mikera.matrixx.impl;

import java.util.Iterator;

import mikera.arrayz.impl.IStridedArray;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.AStridedVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract base class for arbitrary strided matrices
 * 
 * @author Mike
 */
public abstract class AStridedMatrix extends AArrayMatrix implements IStridedArray {
	private static final long serialVersionUID = -8908577438753599161L;

	protected AStridedMatrix(double[] data, int rows, int cols) {
		super(data, rows, cols);
	}

	@Override
	public abstract int getArrayOffset();

	/**
	 * Gets the row stride for this strided matrix. Each row has this offset in the underlying data array 
	 * vs. the previous row. May be positive, zero or negative.
	 */
	public abstract int rowStride();
	
	/**
	 * Gets the column stride for this strided matrix. Each column has this offset in the underlying data array 
	 * vs. the previous column. May be positive, zero or negative.
	 */
	public abstract int columnStride();	
	
	@Override
	public AStridedMatrix subMatrix(int rowStart, int rowCount, int colStart, int colCount) {
		if ((rowStart<0)||(rowStart>=this.rows)||(colStart<0)||(colStart>=this.cols)) throw new IndexOutOfBoundsException(ErrorMessages.position(rowStart,colStart));
		if ((rowStart+rowCount>this.rows)||(colStart+colCount>this.cols)) throw new IndexOutOfBoundsException(ErrorMessages.position(rowStart+rowCount,colStart+colCount));
		int rowStride=rowStride();
		int colStride=columnStride();
		int offset=getArrayOffset();
		return StridedMatrix.wrap(data, rowCount, colCount, offset+rowStart*rowStride+colStart*colStride, rowStride, colStride);
	}
	
	@Override
	public AStridedVector getRow(int i) {
		return Vectorz.wrapStrided(data, getArrayOffset()+i*rowStride(), cols, columnStride());
	}
		
	@Override
	public AStridedVector getColumn(int i) {
		return Vectorz.wrapStrided(data, getArrayOffset()+i*columnStride(), rows, rowStride());
	}
	
	@Override
	public AStridedVector getRowView(int i) {
		return getRow(i);
	}
	
	@Override
	public final AStridedVector getColumnView(int i) {
		return getColumn(i);
	}
	
	@Override
	public double diagonalProduct() {
		int n=Math.min(rowCount(), columnCount());
		int offset=getArrayOffset();
		int st=rowStride()+columnStride();
		double[] data=getArray();
		double result=1.0;
		for (int i=0; i<n; i++) {
			result*=data[offset];
			offset+=st;
		}
		return result;
	}
	
	@Override
	public double trace() {
		int n=Math.min(rowCount(), columnCount());
		int offset=getArrayOffset();
		int st=rowStride()+columnStride();
		double[] data=getArray();
		double result=0.0;
		for (int i=0; i<n; i++) {
			result+=data[offset];
			offset+=st;
		}
		return result;
	}
	
	@Override
	public AStridedVector getBand(int i) {
		int cs=columnStride();
		int rs=rowStride();
		if ((i>cols)||(i<-rows)) throw new IndexOutOfBoundsException(ErrorMessages.invalidBand(this, i));
		return Vectorz.wrapStrided(data, getArrayOffset()+bandStartColumn(i)*cs+bandStartRow(i)*rs, bandLength(i), rs+cs);
	}
	
	@Override
	public void add(AVector v) {
		checkColumnCount(v.length());
		int offset=getArrayOffset();
		int colStride=columnStride();
		int rowStride=rowStride();
		for (int i=0; i<rows; i++) {
			v.addToArray(data, offset+i*rowStride, colStride);
		}
	}
	
	@Override
	public void addToArray(double[] dest, int destOffset) {
		int offset=getArrayOffset();
		int colStride=columnStride();
		int rowStride=rowStride();
		for (int i=0; i<rows; i++) {
			int thisSrcOffset=offset+i*rowStride;
			for (int j=0; j<cols; j++) {
				dest[destOffset++]+=data[thisSrcOffset+j*colStride];
			}
		}
	}
	
	@Override
	public void applyOp(Op op) {
		int offset=getArrayOffset();
		int colStride=columnStride();
		int rowStride=rowStride();
		for (int i=0; i<rows; i++) {
			op.applyTo(data, offset+i*rowStride, colStride, cols);
		}
	}
	
	@Override
	public final Matrix applyOpCopy(Op op) {
		double[] da=toDoubleArray();
		op.applyTo(da);
		return Matrix.wrap(rows,cols,da);
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		int rc=rowCount();
		int rs=rowStride();
		int cs=columnStride();
		int offset=getArrayOffset();
		double result=init;
		for (int i=0; i<rc; i++) {
			result=op.reduce(result, data, offset+rs*i, cols,cs);
		}
		return result;
	}
	
	@Override
	public double rowDotProduct(int i, AVector a) {
		double[] data=getArray();
		int offset=getArrayOffset();

		return a.dotProduct(data, offset+i*rowStride(),columnStride());
	}
	
	@Override
	public void add(AMatrix m) {
		checkSameShape(m);
		int offset=getArrayOffset();
		int colStride=columnStride();
		int rowStride=rowStride();
		for (int i=0; i<rows; i++) {
			m.getRow(i).addToArray(data, offset+i*rowStride, colStride);
		}
	}
	
	@Override
	public abstract void copyRowTo(int row, double[] dest, int destOffset);
	
	@Override
	public abstract void copyColumnTo(int col, double[] dest, int destOffset);
	
	@Override
	public int[] getStrides() {
		return new int[] {rowStride(), columnStride()};
	}
	
	@Override
	public int getStride(int dimension) {
		switch (dimension) {
			case 0: return rowStride();
			case 1: return columnStride();
			default: throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this, dimension));
		}
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		return new StridedElementIterator(this);
	}
	
	@Override
	public AStridedMatrix getTranspose() {
		return getTransposeView();
	}
	
	@Override
	public AStridedMatrix getTransposeView() {
		return Matrixx.wrapStrided(getArray(),columnCount(),rowCount(),getArrayOffset(),columnStride(),rowStride());
	}
	
	@Override
	public boolean isPackedArray() {
		return (getArrayOffset()==0)&&(columnStride()==1)&&(rowStride()==columnCount())&&(getArray().length==elementCount());
	}
	
	@Override
	public double[] asDoubleArray() {
		if (isPackedArray()) return getArray();
		return null;
	}
	
	@Override
	public boolean isZero() {
		// select row or column iteration in the most cache-friendly manner
		if (rowStride()>columnStride()) {
			int rc=rowCount();
			for (int i=0; i<rc; i++) {
				if (!getRow(i).isZero()) return false;
			}
		} else {
			int cc=columnCount();
			for (int i=0; i<cc; i++) {
				if (!getColumn(i).isZero()) return false;
			}
		}
		return true;
	}
	
	@Override
	public final Matrix clone() {
		// always want a dense result when cloning a strided matrix
		return Matrix.create(this);
	}

}
