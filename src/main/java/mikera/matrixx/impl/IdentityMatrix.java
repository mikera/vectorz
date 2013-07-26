package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.AArrayVector;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised identity matrix class. Immutable.
 * 
 * @author Mike
 *
 */
public class IdentityMatrix extends ADiagonalMatrix {
	private static final int INSTANCE_COUNT=6;
	
	private IdentityMatrix(int dimensions) {
		super(dimensions);
	}
	
	private static final  IdentityMatrix[] INSTANCES=new IdentityMatrix[INSTANCE_COUNT];
	static {
		for (int i=0; i<INSTANCE_COUNT; i++) {
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
	public double calculateElement(int i, AVector v) {
		return v.unsafeGet(i);
	}

	@Override
	public double get(int row, int column) {
		if ((row<0)||(column<0)||(row>=dimensions)||(column>=dimensions)) throw new IndexOutOfBoundsException(ErrorMessages.position(row,column));
		return (row==column)?1.0:0.0;
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return (row==column)?1.0:0.0;
	}
	
	@Override
	public AxisVector getRow(int row) {
		return AxisVector.create(row,dimensions);
	}
	
	@Override
	public AxisVector getColumn(int column) {
		return AxisVector.create(column,dimensions);
	}
	
	public double getDiagonalValue(int i) {
		if ((i<0)||(i>=dimensions)) throw new IndexOutOfBoundsException("Getting diagonal value out of bounds: "+i);
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
	public Vector transform(AVector source) {
		return source.toVector();		
	}
	
	@Override
	public void transformInPlace(AVector v) {
		// nothing to do
	}
	
	@Override
	public void transformInPlace(AArrayVector v) {
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
	public AMatrix inverse() {
		return this;
	}
	
	@Override 
	public double determinant() {
		return 1.0;
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
	public double trace() {
		return dimensions;
	}
	
	@Override 
	public Matrix innerProduct(AMatrix a) {
		if(a.rowCount()!=this.dimensions) throw new IllegalArgumentException(ErrorMessages.mismatch(this, a));
		return a.toMatrix();
	}
	
	@Override 
	public Matrix innerProduct(Matrix a) {
		if(a.rowCount()!=this.dimensions) throw new IllegalArgumentException(ErrorMessages.mismatch(this, a));
		return a.clone();
	}
	
	@Override 
	public Vector innerProduct(AVector v) {
		if(v.length()!=this.dimensions) throw new IllegalArgumentException(ErrorMessages.mismatch(this, v));
		return v.toVector();
	}
	
	@Override 
	public Vector innerProduct(Vector v) {
		if(v.length()!=this.dimensions) throw new IllegalArgumentException(ErrorMessages.mismatch(this, v));
		return v.clone();
	}
	
	@Override 
	public IdentityMatrix getTransposeView() {
		return this;
	}
	
	@Override
	public IdentityMatrix exactClone() {
		return create(dimensions);
	}
}
