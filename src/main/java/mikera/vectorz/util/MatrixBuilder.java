package mikera.vectorz.util;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Utility class for efficiently building matrices by appending vector rows
 * 
 * @author Mike
 */
public class MatrixBuilder extends AMatrix {
	private static final long serialVersionUID = -5875133722867126330L;

	private AVector[] data=new AVector[4];
	
	int length=0;
	
	private void ensureSize(int newSize) {
		if (newSize>data.length) {
			AVector[] nd=new AVector[Math.min(newSize, data.length*2)];
			System.arraycopy(data, 0, nd, 0, length);
			data=nd;
		}
	}

	public void append(Iterable<Object> d) {
		ensureSize(length+1);
		data[length++]=Vectorz.create(d);
	}
	
	public void append(AVector v) {
		ensureSize(length+1);
		data[length++]=Vectorz.create(v);
	}

	public void append(double[] ds) {
		ensureSize(length+1);
		data[length++]=Vectorz.create(ds);
	}

	public void appendRow(AVector row) {
		append(row);
	}

	@Override
	public void replaceRow(int i, AVector row) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, i));
		data[i]=row;
	}

	@Override
	public AVector getRowView(int row) {
		if ((row<0)||(row>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, row));
		return data[row];
	}

	@Override
	public int rowCount() {
		return length;
	}

	@Override
	public int columnCount() {
		return data[0].length();
	}

	@Override
	public AMatrix exactClone() {
		MatrixBuilder mb=new MatrixBuilder();
		for (int i=0; i<length; i++) {
			mb.append(data[i].exactClone());
		}
		return mb;
	}

	@Override
	public double get(int row, int column) {
		checkIndex(row,column);
		return data[row].get(column);
	}

	@Override
	public void set(int row, int column, double value) {
		checkIndex(row,column);
		 data[row].set(column,value);
	}

	@Override
	public boolean isFullyMutable() {
		for (int i=0; i<rowCount(); i++) {
			if (!data[i].isFullyMutable()) return false;
		}
		return true;
	}


}
