package mikera.matrixx.impl;

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


}
