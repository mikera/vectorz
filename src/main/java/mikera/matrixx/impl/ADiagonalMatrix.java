package mikera.matrixx.impl;

import java.util.Arrays;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.impl.ASingleElementVector;
import mikera.vectorz.impl.SingleElementVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Abstract base class for square diagonal matrices
 * @author Mike
 *
 */
public abstract class ADiagonalMatrix extends ASingleBandMatrix {
	private static final long serialVersionUID = -6770867175103162837L;

	protected final int dimensions;
	
	protected ADiagonalMatrix(int dimensions) {
		this.dimensions=dimensions;
	}
	
	@Override
	public int nonZeroBand() {
		return 0;
	}

	@Override
	public boolean isSquare() {
		return true;
	}
	
	@Override
	public boolean isZero() {
		return getLeadingDiagonal().isZero();
	}
	
	@Override
	public boolean isBoolean() {
		return getLeadingDiagonal().isBoolean();
	}
	
	@Override
	public boolean isSymmetric() {
		return true;
	}
	
	@Override
	public boolean isDiagonal() {
		return true;
	}
	
	@Override
	public boolean isRectangularDiagonal() {
		return true;
	}
	
	@Override
	public boolean isUpperTriangular() {
		return true;
	}
	
	@Override
	public boolean isLowerTriangular() {
		return true;
	}
	
	@Override
	public abstract boolean isMutable();
	
	@Override
	public boolean isFullyMutable() {
		return (dimensions<=1)&&(getLeadingDiagonal().isFullyMutable());
	}
	
	@Override
	protected void checkSameShape(AMatrix m) {
		int dims=dimensions;
		if((dims!=m.rowCount())||(dims!=m.columnCount())) {
			throw new IndexOutOfBoundsException(ErrorMessages.mismatch(this, m));
		}
	}
	
	@Override
	protected void checkSameShape(ARectangularMatrix m) {
		int dims=dimensions;
		if((dims!=m.rowCount())||(dims!=m.columnCount())) {
			throw new IndexOutOfBoundsException(ErrorMessages.mismatch(this, m));
		}
	}
	
