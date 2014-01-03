package mikera.matrixx.impl;

import java.util.Arrays;
import java.util.Iterator;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.randomz.Hash;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.RepeatedElementIterator;
import mikera.vectorz.impl.ZeroVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Lightweight immutable zero matrix class
 */
public final class ZeroMatrix extends ABooleanMatrix implements IFastRows, IFastColumns {
	private final int inputDimensions;
	private final int outputDimensions;

	@Override public 
	boolean isFullyMutable() {
		return false;
	}
	
	private ZeroMatrix(int rows, int columns) {
		outputDimensions=rows;
		inputDimensions=columns;
	}
	
	public static ZeroMatrix create(int rows, int columns) {
		return new ZeroMatrix(rows,columns);
	}
	
	@Override
	public boolean isSquare() {
		return inputDimensions==outputDimensions;
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
	public void multiply(double factor) {
		// no change - should maybe be an exception because immutable?
	}
	
	@Override
	public ZeroVector getRow(int row) {
		return ZeroVector.create(inputDimensions);
	}
	
	@Override
	public ZeroVector getColumn(int col) {
		return ZeroVector.create(outputDimensions);
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
	public void getElements(double[] dest, int destOffset) {
		Arrays.fill(dest, destOffset,destOffset+rowCount()*columnCount(),0.0);
	}

	@Override
	public int rowCount() {
		return outputDimensions;
	}

	@Override
	public int columnCount() {
		return inputDimensions;
	}
	
	@Override
	public double determinant() {
		if(isSquare()) throw new UnsupportedOperationException(ErrorMessages.squareMatrixRequired(this));
		return 0.0;
	}
	
	@Override
	public double trace() {
		return 0.0;
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		assert(i>=0);
		assert(i<outputDimensions);
		return 0.0;
	}

	@Override
	public double get(int row, int column) {
		if ((row<0)||(row>=outputDimensions)||(column<0)||(column>=inputDimensions)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		}
		return 0.0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
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
		return Matrixx.newMatrix(outputDimensions, inputDimensions);
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
	public long nonZeroCount() {
		return 0;
	}
	
	@Override 
	public int hashCode() {
		return Hash.zeroVectorHash(inputDimensions*outputDimensions);
	}
	
	@Override
	public void transform(AVector input, AVector output) {
		assert(output.length()==outputDimensions);
		output.fill(0.0);
	}
	
	@Override
	public boolean isInvertible() {
		return false;
	}
	
	@Override
	public AVector asVector() {
		return ZeroVector.create(inputDimensions*outputDimensions);
	}
	
	@Override
	public AMatrix innerProduct(AMatrix m) {
		assert(columnCount()==m.rowCount());
		return ZeroMatrix.create(outputDimensions, m.columnCount());
	}
	
	@Override 
	public void elementMul(AMatrix m) {
		// do nothing, already zero!
	}

	@Override
	public boolean equals(AMatrix m) {
		return m.isZero();
	}
	
	@Override
	public ZeroMatrix getTranspose() {
		if (inputDimensions==outputDimensions) return this;
		return ZeroMatrix.create(inputDimensions, outputDimensions);
	}
	
	@Override
	public Matrix toMatrix() {
		return Matrix.create(rowCount(), columnCount());
	}
	
	@Override
	public Matrix toMatrixTranspose() {
		return Matrix.create(columnCount(), rowCount());
	}
	
	@Override
	public AVector getLeadingDiagonal() {
		return ZeroVector.create(inputDimensions);
	}
	
	@Override
	public AMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		return ZeroMatrix.create(rows, cols);
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		return new RepeatedElementIterator(inputDimensions*outputDimensions,0.0);
	}
	
	@Override
	public ZeroMatrix exactClone() {
		return new ZeroMatrix(outputDimensions,inputDimensions);
	}
}
