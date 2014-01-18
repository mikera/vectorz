package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

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

	public abstract int getColumnBlockIndex(int col);
	public abstract int getRowBlockIndex(int row);
	
	public abstract int columnBlockCount();
	public abstract int rowBlockCount();
}
