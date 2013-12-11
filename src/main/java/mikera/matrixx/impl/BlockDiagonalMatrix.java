package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.util.IntArrays;

/**
 * Class representing a square, block diagonal matrix. 
 * 
 * Each block on the main diagonal must be a square matrix, but need not itself be diagonal.
 * 
 * @author Mike
 *
 */
public class BlockDiagonalMatrix extends ABlockMatrix {
	private final AMatrix[] mats;
	private final int[] sizes;
	private final int[] offsets;
	private final int blockCount;
	private final int size;
	
	private BlockDiagonalMatrix(AMatrix[] newMats) {
		blockCount=newMats.length;
		mats=newMats;
		sizes=new int[blockCount];
		offsets=new int[blockCount];
		int totalSize=0;
		for (int i=0; i<blockCount; i++) {
			int size=mats[i].rowCount();
			sizes[i]=size;
			offsets[i]=totalSize;
			totalSize+=size;
		}
		this.size=totalSize;
	}
	
	public static BlockDiagonalMatrix create(AMatrix... blocks) {
		return new BlockDiagonalMatrix(blocks.clone());
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		for (int i=0; i<blockCount; i++) {
			if (mats[i].isMutable()) return true;
		}
		return true;
	}

	@Override
	public AMatrix getBlock(int rowBlock, int colBlock) {
		if (rowBlock!=colBlock) return ZeroMatrix.create(getBlockRowCount(rowBlock), getBlockColumnCount(colBlock));
		return mats[rowBlock];
	}

	@Override
	public int getBlockColumnCount(int colBlock) {
		return sizes[colBlock];
	}

	@Override
	public int getBlockRowCount(int rowBlock) {
		return sizes[rowBlock];
	}

	@Override
	public int getColumnBlockIndex(int col) {
		if ((col<0)||(col>=size)) throw new IndexOutOfBoundsException("Column: "+ col);
		int i=IntArrays.indexLookup(offsets, col);
		if (i<0) throw new IndexOutOfBoundsException("Column: "+ col);
		return i;
	}

	@Override
	public int getRowBlockIndex(int row) {
		if ((row<0)||(row>=size)) throw new IndexOutOfBoundsException("Row: "+ row);
		int i=IntArrays.indexLookup(offsets, row);
		if (i<0) throw new IndexOutOfBoundsException("Row: "+ row);
		return i;
	}

	@Override
	public int rowCount() {
		return size;
	}

	@Override
	public int columnCount() {
		return size;
	}

	@Override
	public double get(int row, int column) {
		int bi=getRowBlockIndex(row);
		int bj=getColumnBlockIndex(column);
		if (bi!=bj) return 0.0;
		int i=row-offsets[bi];
		int j=column-offsets[bi];
		return mats[bi].unsafeGet(i, j);
	}

	@Override
	public void set(int row, int column, double value) {
		int bi=getRowBlockIndex(row);
		int bj=getColumnBlockIndex(column);
		if (bi!=bj) throw new UnsupportedOperationException("Block Diagonal Matrix immutable at this position");
		int i=row-offsets[bi];
		int j=column-offsets[bi];
		mats[bi].unsafeSet(i, j, value);
	}

	@Override
	public AMatrix exactClone() {
		AMatrix[] newMats=mats.clone();
		for (int i=0; i<blockCount; i++) {
			newMats[i]=newMats[i].exactClone();
		}
		return new BlockDiagonalMatrix(newMats);
	}

	@Override
	public int columnBlockCount() {
		return blockCount;
	}

	@Override
	public int rowBlockCount() {
		return blockCount;
	}
}
