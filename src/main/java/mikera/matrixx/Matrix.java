package mikera.matrixx;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.INDArray;
import mikera.matrixx.algo.Multiplications;
import mikera.matrixx.impl.ADenseArrayMatrix;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.AStridedVector;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.StridedElementIterator;
import mikera.vectorz.impl.StridedVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/** 
 * Standard MxN matrix class backed by a densely packed double[] array
 * 
 * This is the most efficient Vectorz type for 2D matrices.
 * 
 * @author Mike
 */
public final class Matrix extends ADenseArrayMatrix {
	private static final long serialVersionUID = -3260581688928230431L;

	private Matrix(int rowCount, int columnCount) {
		this(rowCount,columnCount,createStorage(rowCount,columnCount));
	}
	
	/**
	 * Creates a new zero-filled matrix of the specified shape.
	 */
	public static Matrix create(int rowCount, int columnCount) {
		return new Matrix(rowCount,columnCount);
	}
	
	public static Matrix create(AMatrix m) {
		return new Matrix(m.rowCount(),m.columnCount(),m.toDoubleArray());
	}
	
	public Matrix(AMatrix m) {
		this(m.rowCount(),m.columnCount(),m.toDoubleArray());
	}
	
	public static double[] createStorage(int rowCount,int columnCount) {
		long elementCount=((long)rowCount)*columnCount;
		int ec=(int)elementCount;
		if (ec!=elementCount) throw new IllegalArgumentException(ErrorMessages.tooManyElements(rowCount,columnCount));
		return new double[ec];
	}
	
	public static Matrix createRandom(int rows, int cols) {
		Matrix m=create(rows,cols);
		double[] d=m.data;
		for (int i=0; i<d.length; i++) {
			d[i]=Math.random();
		}
		return m;
	}
	
	public static Matrix create(INDArray m) {
		if (m.dimensionality()!=2) throw new IllegalArgumentException("Can only create matrix from 2D array");
		int rows=m.getShape(0);
		int cols=m.getShape(1);
		return Matrix.wrap(rows, cols, m.toDoubleArray());		
	}
	
	public static Matrix create(Object... rowVectors) {
		List<AVector> vs=new ArrayList<AVector>();
		for (Object o:rowVectors) {
			vs.add(Vectorz.create(o));
		}
		AMatrix m=VectorMatrixMN.create(vs);
		return create(m);
	}
	
	public static Matrix create(double[][] data) {
		int rows = data.length;
		int cols = data[0].length;
		Matrix m = Matrix.create(rows, cols);
		for (int i = 0; i < rows; i++) {
			double[] drow=data[i];
			if (drow.length!=cols) {
				throw new IllegalArgumentException("Array shape is not rectangular! Row "+i+" has length "+drow.length);
			}
			System.arraycopy(drow, 0, m.data, i * cols, cols);
		}
		return m;
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public boolean isView() {
		return false;
	}
	
	@Override
	public boolean isBoolean() {
		return DoubleArrays.isBoolean(data,0,data.length);
	}
	
	@Override
	public boolean isZero() {
		return DoubleArrays.isZero(data,0,data.length);
	}
	
	@Override
	public boolean isPackedArray() {
		return true;
	}
	
	private Matrix(int rowCount, int columnCount, double[] data) {
		super(data,rowCount,columnCount);
	}
	
	public static Matrix wrap(int rowCount, int columnCount, double[] data) {
		if (data.length!=rowCount*columnCount) throw new IllegalArgumentException("data array is of wrong size: "+data.length);
		return new Matrix(rowCount,columnCount,data);
	}
	
	@Override
	public AStridedMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		if ((rowStart<0)||(rowStart>=this.rows)||(colStart<0)||(colStart>=this.cols)) throw new IndexOutOfBoundsException("Invalid submatrix start position");
		if ((rowStart+rows>this.rows)||(colStart+cols>this.cols)) throw new IndexOutOfBoundsException("Invalid submatrix end position");
		return StridedMatrix.wrap(data, rows, cols, 
				rowStart*rowStride()+colStart*columnStride(), 
				rowStride(), columnStride());
	}
	
