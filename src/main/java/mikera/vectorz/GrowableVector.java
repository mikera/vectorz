package mikera.vectorz;

/**
 * Implements a growable vector, intended for incrementally building vectors
 * 
 * Note that getting the underlying array or a subVector is unsafe, since the 
 * underlying array may be discarded as the vector is grown.
 * 
 * @author Mike
 *
 */
public final class GrowableVector extends ArrayVector {
	private static final long serialVersionUID = -4560854157937758671L;

	private double[] data;
	private int length;
	
	public GrowableVector(AVector v) {
		this(v.length());
		append(v);
	}
	
	private GrowableVector(int initialCapacity) {
		this(new double[initialCapacity],0);
	}
	
	public GrowableVector() {
		this(4);
	}
	
	/**
	 * Returns a new, empty GrowableVector with the specified initial capacity
	 */
	public static GrowableVector ofInitialCapacity(int capacity) {
		return new GrowableVector(capacity);
	}
	
	private GrowableVector(double[] array, int length) {
		this.data=array;
		this.length=length;
	}
	
	/**
	 * Gets the current underlying array of the GrowableVector. Since this array
	 * may be discarded by the GrowableVector, it is not safe to assume that
	 * it will continue to be a valid reference if the GrowableVector is changed. 
	 */
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
	
	public int currentCapacity() {
		return data.length;
	}
	
	public void ensureCapacity(int capacity) {
		int cc=currentCapacity();
		if (capacity<=cc) return;
		
		double[] newData=new double[Math.max(capacity+5, cc*2)];
		System.arraycopy(data, 0, newData, 0, length);
		data=newData;
	}

	@Override
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
		return data[i];
	}

	@Override
	public void set(int i, double value) {
		if (i<0) throw new IndexOutOfBoundsException("Index: "+i);
		ensureCapacity(i+1);
		data[i]=value;
	}
	
	@Override
	public boolean isReference() {
		return false;
	}
	
	public void append(double v) {
		ensureCapacity(length+1);
		data[length++]=v;
	}
	
	public void append(AVector v) {
		int vl=v.length();
		ensureCapacity(length+vl);
		v.copyTo(data, length);
		length+=vl;
	}
	
	/**
	 * Function to build a fixed-size vector containing a copy of data
	 * once the GrowableVector is constructed
	 * @return
	 */
	public AVector build() {
		return Vectorz.create(this);
	}

	@Override
	public GrowableVector clone() {
		return new GrowableVector(data.clone(),length);
	}

	public void clear() {
		length=0;
	}
}
