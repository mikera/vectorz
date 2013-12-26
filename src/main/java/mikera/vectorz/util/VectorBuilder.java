package mikera.vectorz.util;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.AArrayVector;
import mikera.vectorz.impl.ArraySubVector;

/**
 * Utility class for efficiently building vectors by addition of doubles
 * @author Mike
 */
@SuppressWarnings("serial")
public class VectorBuilder extends AArrayVector {
	private double[] data;
	int length=0;
	
	public VectorBuilder() {
		data=new double[4];
	}
	
	public VectorBuilder(int initialCapacity) {
		data=new double[initialCapacity];
	}
	
	private VectorBuilder(double[] data, int length) {
		this.data=data;
		this.length=length;
	}

	private void ensureSize(int newSize) {
		if (newSize>data.length) {
			double[] nd=new double[Math.min(newSize, data.length*2)];
			System.arraycopy(data, 0, nd, 0, length);
			data=nd;
		}
	}

	public void append(double d) {
		ensureSize(length+1);
		data[length++]=d;
	}
	

	public void append(double... ds) {
		int n=ds.length;
		ensureSize(length+n);
		System.arraycopy(ds, 0, data, length, n);
		length+=n;
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
	
	public Vector toVector() {
		return Vector.create(data,0,length);
	}

	@Override
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return data[i];
	}

	@Override
	public void set(int i, double value) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		data[i]=value;		
	}

	@Override
	public double unsafeGet(int i) {
		return data[i];
	}

	@Override
	public void unsafeSet(int i, double value) {
		data[i]=value;		
	}

	@Override
	public double[] getArray() {
		return data;
	}

	@Override
	public int getArrayOffset() {
		return 0;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public AVector exactClone() {
		return new VectorBuilder(data.clone(),length);
	}

}
