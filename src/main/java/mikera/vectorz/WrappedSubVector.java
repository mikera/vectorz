package mikera.vectorz;

public class WrappedSubVector extends AVector {
	private final AVector wrapped;
	private final int offset;
	private final int length;
	
	public WrappedSubVector(AVector source, int offset, int length) {
		if (offset<0) throw new IndexOutOfBoundsException("Start Index: "+offset);
		if ((offset+length)>source.length()) throw new IndexOutOfBoundsException("End Index: "+(offset+length));

		wrapped=source;
		this.offset=offset;
		this.length=length;
	}
	
	@Override
	public int length() {
		return length;
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
