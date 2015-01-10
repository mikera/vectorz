package mikera.matrixx.impl;

import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Matrix class that wraps a vector as a single-column matrix
 * 
 * @author Mike
 */
public class ColumnMatrix extends ARectangularMatrix implements IFastColumns, IFastRows {
	private static final long serialVersionUID = -6040718921619985258L;

	private final AVector vector;
	
	public ColumnMatrix(AVector v) {
		super(v.length(),1);
		vector=v;
	}
	
	public static ColumnMatrix wrap(AVector v) {
		return new ColumnMatrix(v);
	}
	
	@Override
	public boolean isFullyMutable() {
		return vector.isFullyMutable();
	}
	
	@Override
	public boolean isMutable() {
		return vector.isMutable();
	}
	
	@Override
	public boolean isZero() {
		return vector.isZero();
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		if (col!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 1, col));
		vector.getElements(dest, destOffset);
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		dest[destOffset]=vector.get(row);
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		vector.getElements(data, offset);
	}
	
	@Override
	public void applyOp(Op op) {
		vector.applyOp(op);
	}
	
	@Override
	public void multiply(double factor) {
		vector.scale(factor);
	}
	
	@Override 
	public double elementSum() {
		return vector.elementSum();
	}
	
	@Override 
	public double elementMin() {
		return vector.elementMin();
	}
	
	@Override 
	public double elementMax() {
		return vector.elementMax();
	}
	
	@Override 
	public long nonZeroCount() {
		return vector.nonZeroCount();
	}

	@Override
	public double get(int row, int column) {
		if(column!=0) throw new IndexOutOfBoundsException(ErrorMessages.position(row,column));
		return vector.get(row);
	}

	@Override
	public void set(int row, int column, double value) {
		if(column!=0) throw new IndexOutOfBoundsException(ErrorMessages.position(row,column));
		vector.set(row,value);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return vector.unsafeGet(row);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		vector.unsafeSet(row,value);
	}
	
	@Override
	public void addAt(int i, int j, double d) {
		assert(j==0);
		vector.addAt(i,d);
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		vector.addToArray(data,offset);
	}
	
	@Override
	public RowMatrix getTranspose() {
		return new RowMatrix(vector);
	}
	
	@Override
	public RowMatrix getTransposeView() {
		return new RowMatrix(vector);
	}
	
	@Override
	public AVector getColumn(int i) {
		if (i!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 1,i));
		return vector;
	}
	
	@Override
	public AVector getRowView(int i) {
		return vector.subVector(i, 1);
	}
	
	@Override
	public Vector toVector() {
		return vector.toVector();
	}
	
	@Override
	public AVector asVector() {
		return vector;
	}

	@Override
	public ColumnMatrix exactClone() {
		return new ColumnMatrix(vector.exactClone());
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return vector.equalsArray(data, offset);
	}

	@Override
	public Matrix transposeInnerProduct(Matrix s) {
		Vector v=vector.innerProduct(s).toVector();
		return Matrix.wrap(1, s.columnCount(), v.asDoubleArray());
	}
	
	@Override
	public boolean hasUncountable() {
	    return vector.hasUncountable();
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return vector.elementPowSum(p);
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return vector.elementAbsPowSum(p);
    }

}
