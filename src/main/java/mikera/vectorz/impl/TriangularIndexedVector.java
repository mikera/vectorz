package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Vector with an increasing triangular index.
 * @author Mike
 *
 */
public final class TriangularIndexedVector extends AArrayVector {
	private static final long serialVersionUID = -3987292933848795478L;

	private int offset;
	private int baseStride2;

	protected TriangularIndexedVector(int length, double[] data, int offset, int baseStride2) {
		super(length, data);
		this.offset=offset;
		this.baseStride2=baseStride2;
	}
	
	public static TriangularIndexedVector wrap(int length, double[] data, int offset, int baseStride) {
		return new TriangularIndexedVector(length, data, offset, baseStride*2);
	}
	
	private int index(int i) {
		return offset+(((baseStride2+i+1)*i)>>1);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return data[index(i)];
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		data[index(i)]=value;
	}
	
	@Override
	public double unsafeGet(int i) {
		return data[index(i)];
	}

	@Override
	public void unsafeSet(int i, double value) {
		data[index(i)]=value;
	}

	@Override
	public AVector exactClone() {
		return new TriangularIndexedVector(length,data.clone(),offset,baseStride2);
	}

}
