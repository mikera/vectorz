package mikera.vectorz;

import java.util.Iterator;

import mikera.vectorz.impl.IndexedElementVisitor;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Implements a growable vector, intended for incrementally building vectors
 * 
 * Supports the following operations which etend the vector's length:
 *  - set (when used beyond array length)
 *  - append (adds a value to the end of the vector, extending length by one)
 *  
 * 
 * Note that getting the underlying array or a subVector is unsafe, since the 
 * underlying array may be discarded as the vector is grown.
 * 
 * @author Mike
 *
 */
public final class GrowableVector extends AVector {
	private static final long serialVersionUID = -4560854157937758671L;

	private static final int DEFAULT_INITIAL_CAPACITY=4;
	
	private double[] data;
	private int count;
	
	public GrowableVector(AVector v) {
		this(v.length());
		append(v);
	}
	
	public GrowableVector(int initialCapacity) {
		this(DoubleArrays.create(initialCapacity),0);
	}
	
	public GrowableVector() {
		this(DEFAULT_INITIAL_CAPACITY);
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
	
	/**
	 * Creates a GrowableVector by consuming all numbers in the given iterable object
	 * @param iterable An Iterable instance over java.lang.Number instances
	 * @return
	 */
	public static GrowableVector create(Iterable<Number> iterable) {
		GrowableVector v=new GrowableVector();
		for (Number n: iterable) {
			v.append(n.doubleValue());
		}
		return v;
	}
	
	/**
	 * Creates a GrowableVector containing a copy of the elements of a given vector
	 * @param values
	 * @return
	 */
	public static GrowableVector create(AVector values) {
		return wrap(values.toDoubleArray());
	}
	
	/**
	 * Creates a GrowableVector initialised with the given double[] values
	 */
	public static GrowableVector wrap(double[] values) {
		return new GrowableVector(values,values.length);
	}	
	
	/**
	 * Creates a GrowableVector by consuming all numbers in the given iterator
	 * @param iterable An Iterable instance over java.lang.Number instances
	 * @return
	 */
	public static GrowableVector create(Iterator<Number> iterator) {
		GrowableVector v=new GrowableVector();
		while (iterator.hasNext()) {
			v.append(iterator.next().doubleValue());
		}
		return v;
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
		checkIndex(i);
		return data[i];
	}

	@Override
	public void set(int i, double value) {
		if (i<0) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		int newMinLength=i+1;
		if (count<newMinLength) {
			ensureCapacity(newMinLength);
			count=newMinLength;
		}
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
	public double[] toDoubleArray() {
		double[] result=new double[count];
		getElements(result,0);
		return result;
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		System.arraycopy(data, 0, dest, offset, count);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return DoubleArrays.dotProduct(data, offset, this.data, 0, count);
	}

	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.equals(this.data, 0, data, offset, count);
	}

	/**
	 * Inserts a double value at the specified position in this GrowableVector.
	 * Increases the vector length by 1
	 * @param pos
	 * @param value
	 */
	public void insert(int pos, double value) {
		if (pos>count) {
			throw new IllegalArgumentException("Attempting to insert beyond bounds of GrowableVector, length="+count+" and position="+pos);
		}
		ensureCapacity(count+1);
		System.arraycopy(data, pos, data, pos+1, count-pos);
		data[pos]=value;
		count++;
	}

	public static Object create(double[] nonZeroValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double visitNonZero(IndexedElementVisitor elementVisitor) {
		return toVector().visitNonZero(elementVisitor);
	}

}
