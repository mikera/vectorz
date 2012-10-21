package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public final class VectorMatrixMN extends AVectorMatrix {
	private final int rowCount;	
	private final int columnCount;	
	private final AVector[] rows;
	
	public VectorMatrixMN(int rowCount, int columnCount) {
		this.rows=new AVector[rowCount];
		this.rowCount=rowCount;
		this.columnCount=columnCount;
		for (int i=0; i<rowCount; i++) {
			rows[i]=Vectorz.newVector(columnCount);
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

	@Override
	public AVector getRow(int row) {
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
		return rows[row].get(column);
	}

	@Override
	public void set(int row, int column, double value) {
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
}
