package mikera.matrixx;

import mikera.vectorz.Vector3;

public final class MatrixN3 extends VectorMatrix {
	private int rowCount;	
	private final Vector3[] rows;
	
	public MatrixN3(int rowCount) {
		this.rowCount=rowCount;
		rows=new Vector3[rowCount];
		for (int i=0; i<3; i++) {
			rows[i]=new Vector3();
		}
	}

	@Override
	public int rowCount() {
		return rowCount;
	}

	@Override
	public int columnCount() {
		return 3;
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
	public Vector3 getRow(int row) {
		return  rows[row];
	}

}
