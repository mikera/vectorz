package mikera.arrayz;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.impl.BaseNDArray;
import mikera.arrayz.impl.IStridedArray;
import mikera.arrayz.impl.ImmutableArray;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ArrayIndexScalar;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.SingleDoubleIterator;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * General purpose dense strided NDArray class.
 * 
 * Allows arbitrary strided access over a dense double[] array.
 * 
 * @author Mike
 *
 */
public final class NDArray extends BaseNDArray {
	private static final long serialVersionUID = -262272579159731240L;

	private NDArray(int... shape) {
		super(new double[(int)IntArrays.arrayProduct(shape)],
				shape.length,
				0,
				shape,
				IntArrays.calcStrides(shape));
	}
	
	NDArray(double[] data, int offset, int[] shape, int[] stride) {
		this(data,shape.length,offset,shape,stride);
	}
	
	NDArray(double[] data, int dimensions, int offset, int[] shape, int[] stride) {
		super(data,shape.length,offset,shape,stride);
	}
	
	/**
	 * Wraps the given double[] data in an NDArray with the given shape.
	 * Does *not* take a defensive copy of either the data or shape array.
	 * @param data
	 * @param shape
	 * @return
	 */
	public static NDArray wrap(double[] data, int[] shape) {
		int dims=shape.length;
		return new NDArray(data,dims,0,shape,IntArrays.calcStrides(shape));
	}
	
	public static NDArray wrap(Vector v) {
		return wrap(v.getArray(),v.getShape());
	}

	public static NDArray wrap(Matrix m) {
		return wrap(m.data,m.getShape());
	}
	
	public static NDArray wrap(IStridedArray a) {
		return new NDArray(a.getArray(),a.getArrayOffset(),a.getShape(),a.getStrides());
	}
	
	public static NDArray wrap(INDArray a) {
		if (!(a instanceof IStridedArray)) throw new IllegalArgumentException(a.getClass()+" is not a strided array!");
		return wrap((IStridedArray)a);
	}
	
	/**
	 * Creates a new zero-filled NDArray with the given shape.
	 * @param shape
	 * @return
	 */
	public static NDArray newArray(int... shape) {
		return new NDArray(shape.clone());
	}
	
	@Override
	public void set(double value) {
		if (dimensions==0) {
			data[offset]=value;
		} else if (dimensions==1) {
			int n=sliceCount();
			int st=getStride(0);
			for (int i=0; i<n; i++) {
				data[offset+i*st]=value;
			}
		} else {
			for (INDArray s:getSlices()) {
				s.set(value);
			}
		}
	}

	@Override
	public void set(int x, double value) {
		if (dimensions==1) {
			data[offset+x*getStride(0)]=value;
		} else {
			throw new UnsupportedOperationException(ErrorMessages.invalidIndex(this,x));
		}
	}

	@Override
	public void set(int x, int y, double value) {
		if (dimensions==2) {
			data[offset+x*getStride(0)+y*getStride(1)]=value;
		} else {
			throw new UnsupportedOperationException(ErrorMessages.invalidIndex(this,x,y));
		}
	}

