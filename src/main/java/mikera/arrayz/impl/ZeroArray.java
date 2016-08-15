package mikera.arrayz.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.ISparse;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ImmutableScalar;
import mikera.vectorz.impl.ZeroVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

/**
 * Class representing an immutable array filled entirely with zeros.
 * 
 * @author Mike
 *
 */
public final class ZeroArray extends BaseShapedArray implements ISparse {
	private static final long serialVersionUID = 7355257027343666183L;
	
	// we cache an instance of a slice, for performance and to save memory
	private INDArray sliceValue;
	
	private ZeroArray(int[] shape)  {
		super(shape);
		int dims=this.dimensionality();
		switch(dims) {
			case 1: sliceValue= ImmutableScalar.ZERO; break;
			case 2: sliceValue= ZeroVector.create(shape[1]); break;
			case 3: sliceValue= ZeroMatrix.create(shape[1],shape[2]); break;
			default: sliceValue= ZeroArray.wrap(IntArrays.removeIndex(shape, 0)); break;
		}
	}
	
	public static ZeroArray wrap(int... shape) {
		return new ZeroArray(shape);
	}
	
	/**
	 * Returns a zero array with specified shape. Takes a defensive clone of the shape array.
	 * @param shape
	 * @return
	 */
	public static ZeroArray create(int... shape) {
		return new ZeroArray(shape.clone());
	}
	
	@Override
	public long nonZeroCount() {
		return 0;
	}
	
	@Override
	public double get(int... indexes) {
		if (!IntArrays.validIndex(indexes,shape)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, indexes));
		return 0.0;
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

	@Override
	public void set(int[] indexes, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

	@Override
	public INDArray slice(int majorSlice) {
		if ((majorSlice<0)||(majorSlice>=shape[0])) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, majorSlice));
		return sliceValue;
	}

	@Override
	public INDArray slice(int dimension, int index) {
		if (dimension==0) return slice(index);
		return Arrayz.createZeroArray(IntArrays.removeIndex(shape, dimension));
	}
	
	@Override
	public List<INDArray> getSlices() {
		int sc=sliceCount();
		if (sc==0) return Collections.emptyList();
		ArrayList<INDArray> al=new ArrayList<INDArray>(sc);
		INDArray z=slice(0);
		for (int i=0; i<sc; i++) {
			al.add(z);
		}
		return al;
	}

	@Override
	public boolean isView() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isZero() {
		return true;
	}
	
	@Override
	public ZeroArray getTranspose() {
		return ZeroArray.wrap(IntArrays.reverse(shape));
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		// all done!
	}
	
	@Override
	public INDArray addCopy(INDArray a) {	
		return a.broadcastCopyLike(this);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.isZero(data, offset, (int)elementCount());
	}
	
	@Override
	public boolean equals(INDArray a) {
		if (!a.isSameShape(this)) return false;
		return a.isZero();
	}
	
	@Override
	public AVector asVector() {
		
		return Vectorz.createZeroVector(this.elementCount());
	}
	
	@Override
	public INDArray clone() {
		return Arrayz.newArray(shape);
	}
	
	@Override
	public INDArray sparseClone() {
		switch (dimensionality()) {
			case 0: return ImmutableScalar.ZERO;
			case 1: return Vectorz.createSparseMutable(shape[0]);
			case 2: return Matrixx.createSparseRows(this);
			default: {
				int n=sliceCount();
				ArrayList<INDArray> al=new ArrayList<INDArray>(n);
				for (int i=0; i<n; i++) {
					al.add(slice(i).sparseClone());
				}
				return SliceArray.create(al);
			}
		}
	}
	
	@Override
	public ZeroArray exactClone() {
		return create(shape);
	}

	@Override
	public boolean hasUncountable() {
		return false;
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return 0;
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return elementPowSum(p);
    }
}
