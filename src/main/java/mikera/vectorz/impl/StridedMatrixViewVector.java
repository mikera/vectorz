package mikera.vectorz.impl;

import mikera.matrixx.impl.StridedMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Tools;
import mikera.vectorz.util.DoubleArrays;

public final class StridedMatrixViewVector extends AArrayVector {
	private static final long serialVersionUID = 5807998427323932401L;
	
	private final int offset;
	private final int rowStride;
	private final int colStride;
	private final int rows;
	private final int cols;
	
	public StridedMatrixViewVector(StridedMatrix m) {
		this(m.getArray(),m.getArrayOffset(),m.rowCount(),m.columnCount(),m.getStride(0),m.getStride(1));
	}
	
	private StridedMatrixViewVector(double[] data, int offset, int rows, int cols, int rowStride, int colStride) {
		super(Tools.toInt(rows*cols),data);
		this.offset=offset;
		this.rows=rows;
		this.cols=cols;
		this.rowStride=rowStride;
		this.colStride=colStride;
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
			DoubleArrays.add(data, srcOffset, colStride, dest, destOffset, cols);
			destOffset+=cols; 
			srcOffset+=rowStride;
		}
	}
	
	@Override
	public AVector subVector(int start, int length) {
		checkRange(start,length);
		if (((length%cols)==0)&&((length%cols)==0)) {
			int rowStart=(start/cols);
			int rs=(length/cols);
			return new StridedMatrixViewVector(data,offset+rowStart*rowStride,rs,cols,rowStride,colStride);
		}
		return super.subVector(start, length);
	}
}
