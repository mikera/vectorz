package mikera.vectorz.impl;

import java.util.Iterator;

import mikera.vectorz.AVector;
import mikera.vectorz.VectorIterator;

public final class WrappedSubVector extends AVector {
	private static final long serialVersionUID = 2323553136938665228L;

	private final AVector wrapped;
	private final int offset;
	private final int length;
	
	public WrappedSubVector(AVector source, int offset, int length) {
		if (offset<0) throw new IndexOutOfBoundsException("Start Index: "+offset);
		if ((offset+length)>source.length()) throw new IndexOutOfBoundsException("End Index: "+(offset+length));

		if (source instanceof WrappedSubVector) {
			// avoid stacking WrappedSubVectors by using underlying vector
			WrappedSubVector v=(WrappedSubVector)source;
			this.wrapped=v.wrapped;
			this.offset=offset+v.offset;
			this.length=length;
		} else {
			wrapped=source;
			this.offset=offset;
			this.length=length;
		}
	}
	
	@Override 
	public Iterator<Double> iterator() {
		return new VectorIterator(wrapped,offset,length);
	}
	
	@Override
	public int length() {
		return length;
	}
	
	@Override
	public boolean isReference() {
		return true;
	}

	@Override
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
		return wrapped.get(i+offset);
	}

	@Override
	public void set(int i, double value) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
		wrapped.set(i+offset,value);
	}
	
	@Override
	public WrappedSubVector subVector(int offset, int length) {
		if (offset<0) throw new IndexOutOfBoundsException("Start Index: "+offset);
		if ((offset+length)>this.length) throw new IndexOutOfBoundsException("End Index: "+(offset+length));
		return new WrappedSubVector(wrapped, this.offset+offset,length);
	}
}