	@Override
	public Vector innerProduct(AVector a) {
		if (a instanceof Vector) return innerProduct((Vector)a);
		return transform(a);
	}
	
	@Override
	public Matrix innerProduct(Matrix a) {
		return Multiplications.multiply(this, a);
	}

	@Override
	public Matrix innerProduct(AMatrix a) {
		// TODO: consider transposing a into packed arrays?
		if (a instanceof Matrix) {
			return innerProduct((Matrix)a);
		}
		if ((this.columnCount()!=a.rowCount())) {
			throw new IllegalArgumentException(ErrorMessages.mismatch(this, a));
		}
		return Multiplications.multiply(this, a);
//		int rc=this.rowCount();
//		int cc=a.columnCount();
//		int ic=this.columnCount();
//		Matrix result=Matrix.create(rc,cc);
//		for (int i=0; i<rc; i++) {
//			int toffset=ic*i;
//			for (int j=0; j<cc; j++) {
//				double acc=0.0;
//				for (int k=0; k<ic; k++) {
//					acc+=data[toffset+k]*a.unsafeGet(k, j);
//				}
//				result.unsafeSet(i,j,acc);
//			}
//		}
//		return result;
	}
	
	@Override
	public double elementSum() {
		return DoubleArrays.elementSum(data);
	}
	
	@Override
	public double elementSquaredSum() {
		return DoubleArrays.elementSquaredSum(data);
	}
	
	@Override
	public double elementMax(){
		return DoubleArrays.elementMax(data);
	}
	
	@Override
	public double elementMin(){
		return DoubleArrays.elementMin(data);
	}
	
	@Override
	public void abs() {
		DoubleArrays.abs(data);
	}
	
	@Override
	public void signum() {
		DoubleArrays.signum(data);
	}
	
	@Override
	public void square() {
		DoubleArrays.square(data);
	}
	
	@Override
	public void exp() {
		DoubleArrays.exp(data);
	}
	
