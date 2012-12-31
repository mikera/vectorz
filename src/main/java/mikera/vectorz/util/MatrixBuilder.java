package mikera.vectorz.util;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Utility class for efficiently building matrices by addition of vector rows
 * @author Mike
 */
public class MatrixBuilder {
	private AVector[] data=new AVector[4];
	
	int length=0;
	
	private void ensureSize(int newSize) {
		if (newSize>data.length) {
			AVector[] nd=new AVector[Math.min(newSize, data.length*2)];
			System.arraycopy(data, 0, nd, 0, length);
			data=nd;
		}
	}

	public void add(Iterable<Object> d) {
		ensureSize(length+1);
		data[length++]=Vectorz.create(d);
	}
	
	public void add(AVector v) {
		ensureSize(length+1);
		data[length++]=Vectorz.create(v);
	}

	public void add(double[] ds) {
		ensureSize(length+1);
		data[length++]=Vectorz.create(ds);
	}

	/**
	 * Builds a matrix using a copy of the data in this MatrixBuilder
	 * @return
	 */
	public AMatrix toMatrix() {
		AVector[] nd=new AVector[length];
		System.arraycopy(data, 0, nd, 0, length);
		return Matrixx.createFromVectors(nd);
	}


}
