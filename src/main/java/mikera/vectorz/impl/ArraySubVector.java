package mikera.vectorz.impl;

import java.util.Arrays;

import mikera.randomz.Hash;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.DoubleArrays;

/**
 * Vector referring to a fixed offset into a double[] array
 * 
 * @author Mike
 * 
 */
public final class ArraySubVector extends ADenseArrayVector {
	private static final long serialVersionUID = 1262951505515197105L;

	private final int offset;

	private ArraySubVector(double[] values) {
		this(values,0,values.length);
	}
	
	private ArraySubVector(double[] data, int offset, int length) {
		super(length,data);
		this.offset=offset;
	}

	/**
	 * Wraps a double array as a dense ArraySubVector
	 */
	public static ArraySubVector wrap(double[] values) {
		return new ArraySubVector(values);
	}

	/**
	 * Wraps a segment of a double array as a dense ArraySubVector
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
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
	public void fill(double value) {
		Arrays.fill(data, offset, offset+length, value);
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
		v.addMultipleToArray(factor, data, offset);
	}
	
	@Override
	public void addAt(int i, double v) {
		assert((i>=0)&&(i<length));
		data[i+offset]+=v;
	}
	
	@Override
	public void applyOp(Op op) {
		op.applyTo(data, offset, length);
	}
	
	@Override
	public void abs() {
		DoubleArrays.abs(data, offset, length);
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
	public void setElements(double[] src, int srcOffset) {
		System.arraycopy(src, srcOffset, data, this.offset, length);
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
