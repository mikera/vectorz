package mikera.matrixx.impl;

import java.util.List;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.impl.AMatrixViewVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised class for viewing a matrix as a flattened vector
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public class MatrixAsVector extends AMatrixViewVector {
	protected final int rows;
	protected final int columns;
	
	public MatrixAsVector(AMatrix a) {
		super(a,a.rowCount()*a.columnCount());
		rows=a.rowCount();
		columns=a.columnCount();
	}

	@Override
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return source.unsafeGet(i/columns, i%columns);
	}
	
	@Override
	public void set(int i, double value) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
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
		return new MatrixAsVector(source.exactClone());
	}
	
	@Override
	public void fill(double v) {
		source.fill(v);
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
	public void clamp(double min, double max) {
		source.clamp(min, max);
	}
	
	@Override
	public double elementSum() {
		return source.elementSum();
	}
	
	@Override
	public double magnitudeSquared() {
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
		int row=calcRow(start);
		if (row==calcRow(start+length-1)) {
			return source.getRowView(row).subVector(start-row*columns,length);
		}
		return super.subVector(start, length);
	}
}
