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

	protected final AVector[] rowData;
	
	public VectorMatrixMN(int rowCount, int columnCount) {
		super(rowCount,columnCount);
		this.rowData=new AVector[rowCount];
		for (int i=0; i<rowCount; i++) {
			rowData[i]=Vectorz.newVector(columnCount);
		}
	}
	
	protected VectorMatrixMN(AVector... rows) {
		super(rows.length,(rows.length>0)?rows[0].length():0);
		this.rowData=rows;
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
		return VectorMatrixMN.wrap(vs);
	}
		
	public static VectorMatrixMN wrap(AVector[] rows) {
		return new VectorMatrixMN(rows);
	}
	
	@Override
	public void multiply(double factor) {
		for (int i=0; i<rows; i++) {
			rowData[i].scale(factor);
		}
	}
	
	@Override
	public void applyOp(Op op) {
		for (int i=0; i<rows; i++) {
			rowData[i].applyOp(op);
		}
	}
	
	public static VectorMatrixMN create(AMatrix source) {
		int rc=source.rowCount();
		VectorMatrixMN m=new VectorMatrixMN(source.rowCount(),source.columnCount());
		for (int i=0; i<rc; i++) {
			m.rowData[i].set(source.getRow(i));
		}
		return m;
	}
	
	public static VectorMatrixMN wrap(AMatrix source) {
		int rc=source.rowCount();
		AVector[] rows=new AVector[rc];
		for (int i=0; i<rc; i++) {
			rows[i]=source.getRowView(i);
		}
		return new VectorMatrixMN(rows);
	}
	
	@Override 
	public void replaceRow(int i, AVector row) {
		if ((i<0)||(i>=rows)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, i));
		rowData[i]=row;
	}
	
	@Override
	public void swapRows(int i, int j) {
		if (i!=j) {
			AVector t=rowData[i];
			rowData[i]=rowData[j];
			rowData[j]=t;
		}
	}

	@Override
	public AVector getRow(int row) {
		return rowData[row];
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
	public void addAt(int i, int j, double d) {
		rowData[i].addAt(j, d);
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		for (int i=0; i<rows; i++) {
			dest.unsafeSet(i,getRow(i).dotProduct(source));
		}
	}
	
	@Override
	public double rowDotProduct(int i, AVector inputVector) {
		AVector row=rowData[i];
		return row.dotProduct(inputVector);
	}
		
	@Override
	public Matrix clone() {
		return Matrixx.create(this);
	}
	
	@Override
	public VectorMatrixMN exactClone() {
		AVector[] newRows=rowData.clone();
		for (int i=0; i<rows; i++) {
			newRows[i]=newRows[i].exactClone();
		}
		return new VectorMatrixMN(newRows);
	}
}
