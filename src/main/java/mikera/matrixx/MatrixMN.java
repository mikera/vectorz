package mikera.matrixx;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public final class MatrixMN extends AVectorMatrix {
	private final int rowCount;	
	private final int columnCount;	
	private final AVector[] rows;
	
	public MatrixMN(int rowCount, int columnCount) {
		this.rows=new AVector[rowCount];
		this.rowCount=rowCount;
		this.columnCount=columnCount;
		for (int i=0; i<rowCount; i++) {
			rows[i]=Vectorz.createLength(columnCount);
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
	public MatrixMN clone() {
		MatrixMN m=(MatrixMN) super.clone();
		for (int i=0; i<rowCount; i++) {
			m.rows[i]=m.rows[i].clone();
		}
		return m;
	}
}
