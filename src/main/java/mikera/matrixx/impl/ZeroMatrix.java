package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.transformz.ATransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Tools;

/**
 * Lightweight immutable zero matrix class
 */
public final class ZeroMatrix extends AMatrix {
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
	
	@Override
	public int inputDimensions() {
		return inputDimensions;
	}
	
	@Override
	public void scale(double factor) {
		// no change
	}

	@Override
	public int outputDimensions() {
		return outputDimensions;
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
		assert(isSquare());
		return 0.0;
	}
	
	@Override
	public double calculateComponent(int i, AVector v) {
		assert(i>=0);
		assert(i<outputDimensions);
		return 0.0;
	}

	@Override
	public double get(int row, int column) {
		return 0.0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException("ZeroMatrix is immutable!");
	}
	
	@Override
	public AMatrix clone() {
		return Matrixx.newMatrix(outputDimensions, inputDimensions);
	}
	
	@Override
	public boolean isZeroMatrix() {
		return true;
	}
	
	@Override 
	public int hashCode() {
		return Tools.zeroVectorHash(inputDimensions*outputDimensions);
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
	public AMatrix compose(ATransform t) {
		assert(inputDimensions()==t.outputDimensions());
		return ZeroMatrix.create(outputDimensions, t.inputDimensions());
	}
	
	@Override
	public AMatrix compose(AMatrix m) {
		assert(inputDimensions()==m.outputDimensions());
		return ZeroMatrix.create(outputDimensions, m.inputDimensions());
	}
	
	@Override
	public void composeWith(ATransform t) {
		assert(t.inputDimensions()==t.outputDimensions());
	}
	
	@Override
	public void composeWith(AMatrix t) {
		assert(t.inputDimensions()==t.outputDimensions());
	}
	
	@Override 
	public void entrywiseMul(AMatrix m) {
		// do nothing, already zero!
	}

	@Override
	public boolean equals(AMatrix m) {
		return m.isZeroMatrix();
	}
	
	@Override
	public ZeroMatrix getTranspose() {
		return ZeroMatrix.create(inputDimensions, outputDimensions);
	}

	public static ZeroMatrix create(int rows, int columns) {
		return new ZeroMatrix(rows,columns);
	}
}
