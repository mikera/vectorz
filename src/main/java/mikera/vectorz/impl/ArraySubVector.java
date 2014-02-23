package mikera.vectorz.impl;

import mikera.randomz.Hash;
import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Vector referring to a fixed offset into a double[] array
 * 
 * @author Mike
 * 
 */
public final class ArraySubVector extends AArrayVector {
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

	public ArraySubVector(int length) {
		super(length,new double[length]);
		offset = 0;
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
	public ArraySubVector(AArrayVector source, int offset, int length) {
		super(length,source.getArray());
		int len=source.length();
		if ((offset < 0)||(offset + length > len)) 
			throw new IndexOutOfBoundsException(
					ErrorMessages.invalidRange(source, offset, length));
		this.offset = source.getArrayOffset() + offset;
	}

	@Override
	public double get(int i) {
		if ((i < 0) || (i >= length))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return data[offset + i];
	}

	@Override
	public void set(int i, double value) {
		if ((i < 0) || (i >= length))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
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
	public void add(AArrayVector v) {
		int vlength=v.length();
		if (vlength != length) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));
		}
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			data[offset + i] += vdata[voffset + i];
		}
	}
	
	@Override
	public void addMultiple(AArrayVector v, double factor) {
		assert (v.length() == length);
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			data[offset + i] += vdata[voffset + i]*factor;
		}
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
		int len=length();
		if ((start<0)||(start+length>len)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, start, length));
		}
		if (length==0) return Vector0.INSTANCE;
		if (len==length) return this;
		return ArraySubVector.wrap(data, offset+start, length);
	}

	@Override 
	public ArraySubVector exactClone() {
		return new ArraySubVector(data.clone(),offset,length);
	}
}
