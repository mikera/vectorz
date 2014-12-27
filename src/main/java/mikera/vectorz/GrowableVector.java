package mikera.vectorz;

import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Implements a growable vector, intended for incrementally building vectors
 * 
 * Note that getting the underlying array or a subVector is unsafe, since the 
 * underlying array may be discarded as the vector is grown.
 * 
 * @author Mike
 *
 */
public final class GrowableVector extends AVector {
	private static final long serialVersionUID = -4560854157937758671L;

	private double[] data;
	private int count;
	
	public GrowableVector(AVector v) {
		this(v.length());
		append(v);
	}
	
	public GrowableVector(int initialCapacity) {
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
		this.count=length;
	}

	@Override
	public int length() {
		return count;
	}
	
	public int currentCapacity() {
		return data.length;
	}
	
	public void ensureCapacity(int capacity) {
		int cc=currentCapacity();
		if (capacity<=cc) return;
		
		double[] newData=new double[Math.max(capacity+5, cc*2)];
		System.arraycopy(data, 0, newData, 0, count);
		data=newData;
	}

	@Override
	public double get(int i) {
		if ((i<0)||(i>=count)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return data[i];
	}

	@Override
	public void set(int i, double value) {
		if (i<0) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		ensureCapacity(i+1);
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
	public boolean isView() {
		return false;
	}
	
	public void append(double v) {
		ensureCapacity(count+1);
		data[count++]=v;
	}
	
	public void append(double... vs) {
		int n=vs.length;
		ensureCapacity(count+n);
		System.arraycopy(vs, 0, data, count, n);
		count+=n;
	}
	
	public void append(AVector v) {
		int vl=v.length();
		ensureCapacity(count+vl);
		v.getElements(data, count);
		count+=vl;
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
		return new GrowableVector(data.clone(),count);
	}

	public void clear() {
		count=0;
	}
	
	@Override 
	public GrowableVector exactClone() {
		GrowableVector g=new GrowableVector(data.length);
		g.append(this);
		return g;
	}
	
	@Override
	public void validate() {
		if (count>data.length) throw new VectorzException("data array is wrong size!?!");
		super.validate();
	}
	
	@Override
	public Vector toVector() {
		return Vector.create(this);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		System.arraycopy(data, 0, dest, offset, count);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return DoubleArrays.dotProduct(data, offset, this.data, 0, count);
	}
}
