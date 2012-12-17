package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * Scalar matrix class - i.e. multiplies every component by a constant factor
 * @author Mike
 */
public class ScalarMatrix extends ADiagonalMatrix {
	
	private double scale;

	public ScalarMatrix(int dimensions, double scale) {
		super(dimensions);
		this.scale=scale;
	}

	@Override
	public double get(int row, int column) {
		return (row==column)?scale:0;
	}

	public static AMatrix create(int dimensions, double scale) {
		return new ScalarMatrix(dimensions, scale);
	}


}
