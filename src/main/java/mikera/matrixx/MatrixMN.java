package mikera.matrixx;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.util.VectorzException;

/** 
 * Standard MxN matrix class backed by a flat double[] array
 * 
 * @author Mike
 */
public final class MatrixMN extends AMatrix {
	private final int rows;
	private final int columns;
	public final double[] data;
	
	public MatrixMN(int rowCount, int columnCount) {
		this(rowCount,columnCount,new double[rowCount*columnCount]);
	}
	
	public MatrixMN(AMatrix m) {
		this(m.rowCount(),m.columnCount());
		set(m);
	}
	
	private MatrixMN(int rowCount, int columnCount, double[] data) {
		this.rows=rowCount;
		this.columns=columnCount;
		this.data=data;
	}
	
	public static MatrixMN wrap(int rowCount, int columnCount, double[] data) {
		if (data.length!=rowCount*columnCount) throw new VectorzException("data array is of wrong size: "+data.length);
		return new MatrixMN(rowCount,columnCount,data);
	}
	
	@Override
	public AMatrix compose(AMatrix a) {
		if ((this.columnCount()!=a.rowCount())) {
			throw new VectorzException("Matrix sizes not compatible!");
		}
		int rc=this.rowCount();
		int cc=a.columnCount();
		int ic=this.columnCount();
		AMatrix result=Matrixx.newMatrix(rc,cc);
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				double acc=0.0;
				for (int k=0; k<ic; k++) {
					acc+=this.get(i, k)*a.get(k, j);
				}
				result.set(i,j,acc);
			}
		}
		return result;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		assert(rowCount()==dest.length());
		assert(columnCount()==source.length());
		int index=0;
		for (int i=0; i<rows; i++) {
			double acc=0.0;
			for (int j=0; j<columns; j++) {
				acc+=data[index++]*source.get(j);
			}
			dest.set(i,acc);
		}
	}
	
	@Override
	public ArraySubVector getRow(int row) {
		return ArraySubVector.wrap(data,row*columns,columns);
	}

	@Override
	public int rowCount() {
		return rows;
	}

	@Override
	public int columnCount() {
		return columns;
	}
	
	@Override
	public void swapRows(int i, int j) {
		if (i == j) return;
		int a = i*columns;
		int b = j*columns;
		int cc = columnCount();
		for (int k = 0; k < cc; k++) {
			double t = data[a+k];
			data[a+k]=data[b+k];
			data[b+k]=t;
		}
	}
	
	@Override
	public Vector asVector() {
		return Vector.wrap(data);
	}

	@Override
	public double get(int row, int column) {
		return data[(row*columns)+column];
	}

	@Override
	public void set(int row, int column, double value) {
		data[(row*columns)+column]=value;
	}
	
	@Override
	public void scale(double factor) {
		for (int i=0; i<data.length; i++) {
			data[i]*=factor;
		}
	}
	
	@Override
	public void set(AMatrix a) {
		int rc = rowCount();
		assert(rc==a.rowCount());
		int cc = columnCount();
		assert(cc==a.columnCount());
		for (int row = 0; row < rc; row++) {
			for (int column = 0; column < cc; column++) {
				set(row, column, a.get(row, column));
			}
		}
	}
}
