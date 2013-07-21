package mikera.matrixx;

import java.nio.DoubleBuffer;
import java.util.Arrays;

import mikera.matrixx.impl.AArrayMatrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.AStridedVector;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.StridedVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.VectorzException;

/** 
 * Standard MxN matrix class backed by a fully packed double[] array
 * 
 * @author Mike
 */
public final class Matrix extends AArrayMatrix {
	
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
	
	public Matrix create(Object... rowVectors) {
		AMatrix m=VectorMatrixMN.create(rowVectors);
		Matrix r=new Matrix(m.rowCount(),m.columnCount(),new double[rows*cols]);
		r.set(m);
		return r;
	}
	
	@Override
	public boolean isView() {
		return false;
	}
	
	@Override
	public boolean isPackedArray() {
		return true;
	}
	
	private Matrix(int rowCount, int columnCount, double[] data) {
		super(data,rowCount,columnCount);
	}
	
	public static Matrix wrap(int rowCount, int columnCount, double[] data) {
		if (data.length!=rowCount*columnCount) throw new VectorzException("data array is of wrong size: "+data.length);
		return new Matrix(rowCount,columnCount,data);
	}
	
	@Override
	public AArrayMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		if ((rowStart<0)||(rowStart>=rows)||(colStart<0)||(colStart>=cols)) throw new IndexOutOfBoundsException("Invalid submatrix start position");
		if ((rowStart+rows>this.rows)||(colStart+cols>this.cols)) throw new IndexOutOfBoundsException("Invalid submatrix end position");
		if ((rows<1)||(cols<1)) throw new IllegalArgumentException("Submatrix has no elements");
		return StridedMatrix.wrap(data, rows, cols, rowStart*cols+colStart, cols, 1);
	}
	
	@Override
	public Vector innerProduct(AVector a) {
		if (a instanceof Vector) return innerProduct((Vector)a);
		return transform(a);
	}
	
	public Vector innerProduct(Vector a) {
		int rc=rowCount();
		int cc=columnCount();
		if ((cc!=a.length())) {
			throw new VectorzException("Matrix sizes not compatible!");
		}		
		Vector result=Vector.createLength(rows);
		for (int i=0; i<rc; i++) {
			int di=i*cc;
			double acc=0.0;
			for (int j=0; j<cc; j++) {
				acc+=data[di+j]*a.data[j];
			}
			result.set(i,acc);
		}
		return result;
	}
	
	@Override
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
	public void abs() {
		DoubleArrays.abs(data, 0, data.length);
	}
	
	@Override
	public void signum() {
		DoubleArrays.signum(data, 0, data.length);
	}
	
	@Override
	public void square() {
		DoubleArrays.square(data, 0, data.length);
	}
	
	@Override
	public void exp() {
		DoubleArrays.exp(data, 0, data.length);
	}
	
	@Override
	public void log() {
		DoubleArrays.log(data, 0, data.length);
	}
	
	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(data, 0, data.length);
	}
	
	@Override
	public Matrix clone() {
		return new Matrix(rows,cols,data.clone());
	}
	
	@Override
	public Vector transform (AVector a) {
		Vector v=Vector.createLength(rows);
		for (int i=0; i<rows; i++) {
			v.data[i]=a.dotProduct(data, i*cols);
		}
		return v;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		assert(rowCount()==dest.length());
		assert(columnCount()==source.length());
		int index=0;
		for (int i=0; i<rows; i++) {
			double acc=0.0;
			for (int j=0; j<cols; j++) {
				acc+=data[index++]*source.get(j);
			}
			dest.set(i,acc);
		}
	}
	
	@Override
	public ArraySubVector getRow(int row) {
		return ArraySubVector.wrap(data,row*cols,cols);
	}
	
	@Override
	public AStridedVector getColumn(int col) {
		if (cols==1) {
			return Vector.wrap(data);
		} else {
			return StridedVector.wrap(data,col,rows,cols);
		}
	}

	@Override
	public int rowCount() {
		return rows;
	}

	@Override
	public int columnCount() {
		return cols;
	}
	
	@Override
	public void swapRows(int i, int j) {
		if (i == j) return;
		int a = i*cols;
		int b = j*cols;
		int cc = columnCount();
		for (int k = 0; k < cc; k++) {
			double t = data[a+k];
			data[a+k]=data[b+k];
			data[b+k]=t;
		}
	}
	
	@Override
	public void swapColumns(int i, int j) {
		if (i == j) return;
		int rc = rowCount();
		int cc = columnCount();
		for (int k = 0; k < rc; k++) {
			int x=k*cc;
			double t = data[i+x];
			data[i+x]=data[j+x];
			data[j+x]=t;
		}
	}
	
	@Override
	public void multiplyRow(int i, double factor) {
		int offset=i*cols;
		for (int j=0; j<cols; j++) {
			data[offset+j]*=factor;
		}
	}
	
	@Override
	public void addRowMultiple(int src, int dst, double factor) {
		int soffset=src*cols;
		int doffset=dst*cols;
		for (int j=0; j<cols; j++) {
			data[doffset+j]+=factor*data[soffset+j];
		}
	}
	
	@Override
	public Vector asVector() {
		return Vector.wrap(data);
	}
	
	@Override
	public Vector toVector() {
		return Vector.create(data);
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(data);
	}

	@Override
	public double get(int row, int column) {
		return data[(row*cols)+column];
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		data[(row*cols)+column]=value;
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return data[(row*cols)+column];
	}

	@Override
	public void set(int row, int column, double value) {
		data[(row*cols)+column]=value;
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
		if (!((rc==m.rowCount())&&(cc==m.columnCount()))) throw new IllegalArgumentException("Incompatoble matrix sizes");

		int di=0;
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				data[di++]+=m.unsafeGet(i, j)*factor;
			}
		}
	}
	
	@Override
	public void add(AMatrix m) {
		if (m instanceof Matrix) {add((Matrix)m); return;}
		int rc=rowCount();
		int cc=columnCount();
		if (!((rc==m.rowCount())&&(cc==m.columnCount()))) throw new IllegalArgumentException("Incompatoble matrix sizes");

		int di=0;
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				data[di++]+=m.unsafeGet(i, j);
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
		if (!(rc==a.rowCount())) throw new IllegalArgumentException("Non-matching row count");
		int cc = columnCount();
		if (!(cc==a.columnCount())) throw new IllegalArgumentException("Non-matching column count");
		a.getElements(this.data, 0);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		System.arraycopy(data, 0, dest, offset, data.length);
	}
	
	@Override
	public StridedMatrix getTranspose() {
		return StridedMatrix.wrap(data,cols,rows,0,1,cols);
	}
	
	@Override
	public StridedMatrix getTransposeView() {
		return StridedMatrix.wrap(data,cols,rows,0,1,cols);
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

	@Override
	public void setRow(int i, AVector row) {
		int cc=columnCount();
		if (row.length()!=cc) throw new IllegalArgumentException("Row has wrong length: "+row.length());
		row.getElements(data, i*cc);
	}
	
	@Override
	public void setColumn(int j, AVector col) {
		int rc=rowCount();
		if (col.length()!=rc) throw new IllegalArgumentException("Column has wrong length: "+col.length());
		for (int i=0; i<rc; i++) {
			data[i*cols+j]=col.unsafeGet(j);
		}
	}

}
