package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

public abstract class ArrayMatrix extends AMatrix {
	protected final int rows;
	protected final int cols;
	public final double[] data;

	protected ArrayMatrix(double[] data, int rows, int cols ) {
		this.rows=rows;
		this.cols=cols;
		this.data=data;
	}
	
	public abstract boolean isPackedArray();
}
