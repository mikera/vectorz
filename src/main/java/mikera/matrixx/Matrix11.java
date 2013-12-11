package mikera.matrixx;

import mikera.vectorz.util.ErrorMessages;

/**
 * Optimised 1x1 matrix implementation
 * @author Mike
 *
 */
public class Matrix11 extends AMatrix {
	private double value;
	
	public Matrix11() {
		this(0);
	}
	
	public Matrix11(double value) {
		this.value=value;
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
	public double determinant() {
		return value;
	}
	
	@Override
	public double elementSum() {
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
	public void addAt(int i, int j, double value) {
		this.value+=value;
	}
	
	@Override
	public void multiply(double factor) {
		value*=factor; 
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

}
