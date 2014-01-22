package mikera.arrayz.impl;

import java.nio.DoubleBuffer;
import java.util.Arrays;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.ImmutableScalar;
import mikera.vectorz.impl.ImmutableVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

/**
 * Immutable N-dimensional array class
 * 
 * Uses strided array storage
 * 
 * @author Mike
 *
 */
public class ImmutableArray extends BaseNDArray {
	private static final long serialVersionUID = 2078025371733533775L;

	private ImmutableArray(int dims, int[] shape, int[] strides) {
		this(new double[(int)IntArrays.arrayProduct(shape)],shape.length,0,shape,strides);
	}
	
	private ImmutableArray(double[] data, int dimensions, int offset, int[] shape, int[] stride) {
		super(data,dimensions,offset,shape,stride);
	}
	
	private ImmutableArray(int[] shape, double[] data) {
		this(shape.length, shape, IntArrays.calcStrides(shape), data);
	}

	private ImmutableArray(int dims, int[] shape, double[] data) {
		this(dims, shape, IntArrays.calcStrides(shape), data);
	}
	
	public static INDArray wrap(double[] data, int[] shape) {
		long ec=IntArrays.arrayProduct(shape);
		if (data.length!=ec) throw new IllegalArgumentException("Data array does not have correct number of elements, expected: "+ec);
		return new ImmutableArray(shape.length,shape,data);
	}

	private ImmutableArray(int dims, int[] shape, int[] strides, double[] data) {
		this(data,dims,0,shape,strides);
	}
	
	@Override
	public int dimensionality() {
		return dimensions;
	}

	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isElementConstrained() {
		return true;
	}

	@Override
	public int[] getShape() {
		return shape;
	}
	
	@Override
	public int[] getShapeClone() {
		return shape.clone();
	}

	@Override
	public long[] getLongShape() {
		long[] lshape = new long[dimensions];
		IntArrays.copyIntsToLongs(shape, lshape);
		return lshape;
	}

	public int getStride(int dim) {
		return stride[dim];
	}

	@Override
	public int getIndex(int... indexes) {
		int ix = offset;
		for (int i = 0; i < dimensions; i++) {
			ix += indexes[i] * getStride(i);
		}
		return ix;
	}

	@Override
	public double get(int... indexes) {
		return data[getIndex(indexes)];
	}

	@Override
	public void set(int[] indexes, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

	@Override
	public INDArray slice(int majorSlice) {
		if (dimensions==0) {
			throw new IllegalArgumentException("Can't slice a 0-d NDArray");
		} else if (dimensions==1) {
			return ImmutableScalar.create(get(majorSlice));
		} else {
			return new ImmutableArray(data,
					dimensions-1,
					offset+majorSlice*getStride(0),
					Arrays.copyOfRange(shape, 1,dimensions),
					Arrays.copyOfRange(stride, 1,dimensions));
		}
	}
	
	@Override
	public INDArray slice(int dimension, int index) {
		if ((dimension<0)||(dimension>=dimensions)) throw new IllegalArgumentException(ErrorMessages.invalidDimension(this, dimension));
		if (dimension==0) return slice(index);
		return new ImmutableArray(data,
				dimensions-1,
				offset+index*stride[dimension],
				IntArrays.removeIndex(shape,index),
				IntArrays.removeIndex(stride,index));	
	}	
	
	
	@Override
	public ImmutableArray subArray(int[] offsets, int[] shape) {
		int n=dimensions;
		if (offsets.length!=n) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, offsets));
		if (shape.length!=n) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, offsets));
		
		if (IntArrays.equals(shape, this.shape)) {
			if (IntArrays.isZero(offsets)) {
				return this;
			} else {
				throw new IllegalArgumentException("Invalid subArray offsets");
			}
		}
		
		return new ImmutableArray(data,
				n,
				offset+IntArrays.dotProduct(offsets, stride),
				IntArrays.copyOf(shape),
				stride);
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
	public AVector asVector() {
		if (dimensions>0) return super.asVector();
		return ImmutableVector.wrap(new double[] {data[offset]});
	}
		
	@Override
	public INDArray exactClone() {
		return new ImmutableArray(data.clone(),dimensions,offset,shape.clone(),stride.clone());
	}
	
	@Override
	public INDArray sparseClone() {
		return Arrayz.createSparse(this);
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		if (dimensions>0) {
			super.toDoubleBuffer(dest);
		} else {
			dest.put(data[offset]);
		}
	}

	public static INDArray create(INDArray a) {
		int[] shape=a.getShape();
		int n=(int)IntArrays.arrayProduct(shape);
		double[] newData = new double[n];
		a.getElements(newData, 0);
		return ImmutableArray.wrap(newData, shape);
	}

	@Override
	public double[] getArray() {
		throw new UnsupportedOperationException("Array access not supported by Immutablearray");
	}
}
