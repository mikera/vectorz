package mikera.arrayz;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.impl.BaseShapedArray;
import mikera.arrayz.impl.IDenseArray;
import mikera.arrayz.impl.IStridedArray;
import mikera.arrayz.impl.ImmutableArray;
import mikera.indexz.Index;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.Scalar;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ArrayIndexScalar;
import mikera.vectorz.impl.StridedElementIterator;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * General purpose mutable dense N-dimensional array
 * 
 * This is the general multi-dimensional equivalent of Matrix and Vector, and as such is the 
 * most efficient storage type for dense 3D+ arrays
 * 
 * @author Mike
 * 
 */
public final class Array extends BaseShapedArray implements IStridedArray, IDenseArray {
	private static final long serialVersionUID = -8636720562647069034L;

	private final int dimensions;
	private final int[] strides;
	private final double[] data;

	private Array(int dims, int[] shape, int[] strides) {
		super(shape);
		this.dimensions = dims;
		this.strides = strides;
		int n = (int) IntArrays.arrayProduct(shape);
		this.data = new double[n];
	}
	
	private Array(int[] shape, double[] data) {
		this(shape.length, shape, IntArrays.calcStrides(shape), data);
	}

	private Array(int dims, int[] shape, double[] data) {
		this(dims, shape, IntArrays.calcStrides(shape), data);
	}
	
	public static INDArray wrap(double[] data, int... shape) {
		long ec=IntArrays.arrayProduct(shape);
		if (data.length!=ec) throw new IllegalArgumentException("Data array does not have correct number of elements, expected: "+ec);
		return new Array(shape.length,shape,data);
	}

	private Array(int dims, int[] shape, int[] strides, double[] data) {
		super(shape);
		this.dimensions = dims;
		this.strides = strides;
		this.data = data;
	}
	
	public static Array wrap(Vector v) {
		return new Array(v.getShape(),v.getArray());
	}
	
	public static Array wrap(Matrix m) {
		return new Array(m.getShape(),m.getArray());
	}

	public static Array newArray(int... shape) {
		return new Array(shape.length, shape, createStorage(shape));
	}

	public static Array create(INDArray a) {
		int[] shape=a.getShape();
		return new Array(a.dimensionality(), shape, a.toDoubleArray());
	}
	
	public static double[] createStorage(int... shape) {
		long ec=1;
		for (int i=0; i<shape.length; i++) {
			int si=shape[i];
			if ((ec*si)!=(((int)ec)*si)) throw new IllegalArgumentException(ErrorMessages.tooManyElements(shape));
			ec*=shape[i];
		}
		int n=(int)ec;
		if (ec!=n) throw new IllegalArgumentException(ErrorMessages.tooManyElements(shape));
		return new double[n];
	}

	@Override
	public int dimensionality() {
		return dimensions;
	}
	
