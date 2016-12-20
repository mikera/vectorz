package mikera.matrixx.impl;

import mikera.arrayz.INDArray;
import mikera.matrixx.AMatrix;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;

/**
 * Abstract base class that delegates certain elementwise methods to a source matrix.
 * 
 * Suitable for implementing transposes and reshaping views that preserve all elements with a 1-1 mapping.
 * 
 * @author Mike
 */
abstract class ADelegatedMatrix extends ARectangularMatrix {
	private static final long serialVersionUID = 7424713597425793457L;

	protected final AMatrix source;
	
	protected ADelegatedMatrix(int rows, int cols,AMatrix source) {
		super(rows,cols);
		this.source=source;
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
	public boolean isBoolean() {
		return source.isBoolean();
	}
	
	@Override
	public abstract double get(int row, int column);
		
	@Override
	public abstract double unsafeGet(int row, int column);

	@Override
	public abstract void unsafeSet(int row, int column, double value);
	
	@Override
	public int componentCount() {
		return source.componentCount();
	}
	
	@Override
	public INDArray getComponent(int k) {
		return source.getComponent(k);
	}
	
	@Override
	public INDArray[] getComponents() {
		return source.getComponents();
	}
	
	@Override
	public void applyOp(Op op) {
		source.applyOp(op);
	}
	
	@Override
	public void applyOp(IOperator op) {
		source.applyOp(op);
	}
	
	@Override
	public void applyOp(Op2 op, double b) {
		source.applyOp(op,b);
	}

	@Override
	public void multiply(double factor) {
		source.multiply(factor);
	}
	
	@Override
	public void fill(double value) {
		source.fill(value);
	}
	
	@Override
	public void setSparse(double value) {
		source.setSparse(value);
	}
	
	@Override
	public void addSparse(double value) {
		source.addSparse(value);
	}
		
	@Override
	public double elementSum() {
		return source.elementSum();
	}
	
	@Override 
	public double elementMin() {
		return source.elementMin();
	}
	
	@Override 
	public double elementMax() {
		return source.elementMax();
	}
	
	@Override
	public long nonZeroCount() {
		return source.nonZeroCount();
	}
	
	@Override
	public void abs() {
		source.abs();
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
	public void signum() {
		source.signum();
	}
	
	@Override
	public void negate() {
		source.negate();
	}
	
	@Override
	public void log() {
		source.log();
	}
	
	@Override
	public void exp() {
		source.exp();
	}
	
	@Override
	public void reciprocal() {
		source.reciprocal();
	}
}
