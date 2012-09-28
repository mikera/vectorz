package mikera.vectorz;

public abstract class ArrayVector extends AVector {

	public abstract double[] getArray();
	
	public abstract int getArrayOffset();

	/**
	 * Returns a vector referencing a sub-vector of the current vector
	 * 
	 * @param offset
	 * @param length
	 * @return
	 */
	public ArraySubVector subVector(int offset, int length) {
		int len=this.length();
		if ((offset + length) > len)
			throw new IndexOutOfBoundsException("Upper bound " + len
					+ " breached:" + (offset + length));
		if (offset < 0)
			throw new IndexOutOfBoundsException("Lower bound breached:"
					+ offset);
		return new ArraySubVector(this, offset, length);
	}
}
