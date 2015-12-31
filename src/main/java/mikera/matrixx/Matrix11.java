package mikera.matrixx;

import mikera.matrixx.impl.APrimitiveMatrix;
import mikera.vectorz.Op;
import mikera.vectorz.Vector1;
import mikera.vectorz.util.ErrorMessages;

/**
 * Optimised 1x1 matrix implementation
 * @author Mike
 *
 */
public final class Matrix11 extends APrimitiveMatrix {
	private static final long serialVersionUID = -1961422159148368299L;

	private double value;
	
	public Matrix11() {
		this(0);
	}
	
	public Matrix11(double value) {
		this.value=value;
	}

	public Matrix11(AMatrix m) {
		this.value=m.unsafeGet(0, 0);
	}

	@Override
	public int rowCount() {
		return 1;
	}

	@Override
	public int columnCount() {
		return 1;
	}
	
	@Override
	public int checkSquare() {
		return 1;
	}
	

	@Override
	public double determinant() {
		return value;
	}
	
	@Override
	public double elementSum() {
		return value;
	}
	
	@Override
	public double elementMax(){
		return value;
	}
	
	@Override
	public double elementMin(){
		return value;
	}
	
	@Override
	public double elementSquaredSum() {
		return value*value;
	}
	
	@Override
	public long nonZeroCount() {
		return (value==0)?0:1;
	}
	
	@Override
	public long elementCount() {
		return 1;
	}
	
	@Override
	public boolean isDiagonal() {
		return true;
	}
	
	@Override
	public boolean isZero() {
		return value==0;
	}
	
	@Override
	public boolean isIdentity() {
		return value==1.0;
	}
	
	@Override
	public Matrix11 inverse() {
		if (value==0.0) return null;
		return new Matrix11(1.0/value);
	}
	
	@Override
	public double trace() {
		return value;
	}

	@Override
	public double get(int row, int column) {
		if ((row!=0)||(column!=0)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		return value;
	}

	@Override
	public void set(int row, int column, double value) {
		if ((row!=0)||(column!=0)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		this.value=value;
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return value;
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		this.value=value;
	}
	
	@Override
	public void addAt(int i, int j, double value) {
		this.value+=value;
	}
	
	@Override
	public void applyOp(Op op) {
		value=op.apply(value);
	}
	
	@Override
	public void multiply(double factor) {
		value*=factor; 
	}
	
	@Override
	public Vector1 getRowClone(int row) {
		switch (row) {
			case 0: return Vector1.of(value);
			default: throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, row));
		}
	}
	
	@Override
	public Vector1 getColumnClone(int column) {
		switch (column) {
			case 0: return Vector1.of(value);
			default: throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, column));
		}
	}
		
	@Override
	public void getElements(double[] data, int offset) {
		data[offset]=value;
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		dest[destOffset]=value;
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		dest[destOffset]=value;
	}

	@Override
	public AMatrix exactClone() {
		return new Matrix11(value);
	}

	@Override
	public boolean equalsArray(double[] data, int offset) {
		return data[offset]==value;
	}
	
	@Override
	public double[] toDoubleArray() {
		return new double[] {value};
	}

}
