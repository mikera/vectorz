package mikera.vectorz.impl;

import java.util.Iterator;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.impl.IStridedArray;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Abstract base class for vectors backed by a double[] array with a constant stride
 * 
 * The double array can be directly accessed for performance purposes
 * 
 * @author Mike
 */
public abstract class AStridedVector extends AArrayVector implements IStridedArray {
	protected AStridedVector(int length, double[] data) {
		super(length,data);
	}

	private static final long serialVersionUID = -7239429584755803950L;

	/**
	 * Gets the underlying double[] data array for this vector
	 */
	@Override
	public final double[] getArray() {
		return data;
	}
	
	/**
	 * Gets the offset into the underlying double[] data array for the first element of this vector
	 */
	@Override
	public abstract int getArrayOffset();
	
	/**
	 * Gets the stride of this strided vector.
	 * @return
	 */
	public abstract int getStride();
	
	@Override
	public AStridedVector ensureMutable() {
		if (isFullyMutable()) return this;
		return clone();
	}
	
	@Override
	public boolean isRangeZero(int start, int length) {
		int stride=getStride();
		int offset=getArrayOffset()+start*stride;
		return DoubleArrays.isZero(data, offset, length,stride);
	}
	
	@Override public double dotProduct(double[] data, int offset) {
		return DoubleArrays.dotProduct(data,offset,getArray(), getArrayOffset(), getStride(), length());
	}
	
	@Override
	public double dotProduct(AVector v) {
		checkLength(v.length());
		return v.dotProduct(getArray(), getArrayOffset(), getStride());
	}

	
	@Override
	public double elementSum() {
		return DoubleArrays.elementSum(getArray(), getArrayOffset(), getStride(), length());
	}
	
	@Override
	public double elementProduct() {
		int len=length();
		double[] array=getArray();
		int offset=getArrayOffset();
		int stride=getStride();
		double result=1.0;
		for (int i=0; i<len; i++) {
			result*=array[offset+i*stride];
		}		
		return result;
	}
	
	@Override
	public double elementMax(){
		int len=length();
		double[] array=getArray();
		int offset=getArrayOffset();
		int stride=getStride();
		double max = -Double.MAX_VALUE;
		for (int i=0; i<len; i++) {
			double d=array[offset+i*stride];
			if (d>max) max=d;
		}
		return max;
	}
	
	@Override
	public double elementMin(){
		int len=length();
		double[] array=getArray();
		int offset=getArrayOffset();
		int stride=getStride();
		double min = Double.MAX_VALUE;
		for (int i=0; i<len; i++) {
			double d=array[offset+i*stride];
			if (d<min) min=d;
		}
		return min;
	}
	
