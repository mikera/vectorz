package mikera.vectorz.impl;

import java.util.List;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.IFastRows;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;

/**
 * Specialised class for viewing a matrix as a flattened vector
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public class MatrixViewVector extends AMatrixViewVector {
	protected final int rows;
	protected final int columns;
	
	public MatrixViewVector(AMatrix a) {
		super(a,a.rowCount()*a.columnCount());
		rows=a.rowCount();
		columns=a.columnCount();
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return source.unsafeGet(i/columns, i%columns);
	}
	
	@Override
	public void set(int i, double value) {
		checkIndex(i);
		source.unsafeSet(i/columns, i%columns,value);	
	}
	
	@Override
	public double unsafeGet(int i) {
		return source.unsafeGet(i/columns, i%columns);
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		source.unsafeSet(i/columns, i%columns,value);	
	}
	
	@Override
	public AVector exactClone() {
		return new MatrixViewVector(source.exactClone());
	}
	
	@Override
	public boolean isFullyMutable() {
		return source.isFullyMutable();
	}
	
	@Override
	public boolean isMutable() {
		return source.isMutable();
	}
	
	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public void fill(double value) {
		source.set(value);
	}
	
	@Override
	public boolean isSparse() {
		return source.isSparse();
	}
	
	@Override
	public boolean isZero() {
		return source.isZero();
	}

	@Override 
	public void getElements(double[] data, int offset) {
		source.getElements(data,offset);
	}
	
	@Override 
	public List<Double> asElementList() {
		return source.asElementList();
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		source.addToArray(data, offset);
	}
	
	@Override
	public void clamp(double min, double max) {
		source.clamp(min, max);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return source.equalsArray(data, offset);
	}
	
	@Override
	public double elementSum() {
		return source.elementSum();
	}
	
	@Override
	public double elementSquaredSum() {
		return source.elementSquaredSum();
	}
	
	@Override
	public void applyOp(Op op) {
		source.applyOp(op);
	}
	
	@Override
	public void abs() {
		source.abs();
	}
	
	@Override
	public void signum() {
		source.signum();
	}
	
	@Override
	public void negate() {
		source.negate();
	}
	
	@Override
	public void exp() {
		source.exp();
	}
	
	@Override
	public void log() {
		source.log();
	}
	
	@Override
	public void square() {
		source.square();
	}
	
	@Override
	public void sqrt() {
		source.sqrt();
	}
	
	@Override
	public void pow(double exponent) {
		source.pow(exponent);
	}

	@Override
	protected int calcRow(int i) {
		return i/columns;
	}

	@Override
	protected int calcCol(int i) {
		return i%columns;
	}
	
	@Override
	public AVector subVector(int start, int length) {
		int startRow=calcRow(start);
		int endRow=calcRow(start+length-1); 
		if (startRow==endRow) {
			return source.getRowView(startRow).subVector(start-startRow*columns,length);
		} else if ((startRow==endRow-1)&&(source instanceof IFastRows)) {
			int split=endRow*columns;
			return source.getRowView(startRow).subVector(start-startRow*columns,split-start)
					.join(source.getRowView(endRow).subVector(0, start+length-split));
		}
		return super.subVector(start, length);
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<rows; i++) {
			result+=source.getRow(i).dotProduct(data, offset+i*columns);
		}
		return result;
	}
}
