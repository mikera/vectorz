package mikera.matrixx.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract base class for matrices wrapping a dense (rows*cols) subset of a double[] array
 * @author Mike
 *
 */
public abstract class ADenseArrayMatrix extends AStridedMatrix implements IFastRows {
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
	public double elementSum() {
		return DoubleArrays.elementSum(data,getArrayOffset(), rows*cols);
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

}
