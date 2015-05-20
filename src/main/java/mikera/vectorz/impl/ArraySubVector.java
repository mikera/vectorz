package mikera.vectorz.impl;

import mikera.randomz.Hash;
import mikera.vectorz.AVector;

/**
 * Vector referring to a fixed offset into a double[] array
 * 
 * @author Mike
 * 
 */
public final class ArraySubVector extends ADenseArrayVector {
	private static final long serialVersionUID = 1262951505515197105L;

	private final int offset;

	public static ArraySubVector wrap(double[] values) {
		return new ArraySubVector(values);
	}
	
	private ArraySubVector(double[] values) {
		this(values,0,values.length);
	}
	
	private ArraySubVector(double[] data, int offset, int length) {
		super(length,data);
		this.offset=offset;
	}

	public static ArraySubVector wrap(double[] data, int offset, int length) {
		return new ArraySubVector(data,offset,length);
	}

	/**
	 * Constructs a vector directly referencing a sub-vector of an existing
	 * array-based Vector
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 */
	public ArraySubVector(ADenseArrayVector source, int offset, int length) {
		super(length,source.getArray());
		source.checkRange(offset,length);
		this.offset = source.getArrayOffset() + offset;
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return data[offset + i];
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		data[offset + i] = value;
	}
	
	@Override
	public double unsafeGet(int i) {
		return data[offset + i];
	}

	@Override
	public void unsafeSet(int i, double value) {
		data[offset + i] = value;
	}
	
	@Override
	public void add(AVector v) {
		checkSameLength(v);
		v.addToArray(data, offset);
	}
	
	@Override
	public void addMultiple(AVector v, double factor) {
		checkSameLength(v);
		v.addMultipleToArray(factor, 0, data, offset, length);
	}
	
	@Override
	public void addMultiple(ADenseArrayVector v, double factor) {
		checkSameLength(v);
		v.addMultipleToArray(factor, 0, data, offset, length);
	}
	
	@Override
	public void addAt(int i, double v) {
		assert((i>=0)&&(i<length));
		data[i+offset]+=v;
	}

	/**
	 * Vector hashcode, designed to match hashcode of Java double array
	 */
	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = 0; i < length; i++) {
			hashCode = 31 * hashCode + (Hash.hashCode(data[offset+i]));
		}
		return hashCode;
	}

	@Override
	public int getArrayOffset() {
		return offset;
	}
	
	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public AVector subVector(int start, int length) {
		int len=checkRange(start,length);
		if (length==0) return Vector0.INSTANCE;
		if (len==length) return this;
		return ArraySubVector.wrap(data, offset+start, length);
	}

	@Override 
	public ArraySubVector exactClone() {
		return new ArraySubVector(data.clone(),offset,length);
	}

	@Override
	protected int index(int i) {
		return offset+i;
	}
}
