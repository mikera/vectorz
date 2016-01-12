package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Immutable scalar matrix class - a diagonal matrix with an identical value along the leading diagonal.
 * 
 * When used to transform a vector, multiplies every component by a constant factor
 * 
 * @author Mike
 */
public class ScalarMatrix extends ADiagonalMatrix {
	private static final long serialVersionUID = 3777724453035425881L;

	private final double scale;

	public ScalarMatrix(int dimensions, double scale) {
		super(dimensions);
		this.scale=scale;
		if (dimensions<1) throw new IllegalArgumentException("ScalarMatrix must have one or more dimensions");
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
	public boolean isFullyMutable() {
		return false;
	}

	
	@Override
	public AMatrix innerProduct(AMatrix m) {
		if (dimensions!=m.rowCount()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, m));
		return m.innerProduct(scale);
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
	public RepeatedElementVector getLeadingDiagonal() {
		// This is OK since we must have at least one dimension
		return RepeatedElementVector.create(dimensions, scale);
	}

	public static ScalarMatrix create(int dimensions, double scale) {
		return new ScalarMatrix(dimensions, scale);
	}
	
	@Override
	public double trace() {
		return scale*dimensions;
	}
	
	@Override
	public double diagonalProduct() {
		return Math.pow(scale,dimensions);
	}
	
	@Override
	public AMatrix multiplyCopy(double d) {
		return ScalarMatrix.create(dimensions, d*scale);
	}
	
	@Override
	public void transformInPlace(AVector v) {
		v.multiply(scale);
	}
	
	@Override
	public void transformInPlace(ADenseArrayVector v) {
		v.multiply(scale);
	}

	@Override
	public ScalarMatrix exactClone() {
		return new ScalarMatrix(dimensions,scale);
	}
}
