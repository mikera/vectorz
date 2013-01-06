package mikera.matrixx.impl;

import mikera.transformz.marker.ISpecialisedTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector3;

/**
 * Specialised N*3 Matrix with Vector3 row components
 * 
 * @author Mike
 *
 */
public final class VectorMatrixM3 extends AVectorMatrix  implements ISpecialisedTransform  {
	private int rowCount;	
	private Vector3[] rows;
	
	public VectorMatrixM3(int rowCount) {
		this.rowCount=rowCount;
		rows=new Vector3[rowCount];
		for (int i=0; i<rowCount; i++) {
			rows[i]=new Vector3();
		}
	}
	
	private void ensureRowCapacity(int size) {
		if (size<=rows.length) return;
		int newSize=Math.max(size, rows.length*2);
		Vector3[] newRows=new Vector3[newSize];
		System.arraycopy(rows, 0, newRows, 0, rowCount);
		rows=newRows;
	}
	
	@Override
	public void scale(double factor) {
		for (Vector3 vector:rows) {
			vector.scale(factor);
		}
	}
	
	@Override 
	public void appendRow(AVector row) {
		if (row instanceof Vector3) {
			appendRow((Vector3)row);
		} else {
			appendRow(new Vector3(row));
		}
	}
	
	public void appendRow(Vector3 row) {
		ensureRowCapacity(rowCount+1);
		rows[rowCount++]=row;
	}
	
	private VectorMatrixM3(Vector3[] rows) {
		rowCount=rows.length;
		this.rows=rows;
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
	
	@Override
	public void transform(AVector source, AVector dest) {
		if (source instanceof Vector3) {transform((Vector3)source,dest); return;}
		super.transform(source,dest);
	}
	
	public void transform(Vector3 source, AVector dest) {
		for (int i=0; i<rowCount; i++) {
			dest.set(i,getRow(i).dotProduct(source));
		}
	}
	
	@Override
	public boolean isSquare() {
		return rowCount==3;
	}
	
	@Override
	public VectorMatrixM3 clone() {
		VectorMatrixM3 m=new VectorMatrixM3(rows.clone());
		for (int i=0; i<rowCount; i++) {
			m.rows[i]=m.rows[i].clone();
		}
		return m;
	}

}
