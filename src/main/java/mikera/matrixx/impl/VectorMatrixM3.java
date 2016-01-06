package mikera.matrixx.impl;

import mikera.transformz.marker.ISpecialisedTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector3;

/**
 * Specialised N*3 Matrix with Vector3 row components
 * 
 * @author Mike
 *
 */
public final class VectorMatrixM3 extends AVectorMatrix<Vector3>  implements ISpecialisedTransform  {
	private static final long serialVersionUID = -8148184725377519520L;

	private final Vector3[] rowData;
	
	public VectorMatrixM3(int rowCount) {
		super(rowCount,3);
		rowData=new Vector3[rowCount];
		for (int i=0; i<rowCount; i++) {
			rowData[i]=new Vector3();
		}
	}
	
	private VectorMatrixM3(Vector3[] rows) {
		super(rows.length,3);
		this.rowData=rows;
	}
	
	@Override
	public void multiply(double factor) {
		for (Vector3 vector:rowData) {
			vector.scale(factor);
		}
	}
	
	@Override
	public void applyOp(Op op) {
		for (Vector3 vector:rowData) {
			vector.applyOp(op);
		}
	}
	
	@Override
	public void replaceRow(int i, AVector row) {
		replaceRow(i,(Vector3)row);
	}
	
	public void replaceRow(int i, Vector3 row) {
		rowData[i]=row;
	}

	@Override
	public double get(int row, int column) {
		return rowData[row].get(column);
	}

	@Override
	public void set(int row, int column, double value) {
		rowData[row].set(column,value);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return rowData[row].unsafeGet(column);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		rowData[row].unsafeSet(column,value);
	}
	
	@Override
	public Vector3 getRow(int row) {
		return  rowData[row];
	}
	
	@Override
	public Vector3 getRowClone(int row) {
		return rowData[row].clone();
	}

	@Override
	public void transform(AVector source, AVector dest) {
		if (source instanceof Vector3) {transform((Vector3)source,dest); return;}
		super.transform(source,dest);
	}
	
	public void transform(Vector3 source, AVector dest) {
		for (int i=0; i<rows; i++) {
			dest.set(i,getRow(i).dotProduct(source));
		}
	}
	
	@Override
	public double rowDotProduct(int i, AVector inputVector) {
		Vector3 row=rowData[i];
		return row.dotProduct(inputVector);
	}
		
	@Override
	public VectorMatrixM3 clone() {
		VectorMatrixM3 m=new VectorMatrixM3(rowData.clone());
		for (int i=0; i<rows; i++) {
			m.rowData[i]=m.rowData[i].clone();
		}
		return m;
	}
	
	@Override
	public VectorMatrixM3 exactClone() {
		return clone();
	}

}