	@Override
	public void set(int[] indexes, double value) {
		int ix=offset;
		if (indexes.length!=dimensions) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this,indexes)); 
		for (int i=0; i<dimensions; i++) {
			ix+=indexes[i]*getStride(i);
		}
		data[ix]=value;
	}
	
	@Override
	public INDArray getTranspose() {
		return getTransposeView();
	}
	
	@Override
	public INDArray getTransposeView() {
		return Arrayz.wrapStrided(data,offset,IntArrays.reverse(shape),IntArrays.reverse(stride));
	}

	@Override
	public AVector asVector() {
		if (dimensions==0) {
			return ArraySubVector.wrap(data,offset,1);
		} else if (dimensions==1) {
			return Vectorz.wrapStrided(data, offset, getShape(0), getStride(0));
		} else {
			AVector v=Vector0.INSTANCE;
			int n=sliceCount();
			for (int i=0; i<n; i++) {
				v=v.join(slice(i).asVector());
			}
			return v;
		}
	}

	@Override
	public INDArray reshape(int... dimensions) {
		return super.reshape(dimensions);
	}

	@Override
	public INDArray broadcast(int... dimensions) {
		return super.broadcast(dimensions);
	}

	@Override
	public INDArray slice(int majorSlice) {
		Arrayz.checkShape(this, 0, majorSlice);

		if (dimensions==0) {
			throw new IllegalArgumentException("Can't slice a 0-d NDArray");
		} else if (dimensions==1) {
			return new ArrayIndexScalar(data,offset+majorSlice*getStride(0));
		} else if (dimensions==2) {
			int st=stride[1];
			return Vectorz.wrapStrided(data, offset+majorSlice*getStride(0), getShape(1), st);
		} else if (dimensions==3) {
			return Matrixx.wrapStrided(data, getShape(1), getShape(2),offset+majorSlice*getStride(0), getStride(1),getStride(2));
		} else {
			return Arrayz.wrapStrided(data,
					offset+majorSlice*getStride(0),
					Arrays.copyOfRange(shape, 1,dimensions),
					Arrays.copyOfRange(stride, 1,dimensions));
		}
	}
	
	@Override
	public INDArray slice(int dimension, int index) {
		Arrayz.checkShape(this, dimension, index);
		
		if (dimension==0) return slice(index);
		if (dimensions==2) {
			// note: dimension must be 1 if we are here
			return Vectorz.wrapStrided(data, offset+index*getStride(1), getShape(0), getStride(0));
		}
		return Arrayz.wrapStrided(data,
				offset+index*stride[dimension],
				IntArrays.removeIndex(shape,index),
				IntArrays.removeIndex(stride,index));	
	}	
	
	@Override
	public NDArray subArray(int[] offsets, int[] shape) {
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
		
		return new NDArray(data,
				offset+IntArrays.dotProduct(offsets, stride),
				IntArrays.copyOf(shape),
				stride);
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
		return (!isPackedArray());
	}

	@Override
	public void applyOp(Op op) {
		if (dimensions==0) {
			data[offset]=op.apply(data[offset]);
		} else if (dimensions==1) {
			int len=sliceCount();
			int st=getStride(0);
			op.applyTo(data, offset, st, len);
		} else {
			int n=sliceCount();
			for (int i=0; i<n; i++) {
				slice(i).applyOp(op);
			}		
		}
	}

	@Override
	public void applyOp(IOperator op) {
		applyOp((Op)op);
	}
	
	private boolean equalsBySlices(INDArray a) {
		int sc=sliceCount();
		if (a.sliceCount()!=sc) return false;
		
		for (int i=0; i<sc; i++) {
			if (!(slice(i).equals(a.slice(i)))) return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(INDArray a) {
		if (dimensions!=a.dimensionality()) return false;
		if (dimensions==0) return (get()==a.get());
		return equalsBySlices(a);
	}

	@Override
	public NDArray exactClone() {
		NDArray c=new NDArray(data.clone(),offset,shape.clone(),stride.clone());
		return c;
	}
	
	@Override
	public INDArray clone() {
		return Array.create(this);
	}

	@Override
	public void multiply(double d) {
		if (dimensions==0) {
			data[offset]*=d;
		} else if (dimensions==1) {
			int n=sliceCount();
			for (int i=0; i<n; i++) {
				data[offset+i*getStride(0)]*=d;
			}
		} else {
			int n=sliceCount();
			for (int i=0; i<n; i++) {
				slice(i).scale(d);
			}
		}
	}

	@Override
	public void setElements(int pos,double[] values, int offset, int length) {
		if (length==0) return;
		if (dimensions==0) {
			if (length!=1) throw new IllegalArgumentException("Must have one element!");
			if (pos!=0) throw new IllegalArgumentException("Element index out of bounds: "+pos);
			data[this.offset]=values[offset];
		} else if (dimensions==1) {
			asVector().setElements(pos,values,offset,length);
		} else {
			super.setElements(pos, values, offset, length);
		}
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		if (dimensions==0) {
			dest.put(data[offset]);
		} else if (isPackedArray()) {
			dest.put(data,0,data.length);
		} else {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				INDArray s=slice(i);
				s.toDoubleBuffer(dest);
			}
		}
	}

	@Override
	public double[] asDoubleArray() {
		return isPackedArray()?data:null;
	}

	@Override
	public List<INDArray> getSlices() {
		if (dimensions==0) {
			throw new IllegalArgumentException(ErrorMessages.noSlices(this));
		} else {
			int n=getShape(0);
			ArrayList<INDArray> al=new ArrayList<INDArray>(n);
			for (int i=0; i<n; i++) {
				al.add(slice(i));
			}
			return al;
		}
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		if (dimensionality()==0) {
			return new SingleDoubleIterator(data[offset]);
		} else {
			return super.elementIterator();
		}
	}
	
	@Override public void validate() {
		if (dimensions>shape.length) throw new VectorzException("Insufficient shape data");
		if (dimensions>stride.length) throw new VectorzException("Insufficient stride data");
		
		if ((offset<0)||(offset>=data.length)) throw new VectorzException("Offset out of bounds");
		int[] endIndex=IntArrays.decrementAll(shape);
		int endOffset=offset+IntArrays.dotProduct(endIndex, stride);
		if ((endOffset<0)||(endOffset>data.length)) throw new VectorzException("End offset out of bounds");
		super.validate();
	}
	
	@Override
	public INDArray immutable() {
		return ImmutableArray.create(this);
	}

	@Override
	public double[] getArray() {
		return data;
	}

	public static INDArray wrapStrided(double[] data, int offset,
			int[] shape, int[] strides) {
		return new NDArray(data,offset,shape,strides);
	}
}
