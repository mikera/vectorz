package mikera.vectorz.impl;

import java.nio.DoubleBuffer;

import mikera.vectorz.AVector;

/**
 * A vector class implemented using a java.nio.DoubleBuffer
 * 
 * Intended for use with native libraries that require interop with buffer memory
 * 
 * @author Mike
 *
 */
public class BufferVector extends ASizedVector {
	private static final long serialVersionUID = -24132234222851156L;

	final DoubleBuffer buffer;
	
	protected BufferVector(int length) {
		this(DoubleBuffer.allocate(length), length);
	}

	protected BufferVector(DoubleBuffer buf, int length) {
		super(length);
		this.buffer=buf;
	}
	
	public static BufferVector wrap(double[] source) {
		return new BufferVector(DoubleBuffer.wrap(source),source.length);
	}
	
	public static BufferVector wrap(DoubleBuffer source, int length) {
		return new BufferVector(source,length);
	}
	
	public static BufferVector create(AVector v) {
		return wrap(v.toDoubleArray());
	}
	
	public static BufferVector createLength(int length) {
		return new BufferVector(length);
	}

	@Override
	public double get(int i) {
		return buffer.get(i);
	}

	@Override
	public void set(int i,double value) {
		buffer.put(i,value);
	}
	
	@Override
	public double unsafeGet(int i) {
		return buffer.get(i);
	}

	@Override
	public void unsafeSet(int i, double value) {
		buffer.put(i,value);
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		checkRange(offset,length);
		if (length==0) return Vector0.INSTANCE;
		if (length==this.length) return this;
		buffer.position(offset);
		buffer.limit(offset+length);
		DoubleBuffer newBuffer=buffer.slice();
		buffer.clear();
		return wrap(newBuffer,length);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		buffer.get(dest, offset, length);
		buffer.clear();
	}

	@Override
	public BufferVector exactClone() {
		double[] newArray=new double[length];
		buffer.get(newArray);
		buffer.clear();
		return BufferVector.wrap(newArray);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i]*buffer.get(i);
		}
		return result;
	}



}
