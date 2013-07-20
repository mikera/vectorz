package mikera.arrayz;

import java.nio.DoubleBuffer;
import java.util.List;

import mikera.arrayz.impl.AbstractArray;
import mikera.vectorz.IOp;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.IntArrays;

public final class Array extends AbstractArray<INDArray> {
	final int dimensions;
	final int[] shape;
	final int[] strides;
	final double[] data;
	
	private Array(int dims, int[] shape,int[] strides) {
		this.dimensions=dims;
		this.shape=shape;
		this.strides=strides;
		int n=(int)IntArrays.arrayProduct(shape);
		this.data=new double[n];
	}
	
	
	private Array(int dims, int[] shape, double[] data) {
		this(dims,shape,IntArrays.calcStrides(shape),data);
	}
	
	private Array(int dims, int[] shape,int[] strides,double[] data) {
		this.dimensions=dims;
		this.shape=shape;
		this.strides=strides;
		this.data=data;
	}
	
	public static Array create(INDArray a) {
		int n=(int)a.elementCount();
		double[] data=new double[n];
		a.getElements(data, 0);
		return new Array(a.dimensionality(),a.getShape(),data);
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
		long[] lshape=new long[dimensions];
		IntArrays.copyIntsToLongs(shape, lshape);
		return lshape;
	}
	
	public int getStride(int dim) {
		return strides[dim];
	}
	
	public int getIndex(int... indexes) {
		int ix=0;
		for (int i=0; i<dimensions; i++) {
			ix+=indexes[i]*getStride(i);
		}
		return ix;
	}

	@Override
	public double get(int... indexes) {
		return data[getIndex(indexes)];
	}

	@Override
	public void set(int[] indexes, double value) {
		data[getIndex(indexes)]=value;
	}

	@Override
	public Vector asVector() {
		return Vector.wrap(data);
	}

	@Override
	public INDArray slice(int majorSlice) {
		return slice(0,majorSlice);
	}

	@Override
	public INDArray slice(int dimension, int index) {
		int offset=index*getStride(dimension);
		return new NDArray(data,offset,IntArrays.removeIndex(shape, dimension),IntArrays.removeIndex(strides, dimension));
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
			((Op)op).applyTo(data);
		} else {
			for (int i=0; i<data.length; i++) {
				data[i]=op.apply(data[i]);
			}
		}
	}

	@Override
	public boolean equals(INDArray a) {
		return super.equals(a);
	}
	
	public boolean equals(Array a) {
		if (a.dimensions!=dimensions) return false;
		for (int i=0; i<dimensions; i++) {
			if (a.shape[i]!=shape[i]) return false;
		}
		return DoubleArrays.equals(data,a.data);
	}

	@Override
	public INDArray exactClone() {
		return new Array(dimensions,shape,strides,data.clone());
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
	public Array clone() {
		return new Array(dimensions,shape,data.clone());
	}
}
