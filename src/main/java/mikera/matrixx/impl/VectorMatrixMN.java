package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vectorz;

/**
 * A matrix implemented as a composition of M length N vectors
 * @author Mike
 *
 */
public final class VectorMatrixMN extends AVectorMatrix {
	private int rowCount;	
	private final int columnCount;	
	private AVector[] rows;
	
	public VectorMatrixMN(int rowCount, int columnCount) {
		this.rows=new AVector[rowCount];
		this.rowCount=rowCount;
		this.columnCount=columnCount;
		for (int i=0; i<rowCount; i++) {
			rows[i]=Vectorz.newVector(columnCount);
		}
	}
	
	private VectorMatrixMN(AVector[] rows, int rowCount, int columnCount) {
		this.rows=rows;
		this.rowCount=rows.length;
		this.columnCount=columnCount;
	}
	
	@Override
	public void scale(double factor) {
		for (AVector vector:rows) {
			vector.scale(factor);
		}
	}
	
	@Override
	public void applyOp(Op op) {
		for (AVector v:rows) {
			v.applyOp(op);
		}
	}
	
	public VectorMatrixMN(AMatrix source) {
		this(source.rowCount(),source.columnCount());
		for (int i=0; i<rowCount; i++) {
			for (int j=0; j<columnCount; j++) {
				set(i,j,source.get(i, j));
			}
		}
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
	public AVector getRow(int row) {
		assert(row<rowCount);
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
		assert(row<rowCount);
		return rows[row].get(column);
	}

	@Override
	public void set(int row, int column, double value) {
		assert(row<rowCount);
		rows[row].set(column,value);
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		for (int i=0; i<rowCount; i++) {
			dest.set(i,getRow(i).dotProduct(source));
		}
	}
	
	@Override
	public boolean isSquare() {
		return rowCount==columnCount;
	}
	
	@Override
	public VectorMatrixMN clone() {
		VectorMatrixMN m=new VectorMatrixMN(rowCount,columnCount);
		for (int i=0; i<rowCount; i++) {
			m.rows[i].set(rows[i]);
		}
		return m;
	}
	
	@Override
	public VectorMatrixMN exactClone() {
		AVector[] newRows=rows.clone();
		for (int i=0; i<rowCount; i++) {
			newRows[i]=newRows[i].exactClone();
		}
		return new VectorMatrixMN(rows,rowCount,columnCount);
	}
}
