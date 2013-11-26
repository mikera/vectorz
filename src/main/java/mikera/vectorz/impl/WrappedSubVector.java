package mikera.vectorz.impl;

import java.util.Iterator;

import mikera.vectorz.AVector;

public final class WrappedSubVector extends AWrappedVector<AVector> {
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
	public boolean isFullyMutable() {
		return wrapped.isFullyMutable();
	}
	
	@Override
	public boolean isElementConstrained() {
		return wrapped.isElementConstrained();
	}
	
	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
		return wrapped.unsafeGet(i+offset);
	}

	@Override
	public void set(int i, double value) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
		wrapped.unsafeSet(i+offset,value);
	}
	
	@Override
	public double unsafeGet(int i) {
		return wrapped.unsafeGet(i+offset);
	}

	@Override
	public void unsafeSet(int i, double value) {
		wrapped.unsafeSet(i+offset,value);
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		return wrapped.subVector(this.offset+offset, length);
	}
	
	@Override
	public WrappedSubVector exactClone() {
		return new WrappedSubVector(wrapped.exactClone(),offset,length);
	}

	@Override
	public AVector getWrappedObject() {
		return wrapped;
	}
}
