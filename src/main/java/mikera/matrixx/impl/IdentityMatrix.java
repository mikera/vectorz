package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

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
	public double calculateComponent(int i, AVector v) {
		return v.get(i);
	}

	@Override
	public double get(int row, int column) {
		assert(row>=0);
		assert(column>=0);
		assert(row<dimensions);
		assert(column<dimensions);
		return (row==column)?1.0:0.0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException("Identity matrix is immutable!");
	}
	
	@Override 
	public void transform(AVector source, AVector dest) {
		dest.set(source);
	}
	
	@Override
	public AVector transform(AVector source) {
		return source.clone();		
	}
	
	@Override
	public AVector getLeadingDiagonal() {
		AVector v= Vectorz.newVector(dimensions);
		v.fill(1.0);
		return v;
	}
	
	@Override 
	public boolean isIdentity() {
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
	public AMatrix compose(AMatrix a) {
		assert(a.rowCount()==this.dimensions);
		return a.clone();
	}
	
	@Override 
	public AMatrix getTranspose() {
		return this;
	}
}
