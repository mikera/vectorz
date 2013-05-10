package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.RepeatedElementVector;

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
	public boolean isMutable() {
		return true;
	}

	@Override
	public double get(int row, int column) {
		return (row==column)?scale:0;
	}
	
	@Override
	public AVector getLeadingDiagonal() {
		return RepeatedElementVector.create(dimensions, scale);
	}

	public static AMatrix create(int dimensions, double scale) {
		return new ScalarMatrix(dimensions, scale);
	}
	
	@Override
	public void scale(double factor) {
		scale*=factor;
	}
	
	@Override
	public double trace() {
		return scale*dimensions;
	}
	
	@Override
	public void transformInPlace(AVector v) {
		for (int i=0; i<dimensions; i++) {
			v.scale(scale);
		}
	}

	@Override
	public ScalarMatrix exactClone() {
		return new ScalarMatrix(dimensions,scale);
	}
}
