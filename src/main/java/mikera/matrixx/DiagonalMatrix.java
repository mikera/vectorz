package mikera.matrixx;

import mikera.vectorz.AVector;

public final class DiagonalMatrix extends AMatrix {
	final int dimensions;
	final double[] data;
	
	public DiagonalMatrix(int dimensions) {
		this.dimensions=dimensions;
		data=new double[dimensions];
	}
	
	public DiagonalMatrix(double... values) {
		dimensions=values.length;
		data=new double[dimensions];
		System.arraycopy(values, 0, data, 0, dimensions);
	}
	
	@Override
	public int rowCount() {
		return dimensions;
	}

	@Override
	public int columnCount() {
		return dimensions;
	}

	@Override
	public double get(int row, int column) {
		if (row!=column) return 0.0;
		return data[row];
	}

	@Override
	public void set(int row, int column, double value) {
		if (row!=column) throw new UnsupportedOperationException("Diagonal matrix cannot be set at position ("+row+","+column+")!");
		data[row]=value;
	}

	@Override
	public void transformInPlace(AVector v) {
		if (v.length()!=dimensions) throw new IllegalArgumentException("Wrong length vector: "+v.length());
		for (int i=0; i<dimensions; i++) {
			v.set(i,v.get(i)*data[i]);
		}
	}

}
