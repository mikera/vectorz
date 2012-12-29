package mikera.vectorz.util;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ArraySubVector;

/**
 * Utility class for efficiently building vectors by addition of doubles
 * @author Mike
 */
public class VectorBuilder {
	private double[] data;
	
	int length=0;
	
	public VectorBuilder() {
		data=new double[4];
	}
	
	public VectorBuilder(int initialCapacity) {
		data=new double[initialCapacity];
	}
	
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


	/**
	 * Creates a vector that wraps the internal data of this VectorBuilder.
	 * Further use of the VectorBuilder has undefined results.
	 * 
	 * @return
	 */
	public AVector toWrappingWector() {
		return ArraySubVector.wrap(data, 0, length);
	}
	
	public AVector toVector() {
		AVector v=Vectorz.newVector(length);
		v.setValues(data);
		return v;
	}
}
