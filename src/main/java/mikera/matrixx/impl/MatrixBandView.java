package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.impl.AMatrixViewVector;
import mikera.vectorz.impl.Vector0;
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
		int rc=source.rowCount();
		int cc=source.columnCount();
		if ((band>cc)||(band<-rc)) throw new IllegalArgumentException(ErrorMessages.invalidBand(source,band));
		if ((band==cc)||(band==-rc)) return Vector0.INSTANCE;
		return new MatrixBandView(source,band);
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		int r=source.bandStartRow(band);
		int c=source.bandStartColumn(band);
		for (int i=0; i<length; i++) {
			data[offset+i]+=source.unsafeGet(r+i, c+i);
		}
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		int r=source.bandStartRow(band);
		int c=source.bandStartColumn(band);
		for (int i=0; i<length; i++) {
			data[offset+i]=source.unsafeGet(r+i, c+i);
		}
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
	public double unsafeGet(int i) {
		return source.unsafeGet(calcRow(i), calcCol(i));
	}
	
	@Override
	public void unsafeSet(int i, double v) {
		source.unsafeSet(calcRow(i), calcCol(i),v);
	}
	
	@Override
	public AVector exactClone() {
		return new MatrixBandView(source.exactClone(),band);
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i]*unsafeGet(i);
		}
		return result;
	}

	@Override
	public void applyOp(Op op) {
		for (int i=0; i<length; i++) {
			double v=unsafeGet(i);
			unsafeSet(i,op.apply(v));
		}
	}
}
