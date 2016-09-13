package mikera.arrayz.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * A general N-dimensional double array implemented as a collection of (n-1) dimensional slices
 * 
 * Must have dimensionality 1 or above, and contain at least one slice
 * 
 * @author Mike
 *
 * @param <T>
 */
public final class SliceArray<T extends INDArray> extends BaseShapedArray {
	private static final long serialVersionUID = -2343678749417219155L;

	private final long[] longShape;
	private final T[] slices;
	
	private SliceArray(int[] shape, T[] slices) {
		super(shape);
		this.slices=slices;
		if (slices.length==0) throw new IllegalArgumentException("Can't create SliceArray with zero slices");
		int dims=shape.length;
		this.longShape=new long[dims];
		for (int i=0; i<dims; i++) {
			longShape[i]=shape[i];
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends INDArray>  SliceArray<T> create(INDArray a) {
		return new SliceArray<T>(a.getShape(),(T[]) a.toSliceArray());
	}
	
	/**
	 * Create a SliceArray by wrapping a set of slices.
	 * @param slices
	 * @return
	 */
	@SafeVarargs
	public static <T extends INDArray>  SliceArray<T> of(T... slices) {
		return new SliceArray<T>(IntArrays.consArray(slices.length,slices[0].getShape()),slices.clone());
	}
	
	/**
	 * Create a SliceArray by repeating the same slice instance a given number of times
	 * @param slice
	 * @param n
	 * @return
	 */
	public static SliceArray<INDArray> repeat(INDArray slice, int n) {
		ArrayList<INDArray> al=new ArrayList<INDArray>(n);
		for (int i=0; i<n; i++) {
			al.add(slice);
		}
		return SliceArray.create(al);
	}
	
	public static SliceArray<INDArray> create(List<INDArray> slices) {
		int slen=slices.size();
		if (slen==0) throw new IllegalArgumentException("Can't create SliceArray with zero slices");
		INDArray[] arr=new INDArray[slen];
		return new SliceArray<INDArray>(IntArrays.consArray(slen,slices.get(0).getShape()),slices.toArray(arr));
	}
	
	public static SliceArray<INDArray> create(List<INDArray> slices, int[] shape) {
		int slen=slices.size();
		INDArray[] arr=new INDArray[slen];
		return new SliceArray<INDArray>(shape,slices.toArray(arr));
	}
	
	@Override
	public int dimensionality() {
		return shape.length;
	}
	
	@Override
	public long[] getLongShape() {
	 	return longShape;
	}

	@Override
	public double get(int... indexes) {
		int d=indexes.length;
		T slice=slices[indexes[0]];
		switch (d) {
			case 0: throw new VectorzException("Can't do 0D get on SliceArray!");
			case 1: return slice.get();
			case 2: return slice.get(indexes[1]);
			case 3: return slice.get(indexes[1],indexes[2]);
			default: return slice.get(Arrays.copyOfRange(indexes,1,d));
		}
	}
	
	@Override
	public void set(double value) {
		for (T s:slices) {
			s.set(value);
		}
	}
	
	@Override
	public void fill(double value) {
		for (T s:slices) {
			s.fill(value);
		}
	}
	
	@Override
	public void set(int[] indexes, double value) {
		int d=indexes.length;
		if (d==0) {
			set(value);
		}
		
		T slice=slices[indexes[0]];
		switch (d) {
			case 0: throw new VectorzException("Can't do 0D set on SliceArray!");
			case 1: slice.set(value); return;
			case 2: slice.set(indexes[1],value); return;
			case 3: slice.set(indexes[1],indexes[2],value); return;
			default: slice.set(Arrays.copyOfRange(indexes,1,d),value); return;
		}
	}

	@Override
	public AVector asVector() {
		AVector v=Vector0.INSTANCE;
		for (INDArray a:slices) {
			v=v.join(a.asVector());
		}
		return v;	
	}

	@Override
	public INDArray reshape(int... dimensions) {
		return Arrayz.createFromVector(asVector(), dimensions);
	}

	@Override
	public T slice(int majorSlice) {
		return slices[majorSlice];
	}
	
	@Override
	public int componentCount() {
		return sliceCount();
	}
	
	@Override
	public T getComponent(int k) {
		return slices[k];
	}

	@Override
	public INDArray slice(int dimension, int index) {
		checkDimension(dimension);
		if (dimension==0) return slice(index);
		ArrayList<INDArray> al=new ArrayList<INDArray>(sliceCount());
		for (INDArray s:this) {
			al.add(s.slice(dimension-1,index));
		}
		return SliceArray.create(al);	
	}	

	@Override
	public long elementCount() {
		return IntArrays.arrayProduct(shape);
	}
	
	@Override
	public INDArray innerProduct(INDArray a) {
		int dims=dimensionality();
		switch (dims) {
			case 0: {
				a=a.clone();
				a.scale(get());
				return a;
			}
			case 1: {
				return this.toVector().innerProduct(a);
			}
			case 2: {
				return Matrix.create(this).innerProduct(a);
			}
		}
		
		int n=sliceCount();
		ArrayList<INDArray> al=new ArrayList<INDArray>(n);
		for (INDArray s:this) {
			al.add(s.innerProduct(a));
		}
		return Arrayz.create(al);
	}
	
	@Override
	public INDArray outerProduct(INDArray a) {
		int n=sliceCount();
		ArrayList<INDArray> al=new ArrayList<INDArray>(n);
		for (INDArray s:this) {
			al.add(s.outerProduct(a));
		}
		return Arrayz.create(al);
	}

	@Override
	public boolean isMutable() {
		for (INDArray a:slices) {
			if (a.isMutable()) return true;
		}
		return false;
	}

	@Override
	public boolean isFullyMutable() {
		for (INDArray a:slices) {
			if (!a.isFullyMutable()) return false;
		}
		return true;
	}
	
	@Override
	public boolean isZero() {
		for (INDArray a:slices) {
			if (!a.isZero()) return false;
		}
		return true;
	}

	
	@Override
	public boolean isBoolean() {
		for (INDArray a:slices) {
			if (!a.isBoolean()) return false;
		}
		return true;
	}

	@Override
	public boolean isElementConstrained() {
		for (INDArray a:slices) {
			if (a.isElementConstrained()) return true;
		}
		return false;
	}

	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public void applyOp(Op op) {
		for (INDArray a:slices) {
			a.applyOp(op);
		}
	}

	@Override
	public void applyOp(IOperator op) {
		for (INDArray a:slices) {
			a.applyOp(op);
		}
	}
	
	@Override
	public void multiply(double d) {
		for (INDArray a:slices) {
			a.scale(d);
		}
	}

	@Override
	public boolean equals(INDArray a) {
		if (!Arrays.equals(a.getShape(), this.getShape())) return false;
		for (int i=0; i<slices.length; i++) {
			if (!slices[i].equals(a.slice(i))) return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SliceArray<T> exactClone() {
		T[] newSlices=slices.clone();
		for (int i=0; i<slices.length; i++) {
			newSlices[i]=(T) newSlices[i].exactClone();
		}
		return new SliceArray<T>(shape,newSlices);
	}

	@Override
	public List<?> getSlices() {
		int n=sliceCount();
		ArrayList<Object> al=new ArrayList<Object>(n);
		if (dimensionality()==1) {
			for (int i=0; i<n ; i++) {
				al.add(Double.valueOf(slices[i].get()));
			}
			return al;
		}
		for (INDArray sl:this) {
			al.add(sl);
		}
		return al;
	}
	
	@Override
	public INDArray[] toSliceArray() {
		int n=sliceCount();
		INDArray[] al=new INDArray[n];
		for (int i=0; i<n; i++) {
			al[i]=slice(i);
		}
		return al;
	}
	
	@Override
	public INDArray[] getComponents() {
		return toSliceArray();
	}
	
	@Override
	public double[] toDoubleArray() {
		double[] result=DoubleArrays.createStorageArray(this.getShape());
		int skip=(int)slice(0).elementCount();
		for (int i=0; i<slices.length; i++) {
			INDArray s=slices[i];
			if (s.isSparse()) {
				s.addToArray(result,skip*i);				
			} else {
				s.getElements(result,skip*i);
			}
		}
		return result;
	}
	
	@Override
	public boolean equalsArray(double[] values, int offset) {
		int skip=(int)slice(0).elementCount();
		int di=offset;
		for (int i=0; i<slices.length; i++) {
			if (!slices[i].equalsArray(values,di)) return false;
			di+=skip;
		}
		return true;
	}

	@Override
	public void validate() {
		if (shape.length!=longShape.length) throw new VectorzException("Shape mismatch");
		
		long ec=0;
		for (int i=0; i<slices.length; i++) {
			T s=slices[i];
			ec+=s.elementCount();
			slices[i].validate();
			int[] ss=s.getShape();
			for (int j=0; j<ss.length; j++) {
				if(getShape(j+1)!=ss[j]) throw new VectorzException("Slice shape mismatch");
			}
		}
		if (ec!=elementCount()) throw new VectorzException("Element count mismatch");
		
		super.validate();
	}

	@Override
	public double get() {
		throw new IllegalArgumentException("0d get not supported on "+getClass());
	}

	@Override
	public double get(int x) {
		return slices[x].get();
	}

	@Override
	public double get(int x, int y) {
		return slices[x].get(y);
	}
}
