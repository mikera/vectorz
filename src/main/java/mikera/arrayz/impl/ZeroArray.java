package mikera.arrayz.impl;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.ISparse;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.vectorz.impl.ImmutableScalar;
import mikera.vectorz.impl.ZeroVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

public final class ZeroArray extends AbstractArray<INDArray> implements ISparse {
	private static final long serialVersionUID = 7355257027343666183L;

	private final int[] shape; 
	
	private ZeroArray(int[] shape)  {
		this.shape=shape;
	}
	
	public static ZeroArray wrap(int... shape) {
		return new ZeroArray(shape);
	}
	
	public static ZeroArray create(int... shape) {
		return new ZeroArray(shape.clone());
	}

	@Override
	public int dimensionality() {
		return shape.length;
	}
	
	@Override
	public long nonZeroCount() {
		return 0;
	}

	@Override
	public int[] getShape() {
		return shape;
	}

	@Override
	public double get(int... indexes) {
		if (!IntArrays.validIndex(indexes,shape)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, indexes));
		return 0.0;
	}

	@Override
	public void set(int[] indexes, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

	@Override
	public INDArray slice(int majorSlice) {
		if ((majorSlice<0)||(majorSlice>=shape[0])) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, majorSlice));
		switch (dimensionality()) {
		case 1: return ImmutableScalar.ZERO;
		case 2: return ZeroVector.create(shape[1]);
		case 3: return ZeroMatrix.create(shape[1],shape[2]);
		default: return ZeroArray.wrap(IntArrays.removeIndex(shape, 0));
		}
	}

	@Override
	public INDArray slice(int dimension, int index) {
		if (dimension==0) return slice(index);
		return Arrayz.createZeroArray(IntArrays.removeIndex(shape, dimension));
	}

	@Override
	public int sliceCount() {
		return shape[0];
	}

	@Override
	public long elementCount() {
		return IntArrays.arrayProduct(shape);
	}

	@Override
	public boolean isView() {
		return false;
	}

	@Override
	public INDArray exactClone() {
		return ZeroArray.create(shape);
	}

	@Override
	public double get() {
		if (shape.length!=0) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this));
		return 0.0;
	}

	@Override
	public double get(int x) {
		if (shape.length!=1) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this));
		if ((x<0)||(x>=shape[0])) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, x));
		return 0.0;
	}

	@Override
	public double get(int x, int y) {
		if (shape.length!=2) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this));
		if ((x<0)||(x>=shape[0])) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, x,y));
		if ((y<0)||(y>=shape[1])) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, x,y));
		return 0.0;
	}

}
