package mikera.arrayz.impl;

import mikera.arrayz.INDArray;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

public class JoinedArray extends AbstractArray<INDArray> {

	final int[] shape;
	final INDArray left;
	final INDArray right;
	final int dimension;
	final int split;
	
	private JoinedArray(INDArray left, INDArray right, int dim) {
		this.left=left;
		this.right=right;
		dimension=dim;
		shape=left.getShapeClone();
		this.split=shape[dimension];
		shape[dimension]+=right.getShape(dimension);
	}
	
	public static JoinedArray join(INDArray a, INDArray b, int dim) {
		int n=a.dimensionality();
		if (b.dimensionality()!=n) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a, b));
		
		for (int i=0; i<n; i++) {
			if ((i!=dim)&&(a.getShape(i)!=b.getShape(i))) {
				throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a,b)); 
			}
		}
		return new JoinedArray(a,b,dim);
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
		if (indexes.length!=dimensionality()) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, indexes));
		int di=indexes[dimension];
		if (di<split) {
			return left.get(indexes);
		} else {
			indexes=indexes.clone();
			indexes[dimension]-=split;
			return right.get(indexes);
		}
	}

	@Override
	public void set(int[] indexes, double value) {
		if (indexes.length!=dimensionality()) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, indexes));
		int di=indexes[dimension];
		if (di<split) {
			left.set(indexes,value);
		} else {
			indexes=indexes.clone();
			indexes[dimension]-=split;
			right.set(indexes,value);
		}
	}

	@Override
	public INDArray slice(int majorSlice) {
		if (dimension==0) {
			return (majorSlice<split)?left.slice(majorSlice):right.slice(majorSlice-split);
		} else {
			return new JoinedArray(left.slice(majorSlice),right.slice(majorSlice),dimension-1);
		}
	}

	@Override
	public INDArray slice(int dimension, int index) {
		if (this.dimension==dimension) {
			return (index<split)?left.slice(index):right.slice(index-split);			
		} else if (dimension==0) {
			return slice(index);
		} else {
			return new JoinedArray(left.slice(dimension-1,index),right.slice(dimension-1,index),this.dimension-1);			
		}
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
		return true;
	}

	@Override
	public INDArray exactClone() {
		return new JoinedArray(left.exactClone(),right.exactClone(),dimension);
	}
	
	@Override
	public void validate() {
		if (left.getShape(dimension)+right.getShape(dimension)!=shape[dimension]) throw new Error("Inconsistent shape along split dimension");
		super.validate();
	}

	@Override
	public double get() {
		throw new IllegalArgumentException("0d get not supported on "+getClass());
	}

	@Override
	public double get(int x) {
		if ((x<0)||(x>=sliceCount())) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, x));
		}
		if (x<split) {
			return left.get(x);
		} else {
			return right.get(x-split);
		}
	}

	@Override
	public double get(int x, int y) {
		if (dimension==0) {
			if ((x<0)||(x>=sliceCount())) {
				throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, x,y));
			}
			if (x<split) {
				return left.get(x,y);
			} else {
				return right.get(x-split,y);
			}
		} else {
			if ((y<0)||(y>=sliceCount())) {
				throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, x,y));
			}
			if (y<split) {
				return left.get(x,y);
			} else {
				return right.get(x,y-split);
			}
			
		}
	}
}
