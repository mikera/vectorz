package mikera.vectorz;

public class ZeroLengthVector extends PrimitiveVector {
	private ZeroLengthVector() {
	}
	
	public static ZeroLengthVector INSTANCE=new ZeroLengthVector();
	
	@Override
	public int length() {
		return 0;
	}

	@Override
	public double get(int i) {
		throw new IndexOutOfBoundsException("Attempt to get on zero length vector!");
	}

	@Override
	public void set(int i, double value) {
		throw new IndexOutOfBoundsException("Attempt to set on zero length vector!");
	}

}
