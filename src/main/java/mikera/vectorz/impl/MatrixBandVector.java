package mikera.vectorz.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Vector class representing a matrix band
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public final class MatrixBandVector extends AMatrixViewVector {
	private int band;
	
	private MatrixBandVector(AMatrix source, int band) {
		super(source,source.bandLength(band));
		this.band=band;
	}

	public static AVector create(AMatrix source, int band) {
		if ((band>=source.columnCount())||(band<=-source.rowCount())) return null;
		return new MatrixBandVector(source,band);
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
	public AVector exactClone() {
		return new MatrixBandVector(source.exactClone(),band);
	}

}
