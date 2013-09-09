package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

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
	
	/**
	 * Inner class for generic banded matrix rows
	 * @author Mike
	 *
	 */
	private final class BandedMatrixRow extends AVector {
		final int row;
		final int length;
		final int lower;
		final int upper;
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
		public double dotProduct(AVector v) {
			double result=0.0;
			for (int i=Math.max(0,lower+row); i<=Math.min(length-1, row+upper);i++) {
				result+=getBand(i-row).unsafeGet(Math.min(i, row))*v.unsafeGet(i);
			}
			return result;
		}
		
		@Override 
		public double dotProduct(Vector v) {
			double result=0.0;
			for (int i=Math.max(0,lower+row); i<=Math.min(length-1, row+upper);i++) {
				result+=getBand(i-row).unsafeGet(Math.min(i, row))*v.unsafeGet(i);
			}
			return result;
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
			return ABandedMatrix.this.isFullyMutable();
		}
	}
}
