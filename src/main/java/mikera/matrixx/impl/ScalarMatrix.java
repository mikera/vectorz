package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.AArrayVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.util.ErrorMessages;

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
	public long nonZeroCount() {
		return (scale==0.0)?0:dimensions;
	}	

	@Override
	public double elementSum() {
		return scale*dimensions;
	}	
	
	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public double get(int row, int column) {
		if ((row<0)||(row>=dimensions)||(column<0)||(column>=dimensions)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		}
		return (row==column)?scale:0;
	}
	
	@Override
	public double unsafeGet(int row, int column) {
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
	public double trace() {
		return scale*dimensions;
	}
	
	@Override
	public void transformInPlace(AVector v) {
		v.multiply(scale);
	}
	
	@Override
	public void transformInPlace(AArrayVector v) {
		v.multiply(scale);
	}

	@Override
	public ScalarMatrix exactClone() {
		return new ScalarMatrix(dimensions,scale);
	}
}
