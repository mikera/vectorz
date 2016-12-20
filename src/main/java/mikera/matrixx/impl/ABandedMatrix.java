package mikera.matrixx.impl;

import java.util.Arrays;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.ASizedVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.VectorzException;

/** 
 * Abstract base class for banded matrices
 * 
 * Banded matrix implementations are assumed to store their data efficiently in diagonal bands,
 * so functions on banded matrices can be designed to exploit this fact.
 * 
 * May be either square or rectangular
 * 
 * @author Mike
 *
 */
public abstract class ABandedMatrix extends AMatrix implements IFastBands {
	private static final long serialVersionUID = -229314208418131186L;

	@Override
	public abstract int upperBandwidthLimit();
	
	@Override
	public abstract int lowerBandwidthLimit();
	
	@Override
	public abstract AVector getBand(int band);
	
	@Override
	public int upperBandwidth() {
		for (int i=upperBandwidthLimit(); i>0; i--) {
			if (!(getBand(i).isZero())) return i;
		}
		return 0;
	}
	
	@Override
	public int lowerBandwidth() {
		for (int i=-lowerBandwidthLimit(); i<0; i++) {
			if (!(getBand(i).isZero())) return -i;
		}
		return 0;
	}
	
	@Override
	public boolean isMutable() {
		int lb=lowerBandwidthLimit(), ub=upperBandwidthLimit();
		for (int i=-lb; i<=ub; i++) {
			if (getBand(i).isMutable()) return true;
		}
		return false;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isSymmetric() {
		if (rowCount()!=columnCount()) return false;
		int bs=Math.max(upperBandwidthLimit(), lowerBandwidthLimit());
		for (int i=1; i<=bs; i++) {
			if (!getBand(i).equals(getBand(-i))) return false;
		}
		return true;
	}
	
	@Override
	public boolean isUpperTriangular() {
		return (lowerBandwidthLimit()==0)||(lowerBandwidth()==0);
	}
	
	@Override
	public boolean isLowerTriangular() {
		return (upperBandwidthLimit()==0)||(upperBandwidth()==0);
	}
	
	@Override
	public AVector getRowView(int row) {
		checkRow(row);
		return new BandedMatrixRow(row);
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		int cc=columnCount();
		Arrays.fill(dest, destOffset,destOffset+cc,0.0);
		int start=Math.max(-row, -lowerBandwidthLimit());
		int end=Math.min(cc-1-row, upperBandwidthLimit());
		for (int b=start; b<=end; b++) {
			dest[destOffset+row+b]=unsafeGet(row,row+b);
		}
	}
	
	@Override 
	public long nonZeroCount() {
		long t=0;
		for (int i=-lowerBandwidthLimit(); i<=upperBandwidthLimit(); i++) {
			t+=getBand(i).nonZeroCount();
		}
		return t;
	}
	
	@Override 
	public boolean isZero() {
		for (int i=-lowerBandwidthLimit(); i<=upperBandwidthLimit(); i++) {
			if (!getBand(i).isZero()) return false;
		}
		return true;
	}
	
	@Override
	public boolean isSparse() {
		return (lowerBandwidthLimit()<(rowCount()-1))||(upperBandwidthLimit()<(columnCount()-1));
	}
	
	@Override 
	public double elementSum() {
		double t=0;
		for (int i=-lowerBandwidthLimit(); i<=upperBandwidthLimit(); i++) {
			t+=getBand(i).elementSum();
		}
		return t;
	}
	
	@Override
	public double trace() {
		return getBand(0).elementSum();
	}
	
	@Override
	public double diagonalProduct() {
		return getBand(0).elementProduct();
	}
	
	@Override 
	public double elementSquaredSum() {
		double t=0;
		for (int i=-lowerBandwidthLimit(); i<=upperBandwidthLimit(); i++) {
			t+=getBand(i).elementSquaredSum();
		}
		return t;
	}
	
	@Override 
	public void fill(double value) {
		for (int i=-rowCount()+1; i<columnCount(); i++) {
			getBand(i).fill(value);
		}
	}
	
	@Override
	public void setSparse(double value) {
		int minBand=-lowerBandwidthLimit();
		int maxBand=upperBandwidthLimit();
		for (int i=minBand; i<=maxBand; i++) {
			getBand(i).setSparse(value);
		}
	}
	
	@Override 
	public void multiply(double value) {
		for (int i=-rowCount()+1; i<columnCount(); i++) {
			getBand(i).multiply(value);
		}
	}
	
	@Override
	public Matrix toMatrix() {
		int rc = rowCount();
		int cc = columnCount();
		Matrix m = Matrix.create(rc, cc);
		for (int i=-lowerBandwidthLimit(); i<=upperBandwidthLimit(); i++) {
			m.getBand(i).set(this.getBand(i));
		}
		return m;
	}
	
	@Override
	public Matrix toMatrixTranspose() {
		int rc = rowCount();
		int cc = columnCount();
		Matrix m = Matrix.create(cc, rc);
		for (int i=-lowerBandwidthLimit(); i<=upperBandwidthLimit(); i++) {
			m.getBand(-i).set(this.getBand(i));
		}
		return m;
	}
	
	/**
	 * Inner class for generic banded matrix rows
	 * @author Mike
	 *
	 */
	@SuppressWarnings("serial")
	private final class BandedMatrixRow extends ASizedVector {
		final int row;
		final int lower;
		final int upper;
		
		public BandedMatrixRow(int row) {
			super(columnCount());
			this.row=row;
			this.lower=-lowerBandwidthLimit();
			this.upper=upperBandwidthLimit();
		}

		@Override
		public double get(int i) {
			checkIndex(i);
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
		public void set(int i, double value) {
			checkIndex(i);
			unsafeSet(i,value);
		}
		
		@Override
		public void unsafeSet(int i, double value) {
			int b=i-row;
			getBand(b).unsafeSet(Math.min(i, row),value);
		}

		@Override
		public AVector exactClone() {
			return ABandedMatrix.this.exactClone().getRowView(row);
		}
	
		@Override
		public boolean isFullyMutable() {
			return ABandedMatrix.this.isFullyMutable();
		}
		
		@Override
		public boolean isMutable() {
			return ABandedMatrix.this.isMutable();
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
		public void getElements(double[] dest, int offset) {
			copyRowTo(row,dest,offset);
		}
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		int b1=-lowerBandwidth();
		int b2=upperBandwidth();
		int cc=columnCount();
		for (int b=b1; b<=b2; b++) {
			AVector band=getBand(b);
			int di = offset+this.bandStartColumn(b)+cc*bandStartRow(b);
			band.addToArray(data, di, cc+1);
		}
	}
	
	@Override
	public double[] toDoubleArray() {
		double[] result=DoubleArrays.createStorage(rowCount(),columnCount());
		// since this array is sparse, fastest to use addToArray to modify only non-zero elements
		addToArray(result,0);
		return result;
	}
	
	@Override public void validate() {
		super.validate();
		if (lowerBandwidthLimit()<0) throw new VectorzException("Negative lower bandwidth limit?!?");
		int minBand=-lowerBandwidthLimit();
		int maxBand=upperBandwidthLimit();
		if (minBand<=-rowCount()) throw new VectorzException("Invalid lower limit: "+minBand);
		if (maxBand>=columnCount()) throw new VectorzException("Invalid upper limit: "+maxBand);
		for (int i=minBand; i<=maxBand; i++) {
			AVector v=getBand(i);
			if (bandLength(i)!=v.length()) throw new VectorzException("Invalid band length: "+i);
		}
	}

	@Override
	public double density() {
		return nonZeroCount()/((double)elementCount());
	}
}
