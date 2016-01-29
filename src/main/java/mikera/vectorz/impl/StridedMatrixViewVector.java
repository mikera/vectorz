package mikera.vectorz.impl;

import mikera.matrixx.impl.AStridedMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Tools;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.DoubleArrays;

public final class StridedMatrixViewVector extends AArrayVector {
	private static final long serialVersionUID = 5807998427323932401L;
	
	private final int offset;
	private final int rowStride;
	private final int colStride;
	private final int rows;
	private final int cols;
	
	private StridedMatrixViewVector(double[] data, int offset, int rows, int cols, int rowStride, int colStride) {
		super(Tools.toInt(rows*cols),data);
		this.offset=offset;
		this.rows=rows;
		this.cols=cols;
		this.rowStride=rowStride;
		this.colStride=colStride;
	}
	
	public StridedMatrixViewVector(AStridedMatrix m) {
		this(m.getArray(),m.getArrayOffset(),m.rowCount(),m.columnCount(),m.rowStride(),m.columnStride());
	}

	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public StridedMatrixViewVector exactClone() {
		double[] data=this.data.clone();
		return new StridedMatrixViewVector(data,offset,rows,cols,rowStride,colStride);
	}

	@Override
	protected int index(int i) {
		int r=i/cols;
		int c=i%cols;
		return offset+r*rowStride+c*colStride;
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return unsafeGet(i);
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		unsafeSet(i,value);
	}
	
	@Override
	public void set(AVector v) {
		v.checkLength(length);
		for (int i=0; i<rows; i++) {
			v.copyTo(i*cols, data, offset+rowStride*i, cols, colStride);
		}
	}
	
	@Override
	public double unsafeGet(int i) {
		return data[index(i)];
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		data[index(i)]=value;
	}

	@Override
	public double dotProduct(double[] xs, int offset) {
		double result=0.0;
		for (int i=0; i<rows; i++) {
			result+=DoubleArrays.dotProduct(xs, offset, this.data, this.offset+i*rowStride, colStride, cols);
		    offset+=cols;
		}
		return result;
	}
	
	@Override
	public double elementSum() {
		double result=0.0;
		for (int i=0; i<rows; i++) {
			result+=DoubleArrays.elementSum(data,offset+i*rowStride,colStride,cols);
		}
		return result;
	}
	
	@Override
	public void applyOp(Op op) {
		for (int i=0; i<rows; i++) {
			op.applyTo(data, offset+i*rowStride, colStride, cols);
		}
	}
	
	@Override
	public void getElements(double[] dest, int destOffset) {
		for (int i=0; i<rows; i++) {
			DoubleArrays.copy(this.data,this.offset+i*rowStride, colStride, dest, destOffset, cols);
			destOffset+=cols; // move to next dest line
		}
	}
	
	@Override
	public void addToArray(double[] dest, int destOffset) {
		int srcOffset=this.offset;
		for (int i=0; i<rows; i++) {
			DoubleArrays.add(data, srcOffset+i*rowStride, colStride, dest, destOffset+i*cols, cols);
		}
	}
	
	@Override
	public AVector subVector(int start, int length) {
		checkRange(start,length);
		if (((length%cols)==0)&&((length%cols)==0)) {
			int newRows=(length/cols);
			if (newRows==0) return Vector0.INSTANCE;
			if (newRows==rows) return this;
			
			int rowStart=(start/cols);
			int newOffset=offset+rowStart*rowStride;
			if (newRows==1) return Vectorz.wrapStrided(data, newOffset, cols, colStride);
			
			return new StridedMatrixViewVector(data,newOffset,newRows,cols,rowStride,colStride);
		}
		return super.subVector(start, length);
	}
}
