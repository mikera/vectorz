package mikera.vectorz.util;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Utility class for efficiently building vectors by addition of doubles
 * @author Mike
 */
public class VectorBuilder {
	private double[] data=new double[4];
	
	int length=0;
	
	private void ensureSize(int newSize) {
		if (newSize>data.length) {
			double[] nd=new double[Math.min(newSize, data.length*2)];
			System.arraycopy(data, 0, nd, 0, length);
			data=nd;
		}
	}

	public void add(double d) {
		ensureSize(length+1);
		data[length++]=d;
	}


	public AVector toVector() {
		AVector v=Vectorz.createLength(length);
		v.setValues(data);
		return v;
	}
}