	@Override
	protected final void checkDimension(int dimension) {
		if ((dimension < 0) || (dimension >= dimensions))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this,dimension));
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
		checkDimension(dimension);
		if (dimensions == 1) return ArrayIndexScalar.wrap(data, index);
		if (dimensions == 2) {
			if (dimension == 0) {
				return Vectorz.wrap(data, index * shape[1], shape[1]);
			} else {
				return Vectorz.wrapStrided(data, index, shape[0], strides[0]);
			}
		}

		int offset = index * getStride(dimension);
		return new NDArray(
				data, 
				offset,
				IntArrays.removeIndex(shape, dimension), 
				IntArrays.removeIndex(strides, dimension));
	}
	
	@Override
	public INDArray getTranspose() {
		return getTransposeView();
	}
	
	@Override
	public INDArray getTransposeView() {
		return NDArray.wrapStrided(data, 0, IntArrays.reverse(shape), IntArrays.reverse(strides));
	}
	
	@Override
	public INDArray subArray(int[] offsets, int[] shape) {
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
		
		int[] strides=IntArrays.calcStrides(this.shape);
		return new NDArray(data,
				IntArrays.dotProduct(offsets, strides),
				IntArrays.copyOf(shape),
				strides);
	}

	@Override
	public long elementCount() {
		return data.length;
	}

	@Override
	public double elementSum() {
		return DoubleArrays.elementSum(data);
	}
	
	@Override
	public double elementMax(){
		return DoubleArrays.elementMax(data);
	}
	
	@Override
	public double elementMin(){
		return DoubleArrays.elementMin(data);
	}

	@Override
	public double elementSquaredSum() {
		return DoubleArrays.elementSquaredSum(data);
	}

	@Override
	public void abs() {
		DoubleArrays.abs(data);
	}

	@Override
	public void signum() {
		DoubleArrays.signum(data);
	}

	@Override
	public void square() {
		DoubleArrays.square(data);
	}

	@Override
	public void exp() {
		DoubleArrays.exp(data);
	}

	@Override
	public void log() {
		DoubleArrays.log(data);
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
	public void applyOp(IOperator op) {
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
		if (a instanceof Array) return equals((Array) a);
		if (!isSameShape(a)) return false;
		return a.equalsArray(data, 0);
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
	public void setElements(int pos, double[] values, int offset, int length) {
		System.arraycopy(values, offset, data, pos, length);
	}
	
	@Override
	public void getElements(double[] values, int offset) {
		System.arraycopy(data, 0, values, offset, data.length);
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		return new StridedElementIterator(data,0,(int)elementCount(),1);
	}

	@Override
	public void multiply(double factor) {
		DoubleArrays.multiply(data, 0, data.length, factor);
	}

	@Override
	public List<?> getSlices() {
		if (dimensions==1) {
			int n=sliceCount();
			ArrayList<Double> al=new ArrayList<Double>(n);
			for (int i=0; i<n; i++) {
				al.add(get(i));
			}
			return al;
		} else {
			return super.getSliceViews();
		}
	}

	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(data);
	}
	
	@Override
	public double[] toDoubleArray() {
		return DoubleArrays.copyOf(data);
	}
	
	@Override
	public double[] asDoubleArray() {
		return data;
	}

	@Override
	public INDArray clone() {
		// always return the efficient type for each dimensionality
		switch (dimensions) {
		case 0:
			return Scalar.create(data[0]);
		case 1:
			return Vector.create(data);
		case 2:
			return Matrix.wrap(shape[0], shape[1], DoubleArrays.copyOf(data));
		default:
			return Array.wrap(DoubleArrays.copyOf(data),shape);
		}
	}

	@Override
	public void validate() {
		super.validate();
		if (dimensions != shape.length)
			throw new VectorzException("Inconsistent dimensionality");
		if ((dimensions > 0) && (strides[dimensions - 1] != 1))
			throw new VectorzException("Last stride should be 1");

		if (data.length != IntArrays.arrayProduct(shape))
			throw new VectorzException("Inconsistent shape");
		if (!IntArrays.equals(strides, IntArrays.calcStrides(shape)))
			throw new VectorzException("Inconsistent strides");
	}

	/**
	 * Creates a new matrix using the elements in the specified vector.
	 * Truncates or zero-pads the data as required to fill the new matrix
	 * @param data
	 * @param rows
	 * @param columns
	 * @return
	 */
	public static Array createFromVector(AVector a, int... shape) {
		Array m = Array.newArray(shape);
		int n=(int)Math.min(m.elementCount(), a.length());
		a.copyTo(0, m.data, 0, n);
		return m;
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
	public int[] getStrides() {
		return strides;
	}

	@Override
	public boolean isPackedArray() {
		return true;
	}
	
	@Override
	public boolean isZero() {
		return DoubleArrays.isZero(data);
	}

	@Override
	public INDArray immutable() {
		return ImmutableArray.wrap(DoubleArrays.copyOf(data), this.shape);
	}

	@Override
	public double get() {
		if (dimensions==0) {
			return data[0];
		} else {
			throw new IllegalArgumentException("O-d get not supported on Array of shape: "+Index.of(this.getShape()).toString());
		}
	}

	@Override
	public double get(int x) {
		if (dimensions==1) {
			return data[x];
		} else {
			throw new IllegalArgumentException("1-d get not supported on Array of shape: "+Index.of(this.getShape()).toString());
		}
	}

	@Override
	public double get(int x, int y) {
		if (dimensions==2) {
			return data[x*strides[0]+y];
		} else {
			throw new IllegalArgumentException("2-d get not supported on Array of shape: "+Index.of(this.getShape()).toString());
		}
	}

	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.equals(this.data, 0, data, offset, Tools.toInt(elementCount()));
	}

}
