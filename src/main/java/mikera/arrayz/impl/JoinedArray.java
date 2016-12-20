package mikera.arrayz.impl;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.vectorz.Op;
import mikera.vectorz.util.ErrorMessages;

/**
 * Array created by joining two arrays along a specific dimension
 * 
 * @author Mike
 *
 */
public class JoinedArray extends BaseShapedArray {
	private static final long serialVersionUID = 4929988077055768422L;

	final INDArray left;
	final INDArray right;
	final int dimension;
	final int split;
	
	private JoinedArray(INDArray left, INDArray right, int dim) {
		super(left.getShapeClone());
		this.left=left;
		this.right=right;
		dimension=dim;
		this.split=shape[dimension];
		shape[dimension]+=right.getShape(dimension);
	}
	
	public static INDArray join(INDArray a, INDArray b, int dim) {
		int n=a.dimensionality();
		if (b.dimensionality()!=n) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a, b));
		
		for (int i=0; i<n; i++) {
			if ((i!=dim)&&(a.getShape(i)!=b.getShape(i))) {
				throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a,b)); 
			}
		}
		if (a.getShape(dim)==0) return b;
		if (b.getShape(dim)==0) return a;
		return new JoinedArray(a,b,dim);
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
		Arrayz.checkShape(this, 0, majorSlice);
		if (dimension==0) {
			return (majorSlice<split)?left.slice(majorSlice):right.slice(majorSlice-split);
		} else {
			return new JoinedArray(left.slice(majorSlice),right.slice(majorSlice),dimension-1);
		}
	}
	
	@Override
	public INDArray slice(int dimension, int index) {
		if (this.dimension==dimension) {
			return (index<split)?left.slice(dimension,index):right.slice(dimension,index-split);			
		} else if (dimension==0) {
			return slice(index);
		} else {
			int nd= (dimension<this.dimension)?dimension:dimension-1;
			return left.slice(dimension,index).join(right.slice(dimension,index),nd);			
		}
	}
	
	@Override
	public int componentCount() {
		return 2;
	}
	
	@Override
	public INDArray getComponent(int k) {
		switch (k) {
			case 0: return left;
			case 1: return right;
		}
		throw new IndexOutOfBoundsException(ErrorMessages.invalidComponent(this,k));
	}

	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public void applyOp(Op op) {
		left.applyOp(op);
		right.applyOp(op);
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

	@Override
	public void setSparse(double v) {
		left.setSparse(v);
		right.setSparse(v);
	}
}
