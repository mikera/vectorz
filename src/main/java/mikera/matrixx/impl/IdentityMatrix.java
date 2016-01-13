package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised identity matrix class. Immutable.
 * 
 * @author Mike
 *
 */
public class IdentityMatrix extends ADiagonalMatrix implements IFastRows, IFastColumns {
	private static final long serialVersionUID = 6273459476168581549L;
	private static final int INSTANCE_COUNT=6;
	
	private IdentityMatrix(int dimensions) {
		super(dimensions);
		if (dimensions<1) throw new IllegalArgumentException("IdentityMatrix must have at least one dimension");
	}
	
	private static final  IdentityMatrix[] INSTANCES=new IdentityMatrix[INSTANCE_COUNT];
	static {
		for (int i=1; i<INSTANCE_COUNT; i++) {
			INSTANCES[i]=new IdentityMatrix(i);
		}
	}
	
	public static IdentityMatrix create(int i) {
		if (i<INSTANCE_COUNT) return INSTANCES[i];
		return new IdentityMatrix(i);
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public boolean isZero() {
		return false;
	}
	
	@Override
	public double rowDotProduct(int i, AVector v) {
		return v.unsafeGet(i);
	}

	@Override
	public double get(int i, int j) {
		checkIndex(i,j);
		return (i==j)?1.0:0.0;
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		return (i==j)?1.0:0.0;
	}
	
	@Override
	public AxisVector getRow(int i) {
		return AxisVector.create(i,dimensions);
	}
	
	@Override
	public AxisVector getColumn(int j) {
		return AxisVector.create(j,dimensions);
	}
	
	@Override
	public AxisVector getRowView(int row) {
		return AxisVector.create(row,dimensions);
	}
	
	@Override
	public AxisVector getColumnView(int column) {
		return AxisVector.create(column,dimensions);
	}
	
	@Override
	public double getDiagonalValue(int i) {
		if ((i<0)||(i>=dimensions)) throw new IndexOutOfBoundsException("Getting diagonal value out of bounds: "+i);
		return 1.0;
	}
	
	@Override
	public double unsafeGetDiagonalValue(int i) {
		return 1.0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override 
	public void transform(AVector source, AVector dest) {
		dest.set(source);
	}
		
	@Override
	public void transformInPlace(AVector v) {
		// nothing to do
	}
	
	@Override
	public void transformInPlace(ADenseArrayVector v) {
		// nothing to do
	}
	
	@Override
	public AVector getLeadingDiagonal() {
		return RepeatedElementVector.create(dimensions, 1.0);
	}
	
	@Override 
	public boolean isIdentity() {
		return true;
	}
	
	@Override
	public boolean isOrthogonal() {
	    return true;
	}
	@Override
	public boolean isOrthogonal(double tolerance) {
		return true;
	}
	
	@Override 
	public AMatrix inverse() {
		return this;
	}
	
	@Override 
	public double determinant() {
		return 1.0;
	}
	
	@Override 
	public int rank() {
		return dimensions;
	}
	
	@Override
	public long nonZeroCount() {
		return dimensions;
	}	

	@Override
	public double elementSum() {
		return dimensions;
	}	
	
	@Override
	public double elementMin() {
		return (dimensions>1)?1.0:0.0;
	}
	
	@Override
	public double elementMax() {
		return 1.0;
	}
	
	@Override
	public double trace() {
		return dimensions;
	}
	
	@Override
	public double diagonalProduct() {
		return 1.0;
	}
	
	@Override
	public AMatrix innerProduct(ADiagonalMatrix a) {
		if(a.dimensions!=this.dimensions) throw new IllegalArgumentException(ErrorMessages.mismatch(this, a));
		return a.copy();
	}
	
	@Override 
	public AMatrix innerProduct(AMatrix a) {
		if(a.rowCount()!=this.dimensions) throw new IllegalArgumentException(ErrorMessages.mismatch(this, a));
		return a.copy(); 
	}
	
	@Override 
	public AVector innerProduct(AVector v) {
		if(v.length()!=this.dimensions) throw new IllegalArgumentException(ErrorMessages.mismatch(this, v));
		return v.copy();
	}
	
	@Override
	public ScalarMatrix multiplyCopy(double d) {
		return ScalarMatrix.create(dimensions, d);
	}
	
	@Override
	public AMatrix addCopy(AMatrix m) {
		if (!isSameShape(m))throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, m));
		
		AMatrix r=m.clone();
		if (r instanceof ADenseArrayMatrix) {
			ADenseArrayMatrix ar=(ADenseArrayMatrix) r;
			this.addToArray(ar.getArray(),ar.getArrayOffset());
		} else {
			for (int i=0; i<dimensions; i++) {
				r.addAt(i, i,1.0);
			}
		}
		return r;
	}
	
	@Override 
	public IdentityMatrix getTransposeView() {
		return this;
	}
	
	@Override
	public IdentityMatrix exactClone() {
		return create(dimensions);
	}


	@Override
	public boolean hasUncountable() {
		return false;
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return dimensions;
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return elementPowSum(p);
    }
}
