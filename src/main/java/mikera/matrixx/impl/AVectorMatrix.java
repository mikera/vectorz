package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract base class for matrices that use a collection of Vectors 
 * as storage for the matrix rows.
 * 
 * Vector matrices support appending with new rows - this functionality can be useful
 * e.g. when building a matrix to represent a data set.
 * 
 * @author Mike
 *
 */
public abstract class AVectorMatrix<T extends AVector> extends AMatrix {
	/* ================================
	 * Abstract interface
	 */
	
	public abstract void appendRow(AVector row);
	
	/**
	 * Gets a row of the matrix. Should be guaranteed to be an existing vector by all
	 * descendents of VectorMatrix.
	 */
	@Override
	public abstract T getRow(int row);

	@Override
	public double get(int row, int column) {
		return getRow(row).get(column);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return getRow(row).unsafeGet(column);
	}
	
	@Override
	public boolean isFullyMutable() {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			if (!getRow(i).isFullyMutable()) return false;
		}
		return true;
	}
	
	@Override 
	public void set(double value) {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			getRow(i).set(value);
		}
	}

	@Override
	public void set(int row, int column, double value) {
		getRow(row).set(column,value);
	}
	
	@Override
	public void unsafeSet(int row, int column, double value) {
		getRow(row).unsafeSet(column,value);
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		if ((source instanceof Vector )&&(dest instanceof Vector)) {
			transform ((Vector)source, (Vector)dest);
			return;
		}
		int rc=rowCount();
		if (rc!=dest.length()) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		for (int i=0; i<rc; i++) {
			dest.unsafeSet(i,getRow(i).dotProduct(source));
		}
	}
	
	@Override
	public void transform(Vector source, Vector dest) {
		int rc=rowCount();
		if (rc!=dest.length()) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		for (int i=0; i<rc; i++) {
			dest.unsafeSet(i,getRow(i).dotProduct(source));
		}
	}
	
	@Override
	public double calculateElement(int i, AVector inputVector) {
		T row=getRow(i);
		return row.dotProduct(inputVector);
	}
	
	@Override
	public final void copyRowTo(int row, double[] dest, int destOffset) {
		getRow(row).getElements(dest, destOffset);
	}
	
	
	@Override
	public final void getElements(double[] dest, int destOffset) {
		int rc=rowCount();
		int cc=columnCount();
		for (int i=0; i<rc; i++) {
			getRow(i).getElements(dest, destOffset);
			destOffset+=cc;
		}
	}
	
	@Override
	public void applyOp(Op op) {
		int rc = rowCount();
		for (int i = 0; i < rc; i++) {
			getRow(i).applyOp(op);
		}
	}
	
	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public boolean isZero() {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			if (!getRow(i).isZero()) return false;
		}
		return true;
	}
	
	@Override
	public double elementSum() {
		int rc=rowCount();
		double result=0.0;
		for (int i=0; i<rc; i++) {
			result+=getRow(i).elementSum();
		}
		return result;
	}
	
	@Override
	public double elementSquaredSum() {
		int rc=rowCount();
		double result=0.0;
		for (int i=0; i<rc; i++) {
			result+=getRow(i).elementSquaredSum();
		}
		return result;
	}
	
	@Override
	public long nonZeroCount() {
		int rc=rowCount();
		long result=0;
		for (int i=0; i<rc; i++) {
			result+=getRow(i).nonZeroCount();
		}
		return result;
	}	
	
	@Override
	public AMatrix clone() {
		AMatrix avm= super.clone();
		return avm;
	}
}
