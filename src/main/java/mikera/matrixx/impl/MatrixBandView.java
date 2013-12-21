package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.AMatrixViewVector;
import mikera.vectorz.impl.MatrixIndexScalar;
import mikera.vectorz.util.ErrorMessages;

/**
 * Vector class representing a view of a matrix band
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public final class MatrixBandView extends AMatrixViewVector {
	private int band;
	
	private MatrixBandView(AMatrix source, int band) {
		super(source,source.bandLength(band));
		this.band=band;
	}

	public static AVector create(AMatrix source, int band) {
		if ((band>=source.columnCount())||(band<=-source.rowCount())) return null;
		return new MatrixBandView(source,band);
	}
	
	@Override
	protected int calcRow(int i) {
		return (band<0)?i-band:i;
	}
	@Override
	protected int calcCol(int i) {
		return (band>0)?i+band:i;
	}
	
	@Override
	public MatrixIndexScalar slice(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return MatrixIndexScalar.wrap(source, calcRow(i), calcCol(i));
	}

	@Override
	public AVector exactClone() {
		return new MatrixBandView(source.exactClone(),band);
	}

}