	@Override
	public void log() {
		DoubleArrays.log(data);
	}
	
	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(data);
	}
	
	@Override
	public Matrix clone() {
		return new Matrix(rows,cols,DoubleArrays.copyOf(data));
	}
	
	@Override
	public final void copyRowTo(int row, double[] dest, int destOffset) {
		int srcOffset=row*cols;
		System.arraycopy(data, srcOffset, dest, destOffset, cols);
	}
	
	@Override
	public final void copyColumnTo(int col, double[] dest, int destOffset) {
		int colOffset=col;
		for (int i=0;i<rows; i++) {
			dest[destOffset+i]=data[colOffset+i*cols];
		}
	}

	@Override
	public Vector transform (AVector a) {
		Vector v=Vector.createLength(rows);
		double[] vdata=v.getArray();
		for (int i=0; i<rows; i++) {
			vdata[i]=a.dotProduct(data, i*cols);
		}
		return v;
	}
	
	@Override
	public Vector transform (Vector a) {
		Vector v=Vector.createLength(rows);
		transform(a,v);
		return v;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		if ((source instanceof Vector )&&(dest instanceof Vector)) {
			transform ((Vector)source, (Vector)dest);
			return;
		}
		if(rows!=dest.length()) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		if(cols!=source.length()) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		for (int i=0; i<rows; i++) {
			dest.unsafeSet(i,source.dotProduct(data, i*cols));
		}
	}
	
	@Override
	public void transform(Vector source, Vector dest) {
		int rc = rowCount();
		int cc = columnCount();
		if (source.length()!=cc) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		if (dest.length()!=rc) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		int di=0;
		double[] sdata=source.getArray();
		double[] ddata=dest.getArray();
		for (int row = 0; row < rc; row++) {
			double total = 0.0;
			for (int column = 0; column < cc; column++) {
				total += data[di+column] * sdata[column];
			}
			di+=cc;
			ddata[row]=total;
		}
	}
	
	@Override
	public ArraySubVector getRowView(int row) {
		return ArraySubVector.wrap(data,row*cols,cols);
	}
	
	@Override
	public AStridedVector getColumnView(int col) {
		if (cols==1) {
			if (col!=0) throw new IndexOutOfBoundsException("Column does not exist: "+col);
			return Vector.wrap(data);
		} else {
			return StridedVector.wrap(data,col,rows,cols);
		}
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
	public final Matrix toMatrix() {
		return this;
	}
	
	@Override
	public final double[] toDoubleArray() {
		return DoubleArrays.copyOf(data);
	}
	
	@Override
	public Matrix toMatrixTranspose() {
		int rc = rowCount();
		int cc = columnCount();
		Matrix m = Matrix.create(cc, rc);
		double[] targetData=m.data;
		for (int j=0; j<cc; j++) {
			copyColumnTo(j,targetData,j*rc);
		}
		return m;
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(data);
	}
	
	@Override
	public double[] asDoubleArray() {
		return data;
	}

	@Override
	public double get(int row, int column) {
		if ((column<0)||(column>=cols)) throw new IndexOutOfBoundsException();
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
	public void addAt(int i, int j, double d) {
		data[(i*cols)+j]+=d;
	}

  public void addAt(int i, double d) {
    data[i] += d;
  }

  public void minusAt(int i, double d) {
    data[i] -= d;
  }

  public void divideAt(int i, double d) {
    data[i] /= d;
  }

  public void multiplyAt(int i, double d) {
    data[i] *= d;
  }

  @Override
	public void set(int row, int column, double value) {
		if ((column<0)||(column>=cols)) throw new IndexOutOfBoundsException();
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
		if ((rowCount()!=m.rowCount())||(columnCount()!=m.columnCount())) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, m));
		DoubleArrays.add(data, m.data);
	}

	@Override
	public void addMultiple(AMatrix m,double factor) {
		if (m instanceof Matrix) {addMultiple((Matrix)m,factor); return;}
		int rc=rowCount();
		int cc=columnCount();
		if (!((rc==m.rowCount())&&(cc==m.columnCount()))) throw new IllegalArgumentException(ErrorMessages.mismatch(this, m));

		for (int i=0; i<rc; i++) {
			m.getRow(i).addMultipleToArray(factor, 0, data, i*cols, cc);
		}
	}
	
	@Override
	public void add(double d) {
		DoubleArrays.add(data, d);
	}
	
	@Override
	public void add(AMatrix m) {
		if (m instanceof Matrix) {add((Matrix)m); return;}
		int rc=rowCount();
		int cc=columnCount();
		if (!((rc==m.rowCount())&&(cc==m.columnCount()))) {
			throw new IllegalArgumentException(ErrorMessages.mismatch(this, m));
		}
		m.addToArray(data,0);
	}
	
	@Override
	public void multiply(double factor) {
		for (int i=0; i<data.length; i++) {
			data[i]*=factor;
		}
	}
	
	@Override
	public void set(AMatrix a) {
		if ((rowCount()!=a.rowCount())||(columnCount()!=a.columnCount())) {
			throw new IllegalArgumentException(ErrorMessages.mismatch(this, a));
		}
		a.getElements(this.data, 0);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		System.arraycopy(data, 0, dest, offset, data.length);
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		return new StridedElementIterator(data,0,rows*cols,1);
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
		if (row.length()!=cc) throw new IllegalArgumentException(ErrorMessages.mismatch(this.getRow(i), row));
		row.getElements(data, i*cc);
	}
	
	@Override
	public void setColumn(int j, AVector col) {
		int rc=rowCount();
		if (col.length()!=rc) throw new IllegalArgumentException(ErrorMessages.mismatch(this.getColumn(j), col));
		for (int i=0; i<rc; i++) {
			data[index(i,j)]=col.unsafeGet(j);
		}
	}
	
	@Override
	public StridedVector getBand(int band) {
		int cc=columnCount();
		int rc=rowCount();
		if ((band>cc)||(band<-rc)) throw new IndexOutOfBoundsException(ErrorMessages.invalidBand(this, band));
		return StridedVector.wrap(data, (band>=0)?band:(-band)*cc, bandLength(band), cc+1);
	}
	
	@Override
	protected final int index(int row, int col) {
		return row*cols+col;
	}

	@Override
	public int getArrayOffset() {
		return 0;
	}

	@Override
	public double[] getArray() {
		return data;
	}

}
