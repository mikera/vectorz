package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

public abstract class ArrayMatrix extends AMatrix {
	protected final int rows;
	protected final int columns;
	public final double[] data;

	protected ArrayMatrix(double[] data, int rows, int cols ) {
		this.rows=rows;
		this.columns=cols;
		this.data=data;
	}
}
