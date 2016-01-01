package mikera.vectorz.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AScalar;
import mikera.vectorz.Scalar;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Class representing a scalar view over a single element indexed from an underlying matrix
 * @author Mike
 *
 */
public class MatrixIndexScalar extends AScalar {
	private static final long serialVersionUID = -4023138233113585392L;

	final AMatrix matrix;
	final int row;
	final int col;
	
	private MatrixIndexScalar(AMatrix matrix, int row, int col) {
		// don't check bounds- should be checked by caller
		this.matrix=matrix;
		this.row=row;
		this.col=col;
	}
	
	public static MatrixIndexScalar wrap(AMatrix matrix, int row, int col) {
		MatrixIndexScalar m= new MatrixIndexScalar(matrix,row,col);
		if (!m.isValidIndex()) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(matrix, row,col));
		return m;
	}
	
	private boolean isValidIndex() {
		return !(((row<0)||(row>=matrix.rowCount())) || ((col<0)||(col>=matrix.columnCount())));

	}
	
	@Override
	public double get() {
		return matrix.unsafeGet(row,col);
	}
	
	@Override
	public void set(double value) {
		matrix.unsafeSet(row,col,value);
	}
	
	@Override
	public boolean isMutable() {
		return matrix.isMutable();
	}
	
	@Override
	public boolean isFullyMutable() {
		return matrix.isFullyMutable();
	}
	
	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public Scalar clone() {
		return new Scalar(get());
	}
	
	@Override
	public MatrixIndexScalar exactClone() {
		return new MatrixIndexScalar(matrix.exactClone(),row,col);
	}
	
	@Override
	public AScalar mutable() {
		if (matrix.isFullyMutable()) {
			return this;
		} else {
			return Scalar.create(get());
		}
	}

	@Override
	public ImmutableScalar immutable() {
		return ImmutableScalar.create(get());
	}
	
	@Override
	public void validate() {
		if (!isValidIndex()) throw new VectorzException(ErrorMessages.invalidIndex(matrix, row,col));
		super.validate();
	}
}
