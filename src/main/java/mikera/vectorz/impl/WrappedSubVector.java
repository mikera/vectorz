package mikera.vectorz.impl;

import java.util.Iterator;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;

/**
 * View class referencing a contiguous subvector of another vector. 
 * @author Mike
 *
 */
public final class WrappedSubVector extends ASizedVector {
	private static final long serialVersionUID = 2323553136938665228L;

	private final AVector wrapped;
	private final int offset;
	
	private WrappedSubVector(AVector source, int offset, int length) {
		super(length);
		if (source instanceof WrappedSubVector) {
			// avoid stacking WrappedSubVectors by using underlying vector
			WrappedSubVector v=(WrappedSubVector)source;
			this.wrapped=v.wrapped;
			this.offset=offset+v.offset;
		} else {
			wrapped=source;
			this.offset=offset;
		}
	}
	
	public static WrappedSubVector wrap(AVector source, int offset, int length) {
		return new WrappedSubVector(source,offset,length);
	}
	
	@Override 
	public Iterator<Double> iterator() {
		return new VectorIterator(wrapped,offset,length);
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
	public boolean isZero() {
		return wrapped.isRangeZero(this.offset, this.length);
	}
	
	@Override
	public boolean isRangeZero(int start, int length) {
		return wrapped.isRangeZero(this.offset + start, length);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return wrapped.unsafeGet(i+offset);
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
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
	public void applyOp(Op op) {
		for (int i=0; i<length; i++) {
			int ix=i+offset;
			double v=wrapped.unsafeGet(ix);
			double nv=op.apply(v);
			if (v!=nv) wrapped.unsafeSet(ix,nv);
		}
	}
	
	@Override
	public void add(AVector src, int offset) {
		wrapped.add(this.offset,src,offset,length);
	}
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		wrapped.addToArray(this.offset+offset, array, arrayOffset, length);
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		int len=checkRange(offset,length);
		if (length==0) return Vector0.INSTANCE;
		if (len==length) return this;
		return wrapped.subVector(this.offset+offset, length);
	}
	
	@Override
	public void copyTo(int offset, AVector dest, int destOffset, int length) {
		wrapped.copyTo(this.offset+offset,dest,destOffset,length);
	}
	
	@Override
	public void copyTo(int offset, double[] dest, int destOffset, int length) {
		wrapped.copyTo(this.offset+offset,dest,destOffset,length);
	}
	
	@Override
	public void copyTo(int offset, double[] dest, int destOffset, int length, int stride) {
		wrapped.copyTo(this.offset+offset,dest,destOffset,length, stride);
	}
	
	@Override
	public AVector tryEfficientJoin(AVector a) {
		if (a instanceof WrappedSubVector) return tryEfficientJoin((WrappedSubVector)a);
		return null;
	}
	
	private AVector tryEfficientJoin(WrappedSubVector a) {
		if ((a.wrapped==this.wrapped)&&(a.offset==(this.offset+this.length))) {
			int newLength=this.length+a.length;
			if ((offset==0)&&(newLength==wrapped.length())) return wrapped;
			return new WrappedSubVector(wrapped,offset,newLength);
		}
		return null;
	}
	
	@Override
	public WrappedSubVector exactClone() {
		return new WrappedSubVector(wrapped.exactClone(),offset,length);
	}

	@Override
	public void addAt(int i, double v) {
		wrapped.addAt(offset+i,v);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i]*unsafeGet(i);
		}
		return result;
	}
	
	@Override
	public double dotProduct(double[] data, int offset, int stride) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i*stride]*unsafeGet(i);
		}
		return result;
	}
	
	@Override
	public void multiply(double factor) {
		for (int i=0; i<length; i++) {
			int ix=i+offset;
			wrapped.unsafeSet(ix,wrapped.unsafeGet(ix)*factor);
		}
	}
	
	@Override
	public double elementSum() {
		double result=0.0;
		for (int i=0; i<length; i++) {
			int ix=i+offset;
			double x=wrapped.unsafeGet(ix);
			result+=x;
		}
		return result;
	}
	
	@Override
	public double elementSquaredSum() {
		double result=0.0;
		for (int i=0; i<length; i++) {
			int ix=i+offset;
			double x=wrapped.unsafeGet(ix);
			result+=x*x;
		}
		return result;
	}
	
	@Override
	public boolean equals(AVector v) {
		if (this==v) return true;
		int len=length();
		if (len != v.length())
			return false;
		if (v instanceof ADenseArrayVector) {
			ADenseArrayVector dav=(ADenseArrayVector)v;
			return equalsArray(dav.getArray(),dav.getArrayOffset());
		}
		for (int i = 0; i < len; i++) {
			if (wrapped.unsafeGet(i+offset) != v.unsafeGet(i))
				return false;
		}
		return true;
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		int len=length();
		for (int i=0; i<len; i++) {
			if (wrapped.unsafeGet(i+this.offset)!=data[offset+i]) return false;
		}
		return true;
	}
}
