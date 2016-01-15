package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.Vector0;

/**
 * Abstract base class for a large matrix constructed out of regular subMatrix blocks
 * 
 * @author Mike
 *
 */
public abstract class ABlockMatrix extends ARectangularMatrix {
	private static final long serialVersionUID = 5047577000801031158L;

	public abstract AMatrix getBlock(int rowBlock, int colBlock);
	
	public abstract int getBlockColumnCount(int colBlock);
	public abstract int getBlockRowCount(int rowBlock);
	
	public abstract int getBlockColumnStart(int colBlock);
	public abstract int getBlockRowStart(int rowBlock);

	public abstract int getColumnBlockIndex(int col);
	public abstract int getRowBlockIndex(int row);
	
	public abstract int columnBlockCount();
	public abstract int rowBlockCount();
	
	protected ABlockMatrix(int rows, int cols) {
		super(rows, cols);
	}
	
	@Override
	public int componentCount() {
		return columnBlockCount()*rowBlockCount();
	}
	
	@Override
	public void copyRowTo(int i, double[] dest, int destOffset) {
		getRow(i).getElements(dest, destOffset);
	}
	
	@Override
	public void copyColumnTo(int j, double[] dest, int destOffset) {
		getColumn(j).getElements(dest, destOffset);
	}
	
	@Override
	public AMatrix getComponent(int k) {
		int cbc=columnBlockCount();
		long i=k / cbc;
		long j=k % cbc;
		return getBlock((int)i, (int)j);
	}
	
	@Override
	public AVector getRowView(int row) {
		int blockIndex=getRowBlockIndex(row);
		int blockPos=getBlockRowStart(blockIndex);
		int n=columnBlockCount();
		AVector v=Vector0.INSTANCE;
		for (int i=0; i<n; i++) {
			v=v.join(getBlock(blockIndex,i).getRowView(row-blockPos));
		}
		return v;
	}
	
	@Override
	public boolean isZero() {
		int rbc=rowBlockCount();
		int cbc=columnBlockCount();
		for (int i=0; i<rbc; i++) {
			for (int j=0; j<cbc; j++) {
				if (!getBlock(i,j).isZero()) return false;
			}			
		}
		return true;
	}
	
	@Override
	public boolean isBoolean() {
		int rbc=rowBlockCount();
		int cbc=columnBlockCount();
		for (int i=0; i<rbc; i++) {
			for (int j=0; j<cbc; j++) {
				if (!getBlock(i,j).isBoolean()) return false;
			}			
		}
		return true;
	}
	
	@Override
	public AVector getColumnView(int col) {
		int blockIndex=getColumnBlockIndex(col);
		int blockPos=getBlockColumnStart(blockIndex);
		int n=rowBlockCount();
		AVector v=Vector0.INSTANCE;
		for (int i=0; i<n; i++) {
			v=v.join(getBlock(i,blockIndex).getColumnView(col-blockPos));
		}
		return v;
	}
	
	protected static int sumRowCounts(AMatrix... mats) {
		int result=0;
		for (int i=0; i<mats.length; i++) {
			result+=mats[i].rowCount();
		}
		return result;
	}
	
	protected static int sumColumnCounts(AMatrix... mats) {
		int result=0;
		for (int i=0; i<mats.length; i++) {
			result+=mats[i].columnCount();
		}
		return result;
	}
}
