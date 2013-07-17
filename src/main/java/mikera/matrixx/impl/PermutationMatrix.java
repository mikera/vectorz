package mikera.matrixx.impl;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;

public final class PermutationMatrix extends AMatrix {
	private final Index perm;
	private final int size;
	
	private PermutationMatrix(Index perm) {
		this.perm=perm;
		size=perm.length();
	}
	
	public static PermutationMatrix create(Index rowPermutations) {
		return new PermutationMatrix(rowPermutations.clone());
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isSquare() {
		return true;
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
		return (perm.get(row)==column)?1.0:0.0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException("Can't arbitrarily mutate a permutation matrix");
	}

	@Override
	public PermutationMatrix exactClone() {
		return new PermutationMatrix(perm.clone());
	}

	public static AMatrix create(int... rowPermutations) {
		return create(Index.of(rowPermutations));
	}
}
