package mikera.arrayz;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mikera.vectorz.AVector;
import mikera.vectorz.IOp;
import mikera.vectorz.Op;
import mikera.vectorz.Tools;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.util.VectorzException;

/**
 * A general n-dimensional double array implemented as a collection of (n-1) dimensional slices
 * 
 * @author Mike
 *
 * @param <T>
 */
public class SliceArray<T extends INDArray> extends AbstractArray<T> {
	private final int[] shape;
	private final long[] longShape;
	private final T[] slices;
	
	private SliceArray(int[] shape, T[] slices) {
		this.shape=shape;
		this.slices=slices;
		int dims=shape.length;
		this.longShape=new long[dims];
		for (int i=0; i<dims; i++) {
			longShape[i]=shape[i];
		}
	}
	
	public static <T extends INDArray>  SliceArray<T> create(T... slices) {
		return new SliceArray<T>(Tools.consArray(slices.length,slices[0].getShape()),slices.clone());
	}
	
	public static <T extends INDArray> SliceArray<T> repeat (T s, int n) {
		ArrayList<T> al=new ArrayList<T>();
		for (int i=0; i<n; i++) {
			al.add(s);
		}
		return SliceArray.create(al);
	}
	
	public static <T extends INDArray>  SliceArray<T> create(List<T> slices) {
		int slen=slices.size();
		if (slen==0) throw new VectorzException("Empty list of slices provided to SliceArray");
		T[] arr=(T[]) Array.newInstance(slices.get(0).getClass(),slen);
		return new SliceArray<T>(Tools.consArray(slen,slices.get(0).getShape()),slices.toArray(arr));
	}
	
	@Override
	public int dimensionality() {
		return shape.length;
	}

	@Override
	public int[] getShape() {
		return shape;
	}
	
	@Override
	public int getShape(int dim) {
		return shape[dim];
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
	public void set(int[] indexes, double value) {
		int d=indexes.length;
		if (d==0) {
			for (int i=0; i<slices.length; i++) {
				slices[i].set(indexes,value);
			}
			return;
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
	public INDArray slice(int majorSlice) {
		return slices[majorSlice];
	}
	

	@Override
	public INDArray slice(int dimension, int index) {
		if (dimension<0) throw new IllegalArgumentException("Dimension out of range!");
		if (dimension==0) return slice(index);
		ArrayList<INDArray> al=new ArrayList<INDArray>();
		for (INDArray s:this) {
			al.add(s.slice(dimension-1,index));
		}
		return SliceArray.create(al);	
	}	

	@Override
	public long elementCount() {
		return Tools.arrayProduct(shape);
	}
	
	public INDArray innerProduct(INDArray a) {
		ArrayList<INDArray> al=new ArrayList<INDArray>();
		for (INDArray s:this) {
			al.add(s.innerProduct(a));
		}
		return Arrayz.create(al);
	}
	
	public INDArray outerProduct(INDArray a) {
		ArrayList<INDArray> al=new ArrayList<INDArray>();
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
	public void applyOp(IOp op) {
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

	@Override
	public SliceArray<T> clone() {
		return exactClone();
	}

	@Override
	public SliceArray<T> exactClone() {
		T[] newSlices=slices.clone();
		for (int i=0; i<slices.length; i++) {
			newSlices[i]=(T) newSlices[i].exactClone();
		}
		return new SliceArray<T>(shape,newSlices);
	}

	@Override
	public int sliceCount() {
		return slices.length;
	}

	@Override
	public List<T> getSlices() {
		ArrayList<T> al=new ArrayList<T>();
		for (T sl:this) {
			al.add(sl);
		}
		return al;
	}

	@Override
	public void setElements(double[] values, int offset, int length) {
		int skip=(int)slice(0).elementCount();
		for (int i=0; i<slices.length; i++) {
			slices[i].setElements(values,offset+skip*i,skip);
		}
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
}
