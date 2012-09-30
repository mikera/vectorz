package mikera.matrixx;

import mikera.transformz.AAffineTransform;
import mikera.transformz.ATranslation;
import mikera.transformz.AffineMN;
import mikera.transformz.Transformz;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public abstract class AMatrix extends AAffineTransform {
	// ==============================================
	// Abstract interface
	
	public abstract int rowCount();
	
	public abstract int columnCount();
	
	public abstract double get(int row, int column);

	public abstract void set(int row, int column, double value);
	
	// =============================================
	// Standard implementations
	
	public AAffineTransform toAffineTransform() {
		return new AffineMN(this.clone(),Transformz.identityTransform(outputDimensions()));
	}
	
	@Override
	public AMatrix getMatrixComponent() {
		return this;
	}
	
	@Override
	public ATranslation getTranslationComponent() {
		return Transformz.identityTransform(rowCount());
	}
	
	public boolean isSquare() {
		return rowCount()==columnCount();
	}
	
	@Override
	public int inputDimensions() {
		return columnCount();
	}
	
	@Override
	public int outputDimensions() {
		return rowCount();
	}

	
	@Override
	public void transform(AVector source, AVector dest) {
		int rc=rowCount();
		int cc=columnCount();
		for (int row=0; row<rc; row++) {
			double total=0.0;
			for (int column=0; column<cc; column++) {
				total+=get(row,column)*source.get(column);
			}
			dest.set(row,total);
		}
	}
	
	
	
	@Override
	public void transformInPlace(AVector v) {
		double[] temp=new double[v.length()];
		int rc=rowCount();
		int cc=columnCount();
		if (rc!=cc) throw new UnsupportedOperationException("Cannot transform in place with a non-square transformation");
		for (int row=0; row<rc; row++) {
			double total=0.0;
			for (int column=0; column<cc; column++) {
				total+=get(row,column)*v.get(column);
			}
			temp[row]=total;
		}
		v.setValues(temp);
	}
	
	@SuppressWarnings("serial")
	private class MatrixRow extends AVector {
		private final int row;
		private MatrixRow(int row) {
			this.row=row;
		}
		@Override
		public int length() {
			return columnCount();
		}
		@Override
		public double get(int i) {
			return AMatrix.this.get(row,i);
		}
		@Override
		public void set(int i, double value) {
			AMatrix.this.set(row,i,value);
		}
	}
	
	@SuppressWarnings("serial")
	private class MatrixColumn extends AVector {
		private final int column;
		private MatrixColumn(int column) {
			this.column=column;
		}
		@Override
		public int length() {
			return columnCount();
		}
		@Override
		public double get(int i) {
			return AMatrix.this.get(i, column);
		}
		@Override
		public void set(int i, double value) {
			AMatrix.this.set(i, column,value);
		}
	}
	
	public AVector getRow(int row) {
		return new MatrixRow(row);
	}
	
	public AVector getColumn(int column) {
		return new MatrixColumn(column);
	}
	
	public AVector cloneRow(int row) {
		int cc=columnCount();
		AVector v=Vectorz.createLength(cc);
		for (int i=0; i<cc; i++) {
			v.set(i,get(row,i));
		}
		return v;
	}
	
	public void set(AMatrix a) {
		int rc=rowCount();
		if (a.rowCount()!=rc) throw new IllegalArgumentException("Source matrix has wrog number of rows: "+a.rowCount());
		int cc=columnCount();
		if (a.columnCount()!=cc) throw new IllegalArgumentException("Source matrix has wrong number of columns: "+a.columnCount());
		for (int row=0; row<rc; row++) {
			for (int column=0; column<cc; column++) {
				set(row,column,a.get(row,column));
			}
		}
	}
	
	public boolean isFullyMutable() {
		return true;
	}
	
	public AMatrix clone() {
		return (AMatrix) super.clone();
	}

	public double determinant() {
		if (!isSquare()) throw new UnsupportedOperationException("Cannot take determinant of non-squae matrix!");
		
		int rc=rowCount();
		int[] inds=new int[rc];
		for (int i=0; i<rc; i++) {
			inds[i]=i;
		}
		return calcDeterminant(inds,0);
	}
	
	private static void swap(int[] inds, int a, int b) {
		int temp=inds[a];
		inds[a]=inds[b];
		inds[b]=temp;	
	}

	private double calcDeterminant(int[] inds, int offset) {
		int rc=rowCount();
		if (offset==(rc-1)) return get(offset,inds[offset]);
		
		double det=get(offset,inds[offset])*calcDeterminant(inds,offset+1);
		for (int i=1; i<(rc-offset); i++) {
			swap(inds,offset,offset+i);
			det-=get(offset,inds[offset])*calcDeterminant(inds,offset+1);
			swap(inds,offset,offset+i);
		}
		return det;
	}
}
