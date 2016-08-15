package mikera.matrixx.impl;

import mikera.arrayz.INDArray;
import mikera.arrayz.impl.IDenseArray;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op2;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.impl.AStridedVector;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract base class for matrices wrapping a dense (rows*cols) subset of a double[] array
 * @author Mike
 */
public abstract class ADenseArrayMatrix extends AStridedMatrix implements IFastRows, IDenseArray {
	private static final long serialVersionUID = -2144964424833585026L;

	protected ADenseArrayMatrix(double[] data, int rows, int cols) {
		super(data, rows, cols);
	}

	@Override
	public abstract int getArrayOffset();
	
	@Override
	public boolean isPackedArray() {
		return (getArrayOffset()==0) && (data.length ==(rows*cols));
	}
	
	@Override
	public boolean isZero() {
		return DoubleArrays.isZero(data, getArrayOffset(), rows*cols);
	}
	
	@Override
	public boolean isUpperTriangular() {
		// triangular test, taking into account cache layout to access via rows
		int rc=rowCount();
		int cc=columnCount();
		int offset=getArrayOffset();
		for (int i=1; i<rc; i++) {
			if (!DoubleArrays.isZero(data, offset+i*cc, Math.min(cc, i))) return false;
		}
		return true;
	}
	
	@Override
	public boolean isLowerTriangular() {
		// triangular test, taking into account cache layout to access via rows
		int offset=getArrayOffset();
		int cc=columnCount();
		int testRows=Math.min(cc, rowCount());
		for (int i=0; i<testRows; i++) {
			if (!DoubleArrays.isZero(data, offset+i+1, cc-i-1)) return false;
			offset+=cc;
		}
		return true;
	}
	
	@Override
	public int rowStride() {
		return cols;
	}
	
	@Override
	public int columnStride() {
		return 1;
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		return data[index(i,j)];
	}
	
	@Override
	public void set(AVector v) {
		int rc=rowCount();
		if (v.length()!=cols) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));
		if (rc==0) return;
		double[] data=getArray();
		int offset=getArrayOffset();
		for (int i=0; i<rc; i++) {
			v.getElements(data, offset+i*cols);
		}
	}
	
	@Override
	public void set(AMatrix m) {
		if (!isSameShape(m)) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, m));
		double[] data=getArray();
		int offset=getArrayOffset();
		m.getElements(data, offset);
	}
	
	@Override
	public void setElements(double[] values, int offset) {
		double[] data=getArray();
		int di=getArrayOffset();
		System.arraycopy(values, offset, data, di, Tools.toInt(elementCount()));
	}
	
	@Override
	public void setElements(int pos, double[] values, int offset, int length) {
		double[] data=getArray();
		int di=getArrayOffset()+pos;
		System.arraycopy(values, offset, data, di, length);
	}
	
	@Override
	public ADenseArrayVector getRow(int i) {
		checkRow(i);
		return ArraySubVector.wrap(data, getArrayOffset()+i*cols, cols);
	}
	
	@Override
	public AStridedVector getColumn(int i) {
		checkColumn(i);
		return Vectorz.wrapStrided(data, getArrayOffset()+i, rows, cols);
	}
	
	@Override
	public void setRow(int i, AVector row) {
		int cc = columnCount();
		row.checkLength(cc);
		row.getElements(data, getArrayOffset()+ i * cc);
	}
	
	@Override
	public double elementSum() {
		return DoubleArrays.elementSum(data,getArrayOffset(), rows*cols);
	}
	
	@Override
	public double elementSquaredSum() {
		return DoubleArrays.elementSquaredSum(data,getArrayOffset(), rows*cols);
	}
	
	@Override
	public double elementMax(){
		return DoubleArrays.elementMax(data,getArrayOffset(), rows*cols);
	}
	
	@Override
	public double elementMin(){
		return DoubleArrays.elementMin(data,getArrayOffset(), rows*cols);
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		System.arraycopy(data, getArrayOffset()+row*cols, dest, destOffset, cols);
	}
	
	@Override
	public void unsafeSet(int i, int j,double value) {
		data[index(i,j)]=value;
	}
	
	@Override
	protected int index(int row, int col) {
		return getArrayOffset()+(row*cols)+col;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		if ((source instanceof Vector )&&(dest instanceof Vector)) {
			transform ((Vector)source, (Vector)dest);
			return;
		}
		if(rows!=dest.length()) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		if(cols!=source.length()) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		double[] data=getArray();
		int offset=getArrayOffset();
		for (int i=0; i<rows; i++) {
			dest.unsafeSet(i,source.dotProduct(data, offset+ i*cols));
		}
	}
	
	@Override
	public void add(AVector v) {
		int rc=rowCount();
		int cc=columnCount();
		if(cc!=v.length()) throw new IllegalArgumentException(ErrorMessages.mismatch(this, v));
		double[] data=getArray();
		int offset=getArrayOffset();

		for (int i=0; i<rc; i++) {
			v.addToArray(data, offset+i*cc);
		}		
	}
	
	@Override
	public void add(AMatrix a) {
		if (!isSameShape(a)) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		}
		a.addToArray(getArray(), getArrayOffset());
	}
	
	public void add(ADenseArrayMatrix a, ADenseArrayMatrix b) {
		checkSameShape(a);
		checkSameShape(b);
		DoubleArrays.add2(getArray(), getArrayOffset(), a.getArray(), a.getArrayOffset(), b.getArray(), b.getArrayOffset(), Tools.toInt(this.elementCount()));
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		DoubleArrays.add(getArray(), getArrayOffset(), data, offset, rows*cols);
	}
	
	@Override
	public void addOuterProduct(AVector a, AVector b) {
		int rc= a.checkLength(rows);
		b.checkLength(cols);
		double[] data=getArray();
		int offset=getArrayOffset();
		for (int i=0; i<rc; i++) {
			b.addMultipleToArray(a.unsafeGet(i), data, offset+cols*i);
		}	
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		int offset=getArrayOffset();
		int n=rows*cols;
		return op.reduce(init,data,offset,n);
	}
	
	@Override
	public double rowDotProduct(int i, AVector a) {
		double[] data=getArray();
		int offset=getArrayOffset();
		return a.dotProduct(data, offset+i*cols);
	}
	
	@Override
	public boolean equals(AMatrix a) {
		if (!isSameShape(a)) return false;
		return a.equalsArray(getArray(), getArrayOffset());
	}
	
	@Override
	public boolean equals(INDArray a) {
		if (!isSameShape(a)) return false;
		return a.equalsArray(getArray(), getArrayOffset());
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.equals(getArray(), getArrayOffset(), data, offset, rows*cols);
	}
		
	@Override
	public ADenseArrayVector asVector() {
		return Vectorz.wrap(data, getArrayOffset(), rows*cols);
	}
}