	@Override
	protected final void checkIndex(int i, int j) {
		if ((i<0)||(i>=dimensions)||(j<0)||(j>=dimensions)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i,j));
		}
	}
	
	public final boolean isSameShape(ARectangularMatrix m) {
		return (dimensions==m.rows)&&(dimensions==m.cols);
	}
	
	@Override
	public final int upperBandwidthLimit() {
		return 0;
	}
	
	@Override
	public final int lowerBandwidthLimit() {
		return 0; 
	}
	
	@Override
	public AVector getBand(int band) {
		if (band==0) {
			return getLeadingDiagonal();
		} else {
			if ((band>dimensions)||(band<-dimensions)) throw new IndexOutOfBoundsException(ErrorMessages.invalidBand(this, band));
			return Vectorz.createZeroVector(bandLength(band));
		}
	}
	
	@Override
	public AVector getNonZeroBand() {
		return getLeadingDiagonal();
	}
	
	@Override
	public double determinant() {
		double det=1.0;
		for (int i=0; i<dimensions; i++) {
			det*=unsafeGetDiagonalValue(i);
		}
		return det;
	}
	
	@Override
	public int rank() {
		return (int)getLeadingDiagonal().nonZeroCount();
	}
	
	/**
	 * Returns the number of dimensions of this diagonal matrix
	 * @return
	 */
	public int dimensions() {
		return dimensions;
	}
	
	@Override
	public boolean isSameShape(AMatrix m) {
		return (dimensions==m.rowCount())&&(dimensions==m.columnCount());
	}
	
	@Override
	public int checkSquare() {
		return dimensions;
	}
	
	@Override
	public double elementMax(){
		double ldv=getLeadingDiagonal().elementMax();
		if (dimensions>1) return Math.max(0, ldv); else return ldv;
	}
	
	@Override
	public double elementMin(){
		double ldv=getLeadingDiagonal().elementMin();
		if (dimensions>1) return Math.min(0, ldv); else return ldv;
	}
	
	@Override
	public double elementSum(){
		return getLeadingDiagonal().elementSum();
	}
	
	@Override
	public double elementSquaredSum(){
		return getLeadingDiagonal().elementSquaredSum();
	}
	
	@Override
	public long nonZeroCount(){
		return getLeadingDiagonal().nonZeroCount();
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		Arrays.fill(dest, destOffset,destOffset+dimensions,0.0);
		dest[destOffset+row]=unsafeGetDiagonalValue(row);
	}
	
	@Override
	public void addToArray(double[] dest, int offset) {
		getLeadingDiagonal().addToArray(dest, offset, dimensions+1);
	}
	
	@Override
	public AMatrix addCopy(AMatrix a) {
		if (a.isDiagonal()) {
			if (a.rowCount()!=dimensions) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,a));
			DiagonalMatrix m=DiagonalMatrix.create(this.getLeadingDiagonal());
			a.getLeadingDiagonal().addToArray(m.data,0);
			return m;
		} else {
			return a.addCopy(this);
		}
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		// copying rows and columns is the same!
		copyRowTo(col,dest,destOffset);
	}
	
	/**
	 * Computes the inner product of this matrix with another diagonal matrix of the same shape.
	 * 
	 * The result will be a diagnonal matrix of the same shape.
	 * @param a
	 * @return
	 */
	public AMatrix innerProduct(ADiagonalMatrix a) {
		AMatrix result=clone();
		result.getLeadingDiagonal().multiply(a.getLeadingDiagonal());
		return result;
	}
	
	@Override
	public AMatrix innerProduct(AMatrix a) {
		if (a instanceof ADiagonalMatrix) {
			return innerProduct((ADiagonalMatrix) a);
		} 
		if (!(dimensions==a.rowCount())) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,a));
		AMatrix m=a.clone();
		for (int i=0; i<dimensions; i++) {
			double dv=unsafeGetDiagonalValue(i);
			m.multiplyRow(i, dv);
		}
		return m;
	}
	
	@Override
	public void addOuterProductSparse(AVector a, AVector b) {
		getLeadingDiagonal().addProduct(a, b);
	}
	
	@Override
	public AMatrix transposeInnerProduct(AMatrix s) {
		return innerProduct(s);
	}
	
	@Override
	public void transformInPlace(AVector v) {
		if (v instanceof ADenseArrayVector) {
			transformInPlace((ADenseArrayVector) v);
			return;
		}
		if (v.length()!=dimensions) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,v));
		for (int i=0; i<dimensions; i++) {
			v.unsafeSet(i,v.unsafeGet(i)*unsafeGetDiagonalValue(i));
		}
	}
	
	@Override
	public void transformInPlace(ADenseArrayVector v) {
		double[] data=v.getArray();
		int offset=v.getArrayOffset();
		for (int i=0; i<dimensions; i++) {
			data[i+offset]*=unsafeGetDiagonalValue(i);
		}
	}
	
	@Override
	public void transform(Vector source, Vector dest) {
		int rc = rowCount();
		int cc = rc;
		if (source.length()!=cc) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		if (dest.length()!=rc) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		double[] sdata=source.getArray();
		double[] ddata=dest.getArray();
		for (int row = 0; row < rc; row++) {
			ddata[row]=sdata[row]*unsafeGetDiagonalValue(row);
		}
	}
	
	@Override
	public int rowCount() {
		return dimensions;
	}

	@Override
	public int columnCount() {
		return dimensions;
	}
	
	@Override 
	public boolean isIdentity() {
		return getLeadingDiagonal().elementsEqual(1.0);
	}
	
	@Override
	public void transposeInPlace() {
		// already done!
	}
	
	@Override
	public double rowDotProduct(int i, AVector v) {
		return v.unsafeGet(i)*unsafeGetDiagonalValue(i);
	}
	
	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException(ErrorMessages.notFullyMutable(this, row, column));
	}
	
	@Override
	public void setSparse(double value) {
		getLeadingDiagonal().setSparse(value);
	}
	
	@Override 
	public abstract AVector getLeadingDiagonal();

	public double getDiagonalValue(int i) {
		if ((i<0)||(i>=dimensions)) throw new IndexOutOfBoundsException();
		return unsafeGet(i,i);
	}
	
	@Override
	public ASingleElementVector getRow(int row) {
		return SingleElementVector.create(getDiagonalValue(row), row, dimensions);
	}
	
	@Override
	public ASingleElementVector getColumn(int col) {
		return getRow(col);
	}
	
	public double unsafeGetDiagonalValue(int i) {
		return unsafeGet(i,i);
	}
	
	@Override
	public ADiagonalMatrix getTranspose() {
		return this;
	}
	
	@Override
	public ADiagonalMatrix getTransposeView() {
		return this;
	}
	
	@Override
	public Matrix toMatrix() {
		Matrix m=Matrix.create(dimensions, dimensions);
		for (int i=0; i<dimensions; i++) {
			m.data[i*(dimensions+1)]=unsafeGetDiagonalValue(i);
		}
		return m;
	}
	
	@Override
	public double trace() {
		return getLeadingDiagonal().elementSum();
	}
	
	@Override
	public double diagonalProduct() {
		return getLeadingDiagonal().elementProduct();
	}
	
	@Override
	public double[] toDoubleArray() {
		double[] data=new double[dimensions*dimensions];
		getLeadingDiagonal().addToArray(data, 0, dimensions+1);
		return data;
	}
	
	@Override
	public final Matrix toMatrixTranspose() {
		return toMatrix();
	}
	
	@Override
	public boolean equalsTranspose(AMatrix m) {
		return equals(m);
	}
	
	@Override
	public void validate() {
		if (dimensions!=getLeadingDiagonal().length()) throw new VectorzException("dimension mismatch: "+dimensions);
		
		super.validate();
	}
	
	@Override 
	public abstract ADiagonalMatrix exactClone();

	@Override
	public AMatrix clone() {
		if (dimensions<Vectorz.MIN_SPARSE_LENGTH) return super.clone();
		return sparseClone();
	}
	
	@Override
	public boolean hasUncountable() {
		return getLeadingDiagonal().hasUncountable();
	}
	
    /**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return getLeadingDiagonal().elementPowSum(p);
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return getLeadingDiagonal().elementAbsPowSum(p);
    }
}
