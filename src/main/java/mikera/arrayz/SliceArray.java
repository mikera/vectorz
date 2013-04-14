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

public class SliceArray<T extends INDArray> extends AbstractArray<T> {
	private final int[] shape;
	private final T[] slices;
	
	private SliceArray(int[] shape, T[] slices) {
		this.shape=shape;
		this.slices=slices;
	}
	
	public static <T extends INDArray>  SliceArray<T> create(T... slices) {
		return new SliceArray<T>(Tools.consArray(slices.length,slices[0].getShape()),slices.clone());
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
		throw new UnsupportedOperationException();
	}

	@Override
	public INDArray slice(int majorSlice) {
		return slices[majorSlice];
	}
	
	

	@Override
	public long elementCount() {
		long c=1;
		for (int d:shape) {
			c*=d;
		}
		return c;
	}
	
	public INDArray innerProduct(INDArray a) {
		ArrayList<INDArray> al=new ArrayList<INDArray>();
		for (INDArray s:this) {
			al.add(s.innerProduct(a));
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
	public void scale(double d) {
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


}
