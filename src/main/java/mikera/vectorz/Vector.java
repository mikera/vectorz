package mikera.vectorz;

public final class Vector extends ArrayVector {
	private final double[] data;


	public Vector(double[] values) {
		int length=values.length;
		data = new double[length];
		System.arraycopy(values, 0, data, 0, length);
	}

	public Vector(int length) {
		data = new double[length];
	}

	public Vector(AVector source) {
		int length = source.length();
		data = new double[length];
		source.copyTo(this.data, 0);
	}
	
	@Override
	public int length() {
		return data.length;
	}

	@Override
	public double get(int i) {
		return data[i];
	}

	@Override
	public void set(int i, double value) {
		data[i]=value;
	}
	
	public SubVector subVector(int offset, int length) {
		int len=length();
		if ((offset + length) > len)
			throw new IndexOutOfBoundsException("Upper bound " + len
					+ " breached:" + (offset + length));
		if (offset < 0)
			throw new IndexOutOfBoundsException("Lower bound breached:"
					+ offset);
		return new SubVector(this, offset, length);
	}

	@Override
	public double[] getArray() {
		return data;
	}

	@Override
	public int getArrayOffset() {
		return 0;
	}

}
