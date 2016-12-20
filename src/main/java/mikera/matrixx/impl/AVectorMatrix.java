package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract base class for matrices that use a collection of Vectors 
 * as storage for the matrix rows.
 * 
 * @author Mike
 */
public abstract class AVectorMatrix<T extends AVector> extends ARectangularMatrix implements IFastRows {
	private static final long serialVersionUID = -6838429336358726743L;

	protected AVectorMatrix(int rows, int cols) {
		super(rows, cols);
	}
	
	/* ================================
	 * Abstract interface
	 */
	
	/**
	 * Replaces a row in this matrix. The row must be of a type allowed by the matrix.
	 * 
	 * Detaches the current row: this will invalidate views over the matrix that include the original row.
	 * @param i
	 * @param row
	 */
	@Override
	public abstract void replaceRow(int i, AVector row);
	
	/**
	 * Gets a row of the matrix. 
	 * 
	 * Guaranteed to be an existing vector view by all descendants of AVectorMatrix.
	 */
	@Override
	public abstract T getRow(int row);
	
	@Override
	public final T getRowView(int i) {
		return getRow(i);
	}

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
			if (!getRowView(i).isFullyMutable()) return false;
		}
		return true;
	}
	
	@Override 
	public void fill(double value) {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			getRowView(i).fill(value);
		}
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		getRowView(row).unsafeSet(column,value);
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
	public double rowDotProduct(int i, AVector inputVector) {
		T row=getRow(i);
		return row.dotProduct(inputVector);
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		getRow(row).getElements(dest, destOffset);
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		checkColumn(col);
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			dest[destOffset++]=getRow(i).unsafeGet(col);
		}
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
			getRowView(i).applyOp(op);
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
	public boolean isBoolean() {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			if (!getRow(i).isBoolean()) return false;
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
	public AVector asVector() {
		int rc = rowCount();
		if (rc == 0) return Vector0.INSTANCE;
		if (rc == 1) return getRowView(0);
		
		int cc= columnCount();
		if (cc==1) return getColumn(0);

		AVector v = getRowView(0);
		for (int i = 1; i < rc; i++) {
			v = Vectorz.join(v, getRowView(i));
		}
		return v;
	}
	
	@Override
	public int componentCount() {
		return rows;
	}
	
	@Override
	public AVector getComponent(int i) {
		return getRow(i);
	}
	
	@Override
	public AVector innerProduct(AVector v) {
		int rc=rowCount();
		Vector r=Vector.createLength(rc);
		for (int i=0; i<rc; i++) {
			r.unsafeSet(i, getRow(i).dotProduct(v));
		}
		return r;
	}
	
	@Override
	public AMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		if ((rows==0)||(cols==0)) return ZeroMatrix.create(rows, cols);
		AVector[] newRows=new AVector[rows];
		for (int i=0; i<rows; i++) {
			newRows[i]=getRowView(i+rowStart).subVector(colStart,cols);
		}
		return VectorMatrixMN.wrap(newRows);
	}
	
	@Override
	public boolean equals(AMatrix m) {
		return equalsByRows(m);
	}
	
	@Override
	public AMatrix clone() {
		AMatrix avm= super.clone();
		return avm;
	}


}
