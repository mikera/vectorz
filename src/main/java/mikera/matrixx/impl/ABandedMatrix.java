package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/** 
 * Abstract base class for banded matrices
 * 
 * May be either square or rectangular
 * 
 * @author Mike
 *
 */
public abstract class ABandedMatrix extends AMatrix {
	
	@Override
	public abstract int upperBandwidthLimit();
	
	@Override
	public abstract int lowerBandwidthLimit();
	
	@Override
	public abstract AVector getBand(int band);

	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public AVector getRow(int row) {
		return new BandedMatrixRow(row);
	}
	
	private final class BandedMatrixRow extends AVector {
		int row;
		int length;
		int lower;
		int upper;
		public BandedMatrixRow(int row) {
			this.row=row;
			this.length=columnCount();
			this.lower=lowerBandwidthLimit();
			this.upper=upperBandwidthLimit();
		}

		@Override
		public int length() {
			return length;
		}

		@Override
		public double get(int i) {
			if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
			return unsafeGet(i);
		}
		
		@Override
		public double unsafeGet(int i) {
			int b=i-row;
			if ((b<lower)||(b>upper)) return 0;
			return getBand(b).unsafeGet(Math.min(i, row));
		}

		@Override
		public void set(int i, double value) {
			if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
			unsafeSet(i,value);
		}
		
		@Override
		public void unsafeSet(int i, double value) {
			int b=i-row;
			getBand(b).unsafeSet(Math.min(i, row),value);
		}

		@Override
		public AVector exactClone() {
			return ABandedMatrix.this.exactClone().getRow(row);
		}
	
		@Override
		public boolean isFullyMutable() {
			return (row<=-upper)&&((row+upper)>=length);
		}
	}
}
