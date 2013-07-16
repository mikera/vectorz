package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;

public class StridedMatrix extends AMatrix {
	private final double[] data;
	private final int rowCount;
	private final int columnCount;
	private final int rowStride;
	private final int columnStride;
	private final int offset;
	
	private StridedMatrix(double[] data, int rowCount, int columnCount, int offset, int rowStride, int columnStride) {
		this.data=data;
		this.offset=offset;
		this.rowCount=rowCount;
		this.columnCount=columnCount;
		this.rowStride=rowStride;
		this.columnStride=columnStride;
	}
	
	public static StridedMatrix create(int rowCount, int columnCount) {
		double[] data = new double[rowCount*columnCount];
		return new StridedMatrix(data,rowCount,columnCount,0,columnCount,1);
	}

	@Override
	public int rowCount() {
		return rowCount;
	}

	@Override
	public int columnCount() {
		return columnCount;
	}
	
	public boolean isPackedArray() {
		return (offset==0)&&(columnStride==1)&&(rowStride==columnCount)&&(data.length==rowCount*columnCount);
	}

	@Override
	public double get(int row, int column) {
		if ((row<0)||(column<0)||(row>=rowCount)||(column>=columnCount)) throw new IndexOutOfBoundsException("["+row+","+column+"]");
		return data[offset+row*rowStride+column*columnStride];
	}

	@Override
	public void set(int row, int column, double value) {
		if ((row<0)||(column<0)||(row>=rowCount)||(column>=columnCount)) throw new IndexOutOfBoundsException("["+row+","+column+"]");
		data[offset+row*rowStride+column*columnStride]=value;
	}

	@Override
	public AMatrix exactClone() {
		return new StridedMatrix(data.clone(),rowCount,columnCount,offset,rowStride,columnStride);
	}

	public static StridedMatrix create(AMatrix m) {
		StridedMatrix sm=StridedMatrix.create(m.rowCount(),m.columnCount());
		sm.set(m);
		return sm;
	}
	
	public static StridedMatrix wrap(Matrix m) {
		return new StridedMatrix(m.data,m.rowCount(),m.columnCount(),0,m.columnCount(),1);
	}

	public static StridedMatrix wrap(double[] data, int rows, int columns,
			int offset, int rowStride, int columnStride) {
		return new StridedMatrix(data,rows,columns,offset,rowStride,columnStride);
	}
}
