package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.util.VectorzException;

/**
 * Sparse banded matrix implementation.
 * @author Mike
 *
 */
public class BandedMatrix extends ABandedMatrix {
	private final int minBand;
	private final int maxBand;
	
	private final AVector[] bands;
	private final int rowCount;
	private final int columnCount;

	private BandedMatrix(int rc, int cc, int minBand, AVector[] bands) {
		this.rowCount=rc;
		this.columnCount=cc;
		this.bands=bands;
		this.minBand=minBand;
		this.maxBand=minBand+bands.length-1;
	}
	
	public static BandedMatrix create(int rowCount, int columnCount, int minBand, int maxBand) {
		AVector[] bands=new AVector[maxBand-minBand+1];
		for (int i=minBand; i<=maxBand; i++) {
			bands[i]=Vector.createLength(bandLength(rowCount,columnCount,i));
		}
		return new BandedMatrix(rowCount,columnCount,minBand,bands);
	}
	
	@Override
	public int upperBandwidthLimit() {
		return maxBand;
	}

	@Override
	public int lowerBandwidthLimit() {
		return minBand;
	}

	@Override
	public AVector getBand(int band) {
		if ((band>=minBand)&&(band<=maxBand)) return bands[band-minBand];
		return null;
	}

	@Override
	public int rowCount() {
		return rowCount;
	}

	@Override
	public int columnCount() {
		return columnCount;
	}

	@Override
	public double get(int row, int column) {
		return getBand(bandIndex(row,column)).get(bandPosition(row,column));
	}

	@Override
	public void set(int row, int column, double value) {
		getBand(bandIndex(row,column)).set(bandPosition(row,column),value);
	}

	@Override
	public BandedMatrix exactClone() {
		BandedMatrix b=new BandedMatrix(rowCount,columnCount,minBand,bands.clone());
		for (int i=minBand; i<=maxBand; i++) {
			b.bands[i-minBand]=b.bands[i-minBand].exactClone();
		}
		return b;
	}
	
	@Override public void validate() {
		super.validate();
		for (int i=minBand; i<=maxBand; i++) {
			if (bandLength(i)!=getBand(i).length()) throw new VectorzException("Invalid band length: "+i);
		}
	}
	
}
