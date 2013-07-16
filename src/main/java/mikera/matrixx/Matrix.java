package mikera.matrixx;

import java.nio.DoubleBuffer;
import java.util.Arrays;

import mikera.matrixx.impl.StridedMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.StridedVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.VectorzException;

/** 
 * Standard MxN matrix class backed by a flat double[] array
 * 
 * @author Mike
 */
public final class Matrix extends AMatrix {
	private final int rows;
	private final int columns;
	public final double[] data;
	
	public Matrix(int rowCount, int columnCount) {
		this(rowCount,columnCount,new double[rowCount*columnCount]);
	}
	
	public static Matrix create(int rowCount, int columnCount) {
		return new Matrix(rowCount,columnCount);
	}
	
	public static Matrix create(AMatrix m) {
		Matrix nm=new Matrix(m.rowCount(),m.columnCount());
		nm.set(m);
		return nm;
	}
	
	public Matrix(AMatrix m) {
		this(m.rowCount(),m.columnCount());
		set(m);
	}
	
	public Matrix(Object... vs) {
		AMatrix m=Matrixx.create(vs);
		rows=m.rowCount();
		columns=m.columnCount();
		data=new double[rows*columns];
		set(m);
	}
	
	@Override
	public boolean isView() {
		return false;
	}
	
	public boolean isPackedArray() {
		return true;
	}
	
	private Matrix(int rowCount, int columnCount, double[] data) {
		this.rows=rowCount;
		this.columns=columnCount;
		this.data=data;
	}
	
	public static Matrix wrap(int rowCount, int columnCount, double[] data) {
		if (data.length!=rowCount*columnCount) throw new VectorzException("data array is of wrong size: "+data.length);
		return new Matrix(rowCount,columnCount,data);
	}
	
	public Matrix innerProduct(Matrix a) {
		if ((this.columnCount()!=a.rowCount())) {
			throw new VectorzException("Matrix sizes not compatible!");
		}
		int rc=this.rowCount();
		int cc=a.columnCount();
		int ic=this.columnCount();
		Matrix result=Matrix.create(rc,cc);
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
	public Matrix innerProduct(AMatrix a) {
		if (a instanceof Matrix) {
			return innerProduct((Matrix)a);
		}
		if ((this.columnCount()!=a.rowCount())) {
			throw new VectorzException("Matrix sizes not compatible!");
		}
		int rc=this.rowCount();
		int cc=a.columnCount();
		int ic=this.columnCount();
		Matrix result=Matrix.create(rc,cc);
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
	public double elementSum() {
		return DoubleArrays.elementSum(data, 0, data.length);
	}
	
	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(data, 0, data.length);
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
	public AVector getColumn(int row) {
		return StridedVector.wrap(data,row,rows,columns);
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
	public void multiplyRow(int i, double factor) {
		int offset=i*columns;
		for (int j=0; j<columns; j++) {
			data[offset+j]*=factor;
		}
	}
	
	@Override
	public void addRowMultiple(int src, int dst, double factor) {
		int soffset=src*columns;
		int doffset=dst*columns;
		for (int j=0; j<columns; j++) {
			data[doffset+j]+=factor*data[soffset+j];
		}
	}
	
	@Override
	public Vector asVector() {
		return Vector.wrap(data);
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(data,0,data.length);
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
	public void applyOp(Op op) {
		op.applyTo(data);
	}
	
	public void addMultiple(Matrix m,double factor) {
		assert(rowCount()==m.rowCount());
		assert(columnCount()==m.columnCount());
		for (int i=0; i<data.length; i++) {
			data[i]+=m.data[i]*factor;
		}
	}
	
	public void add(Matrix m) {
		assert(rowCount()==m.rowCount());
		assert(columnCount()==m.columnCount());
		for (int i=0; i<data.length; i++) {
			data[i]+=m.data[i];
		}
	}

	@Override
	public void addMultiple(AMatrix m,double factor) {
		if (m instanceof Matrix) {addMultiple((Matrix)m,factor); return;}
		int rc=rowCount();
		int cc=columnCount();
		assert(rc==m.rowCount());
		assert(cc==m.columnCount());

		int di=0;
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				data[di++]+=m.get(i, j)*factor;
			}
		}
	}
	
	@Override
	public void add(AMatrix m) {
		if (m instanceof Matrix) {add((Matrix)m); return;}
		int rc=rowCount();
		int cc=columnCount();
		assert(rc==m.rowCount());
		assert(cc==m.columnCount());

		int di=0;
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				data[di++]+=m.get(i, j);
			}
		}
	}
	
	@Override
	public void multiply(double factor) {
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
		int di=0;
		for (int row = 0; row < rc; row++) {
			for (int column = 0; column < cc; column++) {
				data[di++]=a.get(row, column);
			}
		}
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		System.arraycopy(data, 0, dest, offset, data.length);
	}
	
	@Override
	public StridedMatrix getTranspose() {
		return StridedMatrix.wrap(data,columns,rows,0,1,columns);
	}
	
	@Override 
	public void set(double value) {
		Arrays.fill(data,value);
	}
	
	@Override
	public void reciprocal() {
		DoubleArrays.reciprocal(data,0,data.length);
	}
	
	@Override
	public void clamp(double min, double max) {
		DoubleArrays.clamp(data,0,data.length,min,max);
	}
	
	@Override
	public Matrix exactClone() {
		return new Matrix(this);
	}

}
