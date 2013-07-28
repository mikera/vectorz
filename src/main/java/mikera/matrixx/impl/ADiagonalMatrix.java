package mikera.matrixx.impl;

import java.util.Arrays;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Tools;
import mikera.vectorz.impl.AArrayVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Abstract base class for diagonal matrices
 * @author Mike
 *
 */
public abstract class ADiagonalMatrix extends AMatrix implements ISparse {
	protected final int dimensions;
	
	public ADiagonalMatrix(int dimensions) {
		this.dimensions=dimensions;
	}

	@Override
	public boolean isSquare() {
		return true;
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
		return false;
	}
	
	@Override
	public double determinant() {
		double det=1.0;
		for (int i=0; i<dimensions; i++) {
			det*=getDiagonalValue(i);
		}
		return det;
	}
	
	/**
	 * Returns the number of dimensions of this diagonal matrix
	 * @return
	 */
	public int dimensions() {
		return dimensions;
	}
	
	@Override
	protected void copyRowTo(int row, double[] dest, int destOffset) {
		Arrays.fill(dest, destOffset,destOffset+dimensions,0.0);
		dest[destOffset+row]=getDiagonalValue(row);
	}
	
	@Override
	protected void copyColumnTo(int col, double[] dest, int destOffset) {
		// copying rows and columns is the same!
		copyRowTo(col,dest,destOffset);
	}
	
	public AMatrix innerProduct(ADiagonalMatrix a) {
		int dims=this.dimensions;
		if (dims!=a.dimensions) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,a));
		DiagonalMatrix result=DiagonalMatrix.createDimensions(dims);
		for (int i=0; i<dims; i++) {
			result.data[i]=getDiagonalValue(i)*a.getDiagonalValue(i);
		}
		return result;
	}
	
	@Override
	public AMatrix innerProduct(AMatrix a) {
		if (a instanceof ADiagonalMatrix) {
			return innerProduct((ADiagonalMatrix) a);
		} else if (a instanceof Matrix) {
			return innerProduct((Matrix) a);
		}
		if (!(dimensions==a.rowCount())) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,a));
		int acc=a.columnCount();
		Matrix m=Matrix.create(dimensions, acc);
		for (int i=0; i<dimensions; i++) {
			double dv=getDiagonalValue(i);
			for (int j=0; j<acc; j++) {
				m.unsafeSet(i, j, dv*a.unsafeGet(i,j));
			}
		}
		return m;
	}
	
	@Override
	public Matrix innerProduct(Matrix a) {
		if (!(dimensions==a.rowCount())) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,a));
		int acc=a.columnCount();
		Matrix m=Matrix.create(dimensions, acc);
		for (int i=0; i<dimensions; i++) {
			double dv=getDiagonalValue(i);
			for (int j=0; j<acc; j++) {
				m.unsafeSet(i, j, dv*a.unsafeGet(i,j));
			}
		}
		return m;
	}
	
	@Override
	public Matrix transposeInnerProduct(Matrix s) {
		return innerProduct(s);
	}
	
	@Override
	public void transformInPlace(AVector v) {
		if (v instanceof AArrayVector) {
			transformInPlace((AArrayVector) v);
			return;
		}
		if (v.length()!=dimensions) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,v));
		for (int i=0; i<dimensions; i++) {
			v.unsafeSet(i,v.unsafeGet(i)*getDiagonalValue(i));
		}
	}
	
	@Override
	public void transformInPlace(AArrayVector v) {
		double[] data=v.getArray();
		int offset=v.getArrayOffset();
		for (int i=0; i<dimensions; i++) {
			data[i+offset]*=getDiagonalValue(i);
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
		for (int i=0; i<dimensions; i++ ) {
			if (unsafeGet(i,i)!=1.0) return false;
			
		}
		return true;
	}
	
	
	@Override
	public boolean isBoolean() {
		for (int i=0; i<dimensions; i++ ) {
			if (!Tools.isBoolean(unsafeGet(i,i))) return false;
			
		}
		return true;
	}
	
	@Override
	public void transposeInPlace() {
		// already done!
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		return v.unsafeGet(i)*getDiagonalValue(i);
	}
	
	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException(ErrorMessages.notFullyMutable(this, row, column));
	}

	public double getDiagonalValue(int i) {
		if ((i<0)||(i>=dimensions)) throw new IndexOutOfBoundsException();
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
	public double density() {
		return 1.0/dimensions;
	}
	
	@Override
	public Matrix toMatrix() {
		Matrix m=Matrix.create(dimensions, dimensions);
		for (int i=0; i<dimensions; i++) {
			m.data[i*(dimensions+1)]=getDiagonalValue(i);
		}
		return m;
	}
	
	@Override
	public void validate() {
		if (dimensions!=getLeadingDiagonal().length()) throw new VectorzException("dimension mismatch: "+dimensions);
		
		super.validate();
	}
}
