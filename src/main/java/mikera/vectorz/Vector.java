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

	/**
	 * Copy constructor from an arbitrary vector
	 * 
	 * @param source
	 */
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

	@Override
	public double[] getArray() {
		return data;
	}

	@Override
	public int getArrayOffset() {
		return 0;
	}
	
	@Override
	public void fill(double value) {
		int len=length();
		for (int i = 0; i < len; i++) {
			data[i] += value;
		}
	}
	
	@Override
	public Vector clone() {
		return new Vector(this);
	}

}
