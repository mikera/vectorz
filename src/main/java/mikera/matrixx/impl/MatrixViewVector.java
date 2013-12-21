package mikera.matrixx.impl;

import java.util.List;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.ErrorMessages;

@SuppressWarnings("serial")
public class MatrixViewVector extends AMatrixSubVector {
	protected final AMatrix source;
	protected final int rows;
	protected final int columns;
	protected final int length;
	
	public MatrixViewVector(AMatrix a) {
		source=a;
		rows=a.rowCount();
		columns=a.columnCount();
		length=rows*columns;
	}

	@Override
	public int length() {
		return length;
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
		return new MatrixViewVector(source.exactClone());
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
}
