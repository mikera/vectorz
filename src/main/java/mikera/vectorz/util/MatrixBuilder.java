package mikera.vectorz.util;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.AVectorMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Utility class for efficiently building matrices by addition of vector rows
 * 
 * @author Mike
 */
public class MatrixBuilder extends AVectorMatrix<AVector> {
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

	@Override
	public void appendRow(AVector row) {
		append(row);
	}

	@Override
	public void replaceRow(int i, AVector row) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		data[i]=row;
	}

	@Override
	public AVector getRow(int row) {
		if ((row<0)||(row>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row));
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


}