	@Override
	public INDArray broadcast(int... shape) {
		int dims=shape.length;
		if (dims==0) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, shape));
		} else if (dims==1) {
			if (shape[0]!=length()) throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, shape));
			return this;
		} else if (dims==2) {
			int rc=shape[0];
			int cc=shape[1];
			if (cc!=length()) throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, shape));
			return Matrixx.wrapStrided(getArray(), rc, cc, getArrayOffset(), 0, getStride());
		}
		if (shape[dims-1]!=length()) throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, shape));
		int[] newStrides=new int[dims];
		newStrides[dims-1]=getStride();
		return Arrayz.wrapStrided(getArray(),getArrayOffset(),shape,newStrides);
	}
	
	@Override
	public AStridedMatrix broadcastLike(AMatrix target) {
		if (length()==target.columnCount()) {
			return StridedMatrix.wrap(getArray(), target.rowCount(), length(), getArrayOffset(), 0, getStride());
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, target));
		}
	}
	
	@Override
	public AVector selectView(int... inds) {
		int n=inds.length;
		int[] ix=new int[n];
		int off=getArrayOffset();
		int stride=getStride();
		for (int i=0; i<n; i++) {
			ix[i]=off+stride*inds[i];
		}
		return IndexedArrayVector.wrap(getArray(), ix);
	}
	
	@Override
	public Vector clone() {
		return Vector.create(this);
	}
	
	@Override
	public abstract void set(AVector v);
	
	@Override
	public abstract void setElements(double[] values, int offset);
	
	@Override
	public abstract void setElements(int pos, double[] values, int offset, int length);
	
	@Override
	public void add(int offset, AVector a, int aOffset, int length) {
		double[] tdata=getArray();
		int stride=getStride();
		int toffset=getArrayOffset()+offset*stride;
		a.subVector(aOffset, length).addToArray(tdata, toffset, stride);	
	}
	
	@Override
	public abstract void addAt(int i, double v);
	
	@Override
	public void addToArray(int offset, double[] destData, int destOffset,int length) {
		int thisStride=getStride();
		int thisOffset=getArrayOffset()+offset*thisStride;
		DoubleArrays.add(this.data, thisOffset, thisStride, destData, destOffset, length);
	}
	
	@Override
	public void addMultipleToArray(double factor,int offset, double[] destData, int destOffset,int length) {
		int thisStride=getStride();
		int thisOffset=getArrayOffset()+offset*thisStride;
		DoubleArrays.addMultiple(destData, destOffset, this.data, thisOffset, thisStride, length, factor);
	}
	
	@Override
	public void addToArray(double[] dest, int destOffset, int destStride) {
		int stride=getStride();
		double[] tdata=getArray();
		int toffset=getArrayOffset();
		for (int i = 0; i < length; i++) {
			dest[destOffset+i*destStride]+=tdata[toffset+i*stride];
		}
	}
	
	@Override
	public abstract void applyOp(Op op);
	
	@Override
	public final Vector applyOpCopy(Op op) {
		double[] da=toDoubleArray();
		op.applyTo(da);
		return Vector.wrap(da);
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		int stride=getStride();
		int offset=this.getArrayOffset();
		return op.reduce(init, data, offset, length, stride);
	}
	
	@Override
	public double reduce(Op2 op) {
		return DoubleArrays.reduce(op, data, getArrayOffset(), length, getStride());
	}
	
	@Override
	public void copyTo(int offset, double[] dest, int destOffset, int length, int stride) {
		int thisStride=getStride();
		int thisOffset=this.getArrayOffset();
		for (int i=offset; i<length; i++) {
			dest[destOffset+i*stride]=data[thisOffset+i*thisStride];
		}
	}
	
	@Override
	public boolean hasUncountable() {
		int stride=getStride(); 
		int offset=this.getArrayOffset();
		for(int i=0; i<length; i++) {
			double v=data[offset+(i*stride)];
			if (Vectorz.isUncountable(v)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public double[] asDoubleArray() {
		if (isPackedArray()) return getArray();
		return null;
	}

	@Override
	public boolean isPackedArray() {
		return (getStride()==1)&&(getArrayOffset()==0)&&(getArray().length==length());
	}
	
	@Override
	public int[] getStrides() {
		return new int[] {getStride()};
	}
	
	@Override
	public Iterator<Double> iterator() {
		return new StridedElementIterator(getArray(),getArrayOffset(),length(),getStride());
	}
	
	@Override
	public int getStride(int dimension) {
		if (dimension!=0) throw new IllegalArgumentException(ErrorMessages.invalidDimension(this, dimension));
		return getStride();
	}
	
	@Override
	public double elementSquaredSum() {
		return DoubleArrays.elementSquaredSum(data, getArrayOffset(), length,getStride());
	}
	
	@Override
	public void getElements(double[] dest, int destOffset) {
		int offset=getArrayOffset();
		int stride=getStride();
		for (int i=0; i<length; i++) {
			dest[destOffset+i]=data[offset+(i*stride)];
		}
	}
	
	@Override
	public void getElements(double[] dest, int destOffset, int[] indices) {
		int n=indices.length;
		int offset=getArrayOffset();
		int stride=getStride();
		for (int i=0; i<n; i++) {
			dest[destOffset+i]=data[offset+(stride*indices[i])];
		}
	}
	
	@Override
	public boolean equals(AVector v) {
		if (this==v) return true;
		int len=length();
		if (len != v.length())
			return false;
		if (v instanceof ADenseArrayVector) {
			ADenseArrayVector dav=(ADenseArrayVector)v;
			return equalsArray(dav.getArray(),dav.getArrayOffset());
		}
		int stride=getStride();
		int di=getArrayOffset();
		for (int i = 0; i < len; i++) {
			if (data[di+i*stride] != v.unsafeGet(i))
				return false;
		}
		return true;
	}
	
	@Override
	public boolean equalsArray(double[] vs, int offset) {
		int stride=getStride();
		int di=getArrayOffset();
		for (int i=0; i<length; i++) {
			if (vs[offset+i]!=data[di]) return false;
			di+=stride;
		}
		return true;
	}
	
	@Override
	public double visitNonZero(IndexedElementVisitor elementVisitor) {
		return DoubleArrays.visitNonZero(elementVisitor, getArray(), getArrayOffset(), length(),getStride());
	}
	
	@Override
	public void validate() {
		if (length>0) {
			int offset=getArrayOffset();
			if ((offset<0)||(offset>=data.length)) throw new VectorzException("offset out of bounds: "+offset);
			int lastIndex=offset+(getStride()*(length-1));
			if ((lastIndex<0)||(lastIndex>=data.length)) throw new VectorzException("lastIndex out of bounds: "+lastIndex);
		}
		super.validate();
	}
}
