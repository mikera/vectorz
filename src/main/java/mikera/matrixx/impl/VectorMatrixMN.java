package mikera.matrixx.impl;

import java.util.List;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.ErrorMessages;

/**
 * A matrix implemented as a composition of M length N vectors
 * @author Mike
 *
 */
public class VectorMatrixMN extends AVectorMatrix<AVector> {
	private static final long serialVersionUID = -3660730676103956050L;

	protected int rowCount;	
	protected final int columnCount;	
	protected AVector[] rows;
	
	public VectorMatrixMN(int rowCount, int columnCount) {
		this.rows=new AVector[rowCount];
		this.rowCount=rowCount;
		this.columnCount=columnCount;
		for (int i=0; i<rowCount; i++) {
			rows[i]=Vectorz.newVector(columnCount);
		}
	}
	
	protected VectorMatrixMN(AVector[] rows, int rowCount, int columnCount) {
		if (rows.length<rowCount) throw new IllegalArgumentException("Insufficient rows provided!");
		this.rows=rows;
		this.rowCount=rowCount;
		this.columnCount=columnCount;
	}
	
	protected VectorMatrixMN(AVector[] rows) {
		this(rows,rows.length,rows[0].length());
	}
	
	/**
	 * Create a matrix from a list of rows
	 * 
	 * @param rows
	 * @return
	 */
	public static VectorMatrixMN create(List<?> rows) {
		int rc = rows.size();
		AVector[] vs = new AVector[rc];
		for (int i = 0; i < rc; i++) {
			vs[i] = Vectorz.toVector(rows.get(i));
		}
		return VectorMatrixMN.wrap(vs);
	}
	
	public static VectorMatrixMN create(List<?> rows, int[] shape) {
		int rc = rows.size();
		AVector[] vs = new AVector[rc];
		for (int i = 0; i < rc; i++) {
			vs[i] = Vectorz.toVector(rows.get(i));
		}
		return VectorMatrixMN.wrap(vs,shape);
	}
		
	public static VectorMatrixMN wrap(AVector[] rows) {
		int rc=rows.length;
		int cc=rows[0].length();
		return new VectorMatrixMN(rows,rc,cc);
	}
	
	public static VectorMatrixMN wrap(AVector[] rows, int[] shape) {
		int rc=shape[0];
		if (rows.length!=rc) throw new IllegalArgumentException("Inconsistent row count!");
		return new VectorMatrixMN(rows,rc,shape[1]);
	}
	
	@Override
	public void multiply(double factor) {
		for (int i=0; i<rowCount; i++) {
			rows[i].scale(factor);
		}
	}
	
	@Override
	public void applyOp(Op op) {
		for (int i=0; i<rowCount; i++) {
			rows[i].applyOp(op);
		}
	}
	
	public static VectorMatrixMN create(AMatrix source) {
		int rc=source.rowCount();
		VectorMatrixMN m=new VectorMatrixMN(source.rowCount(),source.columnCount());
		for (int i=0; i<rc; i++) {
			m.rows[i].set(source.getRow(i));
		}
		return m;
	}
	
	public static VectorMatrixMN wrap(AMatrix source) {
		int rc=source.rowCount();
		int cc=source.columnCount();
		AVector[] rows=new AVector[rc];
		for (int i=0; i<rc; i++) {
			rows[i]=source.getRow(i);
		}
		return new VectorMatrixMN(rows,rc,cc);
	}
	
	private void ensureRowCapacity(int size) {
		if (size<=rows.length) return;
		int newSize=Math.max(size, rows.length*2);
		AVector[] newRows=new AVector[newSize];
		System.arraycopy(rows, 0, newRows, 0, rowCount);
		rows=newRows;
	}
	
	@Override 
	public void appendRow(AVector row) {
		ensureRowCapacity(rowCount+1);
		rows[rowCount++]=row;
	}
	
	@Override 
	public void replaceRow(int i, AVector row) {
		if ((i<0)||(i>=rowCount)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, i));
		rows[i]=row;
	}
	
	@Override
	public void swapRows(int i, int j) {
		if (i!=j) {
			AVector t=rows[i];
			rows[i]=rows[j];
			rows[j]=t;
		}
	}

	@Override
	public AVector getRowView(int row) {
		if (row>=rowCount) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, row));
		return rows[row];
	}

	@Override
	public int rowCount() {
		return rowCount;
	}

	@Override
	public int columnCount() {
		return columnCount;
	}
	
	@Override
	public double get(int row, int column) {
		checkIndex(row,column);
		return rows[row].unsafeGet(column);
	}

	@Override
	public void set(int row, int column, double value) {
		checkIndex(row,column);
		rows[row].unsafeSet(column,value);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return rows[row].unsafeGet(column);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		rows[row].unsafeSet(column,value);
	}
	
	@Override
	public void addAt(int i, int j, double d) {
		rows[i].addAt(j, d);
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		for (int i=0; i<rowCount; i++) {
			dest.unsafeSet(i,getRow(i).dotProduct(source));
		}
	}
	
	@Override
	public double calculateElement(int i, AVector inputVector) {
		assert(i<rowCount);
		AVector row=rows[i];
		return row.dotProduct(inputVector);
	}
	
	@Override
	public boolean isSquare() {
		return rowCount==columnCount;
	}
	
	@Override
	public Matrix clone() {
		return Matrixx.create(this);
	}
	
	@Override
	public VectorMatrixMN exactClone() {
		AVector[] newRows=rows.clone();
		for (int i=0; i<rowCount; i++) {
			newRows[i]=newRows[i].exactClone();
		}
		return new VectorMatrixMN(newRows,rowCount,columnCount);
	}
}
