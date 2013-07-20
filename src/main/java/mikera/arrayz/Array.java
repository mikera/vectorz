package mikera.arrayz;

import java.nio.DoubleBuffer;
import java.util.List;

import mikera.arrayz.impl.AbstractArray;
import mikera.matrixx.Matrix;
import mikera.vectorz.IOp;
import mikera.vectorz.Op;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ArrayIndexScalar;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * General purpose mutable backed N-dimensional array
 * 
 * This is the general multi-dimensional equivalent of Matrix and Vector
 * 
 * @author Mike
 *
 */
public final class Array extends AbstractArray<INDArray> {
	private final int dimensions;
	private final int[] shape;
	private final int[] strides;
	private final double[] data;

	private Array(int dims, int[] shape, int[] strides) {
		this.dimensions = dims;
		this.shape = shape;
		this.strides = strides;
		int n = (int) IntArrays.arrayProduct(shape);
		this.data = new double[n];
	}

	private Array(int dims, int[] shape, double[] data) {
		this(dims, shape, IntArrays.calcStrides(shape), data);
	}

	private Array(int dims, int[] shape, int[] strides, double[] data) {
		this.dimensions = dims;
		this.shape = shape;
		this.strides = strides;
		this.data = data;
	}

	public static Array newArray(int... shape) {
		int n = (int) IntArrays.arrayProduct(shape);
		double[] data = new double[n];
		return new Array(shape.length, shape, data);
	}

	public static Array create(INDArray a) {
		int n = (int) a.elementCount();
		double[] data = new double[n];
		a.getElements(data, 0);
		return new Array(a.dimensionality(), a.getShape(), data);
	}

	@Override
	public int dimensionality() {
		return dimensions;
	}

	@Override
	public int[] getShape() {
		return shape;
	}

	@Override
	public long[] getLongShape() {
		long[] lshape = new long[dimensions];
		IntArrays.copyIntsToLongs(shape, lshape);
		return lshape;
	}

	public int getStride(int dim) {
		return strides[dim];
	}

	public int getIndex(int... indexes) {
		int ix = 0;
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
		data[getIndex(indexes)] = value;
	}

	@Override
	public Vector asVector() {
		return Vector.wrap(data);
	}

	@Override
	public Vector toVector() {
		return Vector.create(data);
	}

	@Override
	public INDArray slice(int majorSlice) {
		return slice(0, majorSlice);
	}

	@Override
	public INDArray slice(int dimension, int index) {
		if ((dimension<0)||(dimension>=dimensions)) throw new IndexOutOfBoundsException("Dimension out of range: "+dimension);
		if (dimensions==1) return ArrayIndexScalar.wrap(data, index);
		if (dimensions==2) {
			if (dimension==0) {
				return Vectorz.wrap(data, index* shape[1], shape[1]);				
			} else {
				return Vectorz.wrapStrided(data, index, shape[0], strides[0]);
			}
		}
		
		int offset = index * getStride(dimension);
		return new NDArray(data, offset,
				IntArrays.removeIndex(shape, dimension), IntArrays.removeIndex(
						strides, dimension));
	}

	@Override
	public int sliceCount() {
		return shape[0];
	}

	@Override
	public long elementCount() {
		return data.length;
	}

	@Override
	public double elementSum() {
		return DoubleArrays.elementSum(data, 0, data.length);
	}

	@Override
	public double elementSquaredSum() {
		return DoubleArrays.elementSquaredSum(data, 0, data.length);
	}
	
	
	@Override
	public void abs() {
		DoubleArrays.abs(data, 0, data.length);
	}
	
	@Override
	public void signum() {
		DoubleArrays.signum(data, 0, data.length);
	}
	
	@Override
	public void square() {
		DoubleArrays.square(data, 0, data.length);
	}
	
	@Override
	public void exp() {
		DoubleArrays.exp(data, 0, data.length);
	}
	
	@Override
	public void log() {
		DoubleArrays.log(data, 0, data.length);
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}

	@Override
	public boolean isElementConstrained() {
		return false;
	}

	@Override
	public boolean isView() {
		return false;
	}

	@Override
	public void applyOp(Op op) {
		op.applyTo(data);
	}

	@Override
	public void applyOp(IOp op) {
		if (op instanceof Op) {
			((Op) op).applyTo(data);
		} else {
			for (int i = 0; i < data.length; i++) {
				data[i] = op.apply(data[i]);
			}
		}
	}

	@Override
	public boolean equals(INDArray a) {
		if (a instanceof Array) return equals((Array)a);
		return super.equals(a);
	}

	public boolean equals(Array a) {
		if (a.dimensions != dimensions) return false;
		if (!IntArrays.equals(shape, a.shape)) return false;
		return DoubleArrays.equals(data, a.data);
	}

	@Override
	public Array exactClone() {
		return new Array(dimensions, shape, strides, data.clone());
	}

	@Override
	public void setElements(double[] values, int offset, int length) {
		System.arraycopy(values, offset, data, 0, length);
	}

	@Override
	public void multiply(double factor) {
		DoubleArrays.multiply(data, 0, data.length, factor);
	}

	@Override
	public List<INDArray> getSlices() {
		return super.getSliceViews();
	}

	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(data);
	}

	@Override
	public INDArray clone() {
		switch (dimensions) {
		case 0: return Scalar.create(data[0]);
		case 1:	return Vector.create(data);
		case 2: return Matrix.wrap(shape[0], shape[1], data.clone());
		default: return new Array(dimensions, shape, data.clone());
		}	
	}

	@Override
	public void validate() {
		super.validate();
		if (dimensions != shape.length)
			throw new VectorzException("Inconsistent dimensionality");
		if ((dimensions>0)&&(strides[dimensions-1]!=1))
			throw new VectorzException("Last stride should be 1");
		
		if (data.length != IntArrays.arrayProduct(shape))
			throw new VectorzException("Inconsistent shape");
		if (!IntArrays.equals(strides, IntArrays.calcStrides(shape)))
			throw new VectorzException("Inconsistent strides");
	}
}
