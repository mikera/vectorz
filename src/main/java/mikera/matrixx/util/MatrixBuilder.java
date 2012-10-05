package mikera.matrixx.util;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;

/**
 * Utility class for efficiently building vectors by addition of doubles
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

	public void add(AVector d) {
		ensureSize(length+1);
		data[length++]=d;
	}


	public AMatrix toVector() {
		AVector[] nd=new AVector[length];
		System.arraycopy(data, 0, nd, 0, length);
		return Matrixx.createFromVectors(nd);
	}
}
