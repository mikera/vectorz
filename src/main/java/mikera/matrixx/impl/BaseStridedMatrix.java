package mikera.matrixx.impl;

public abstract class BaseStridedMatrix extends AStridedMatrix {
	private static final long serialVersionUID = 4101975245958427299L;
	
	protected final int rowStride;
	protected final int colStride;
	protected final int offset;

	protected BaseStridedMatrix(double[] data, int rowCount, int columnCount,
			int offset, int rowStride, int columnStride) {
		super(data,rowCount,columnCount);
		this.offset = offset;
		this.rowStride = rowStride;
		this.colStride = columnStride;
	}
	
	@Override
	public final int rowStride() {
		return rowStride;
	}
	
	@Override
	public final int columnStride() {
		return colStride;
	}
	
	@Override
	public final int getArrayOffset() {
		return offset;
	}
	
	@Override
	public final double get(int i, int j) {
		checkIndex(i,j);
		return data[index(i,j)];
	}
	
	@Override
	public final double unsafeGet(int i, int j) {
		return data[index(i,j)];
	}
	
	@Override
	protected final int index(int row, int col) {
		return offset+(row*rowStride)+(col*colStride);
	}
	

}
