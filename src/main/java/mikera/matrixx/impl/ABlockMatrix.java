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
public abstract class ABlockMatrix extends AMatrix {
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
}
