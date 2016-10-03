package mikera.matrixx.impl;

import java.util.Arrays;
import java.util.Iterator;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.randomz.Hash;
import mikera.vectorz.AVector;
import mikera.vectorz.Op2;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.RepeatedElementIterator;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * Lightweight, sparse immutable zero matrix class. 
 * 
 * Can be any shape, including empty shapes.
 */
public final class ZeroMatrix extends ARectangularMatrix implements IFastRows, IFastColumns, ISparse {
	private static final long serialVersionUID = 875833013123277805L;

	@Override public 
	boolean isFullyMutable() {
		return (rows==0)||(cols==0);
	}
	
	private ZeroMatrix(int rows, int columns) {
		super(rows,columns);
	}
	
	/**
	 * Create a ZeroMatrix of the specified size
	 * 
	 * @param rows
	 * @param columns
	 * @return
	 */
	public static ZeroMatrix create(int rows, int columns) {
		return new ZeroMatrix(rows,columns);
	}
		
	@Override
	public boolean isSparse() {
		return true;
	}
		
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isSymmetric() {
		return isSquare();
	}
	
	@Override
	public boolean isDiagonal() {
		return isSquare();
	}
	
	@Override
	public boolean isUpperTriangular() {
		return true;
	}
	
	@Override
	public boolean isLowerTriangular() {
		return true;
	}
	
	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public int upperBandwidthLimit() {
		return 0;
	}
	
	@Override
	public int lowerBandwidthLimit() {
		return 0;
	}
	
	@Override
	public AVector getRowView(int row) {
		checkRow(row);
		return Vectorz.createZeroVector(cols);
	}
	
	@Override
	public AVector getColumnView(int col) {
		checkColumn(col);
		return Vectorz.createZeroVector(rows);
	}
	
	@Override
	public AVector getRowClone(int row) {
		checkRow(row);
		return Vectorz.newVector(cols);
	}
	
	@Override
	public AVector getColumnClone(int col) {
		checkColumn(col);
		return Vectorz.newVector(rows);
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {	
		Arrays.fill(dest, destOffset,destOffset+columnCount(),0.0);
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		Arrays.fill(dest, destOffset,destOffset+rowCount(),0.0);
	}
	
	@Override
	public void addToArray(double[] dest, int offset) {
		// do nothing
	}
	
	@Override
	public void addSparse(double c) {
		// no change
	}
	
	@Override
	public void setSparse(AMatrix a) {
		checkSameShape(a);
		// no change
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		return op.reduceZeros(init,elementCount());
	}
	
	@Override
	public double reduce(Op2 op) {
		return op.reduceZeros(elementCount());
	}
	
	@Override
	public AMatrix addCopy(AMatrix a) {
		if (!isSameShape(a)) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		return a.copy();
	}
	
	@Override
	public void getElements(double[] dest, int destOffset) {
		Arrays.fill(dest, destOffset,destOffset+rowCount()*columnCount(),0.0);
	}

	@Override
	public double determinant() {
		if(isSquare()) throw new UnsupportedOperationException(ErrorMessages.squareMatrixRequired(this));
		return 0.0;
	}
	
	@Override
	public int rank() {
		return 0;
	}
	
	@Override
	public double trace() {
		return 0.0;
	}
	
	@Override
	public double diagonalProduct() {
		int n=Math.min(rowCount(), columnCount());
		return (n>0)?0.0:1.0;
	}
	
	@Override
	public double rowDotProduct(int i, AVector v) {
		return 0.0;
	}

	@Override
	public double get(int i, int j) {
		checkIndex(i,j);
		return 0.0;
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return 0.0;
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public AMatrix clone() {
		return Matrixx.newMatrix(rows, cols);
	}
	
	@Override
	public boolean isZero() {
		return true;
	}

	@Override
	public double elementSum() {
		return 0.0;
	}
	
	@Override
	public double elementMax(){
		if ((rows<=0)||(cols<=0)) throw new IllegalArgumentException(ErrorMessages.noElements(this)); 
		return 0.0;
	}
	
	@Override
	public double elementMin(){
		if ((rows<=0)||(cols<=0)) throw new IllegalArgumentException(ErrorMessages.noElements(this)); 
		return 0.0;
	}
	
	@Override
	public long nonZeroCount() {
		return 0;
	}
	
	@Override 
	public int hashCode() {
		return Hash.zeroVectorHash(cols*rows);
	}
	
	@Override
	public void transform(AVector input, AVector output) {
		assert(output.length()==rows);
		output.fill(0.0);
	}
	
	@Override
	public void transform(Vector input, Vector output) {
		assert(output.length()==rows);
		output.fill(0.0);
	}
	
	@Override
	public boolean isInvertible() {
		return false;
	}
	
	@Override
	public AVector asVector() {
		return Vectorz.createZeroVector(cols*rows);
	}
	
	@Override
	public AMatrix innerProduct(AMatrix m) {
		if (columnCount()!=m.rowCount()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, m));
		return ZeroMatrix.create(rows, m.columnCount());
	}
	
	@Override
	public ZeroMatrix multiplyCopy(double a) {
		return this;
	}
	
	@Override 
	public void multiply(AMatrix m) {
		// do nothing, already zero!
	}

	@Override
	public boolean equals(AMatrix m) {
		if (!isSameShape(m)) return false;
		return m.isZero();
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.isZero(data, offset, rows*cols);
	}

	@Override
	public ZeroMatrix getTranspose() {
		if (cols==rows) return this;
		return ZeroMatrix.create(cols, rows);
	}
	
	@Override
	public ZeroMatrix getTransposeView() {
		if (cols==rows) return this;
		return ZeroMatrix.create(cols, rows);
	}
	
	@Override
	public Matrix toMatrix() {
		return Matrix.create(rows, cols);
	}
	
	@Override
	public double[] toDoubleArray() {
		return new double[rows*cols];
	}
		
	@Override
	public AMatrix sparseClone() {
		return Matrixx.createSparse(rows, cols);
	}
	
	@Override
	public Matrix toMatrixTranspose() {
		return Matrix.create(cols, rows);
	}
	
	@Override
	public AVector getLeadingDiagonal() {
		return Vectorz.createZeroVector(Math.min(rows, cols));
	}
	
	@Override
	public AVector getBand(int band) {
		return Vectorz.createZeroVector(bandLength(band));
	}
	
	@Override
	public AMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		return ZeroMatrix.create(rows, cols);
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		return new RepeatedElementIterator(cols*rows,0.0);
	}
	
	@Override
	public ZeroMatrix exactClone() {
		return new ZeroMatrix(rows,cols);
	}

	@Override
	public boolean hasUncountable() {
		return false;
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return 0;
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return elementPowSum(p);
    }
}
