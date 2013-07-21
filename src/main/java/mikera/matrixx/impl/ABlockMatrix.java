package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

public abstract class ABlockMatrix extends AMatrix {
	
	public abstract AMatrix getBlock(int rowBlock, int colBlock);
	
	public abstract int getBlockColumnCount(int colBlock);
	public abstract int getBlockRowCount(int rowBlock);

	public abstract int getColumnBlockIndex(int col);
	public abstract int getRowBlock(int row);

}
