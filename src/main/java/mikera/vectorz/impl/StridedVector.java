package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.VectorzException;

public final class StridedVector extends AStridedVector {
	private static final long serialVersionUID = 5807998427323932401L;
	
	private final int offset;
	private final int stride;
	
	private StridedVector(double[] data, int offset, int length, int stride) {
		super(length,data);
		if ((offset<0)) throw new IndexOutOfBoundsException();
		if (length>0) {
			// check last element is in the array
			int lastOffset=(offset+(length-1)*stride);
			if ((lastOffset>=data.length)||(lastOffset<0)) throw new IndexOutOfBoundsException("StridedVector ends outside array");
		}
		this.offset=offset;
		this.stride=stride;
	}
	
	public static StridedVector wrapStrided(double[] data, int offset, int length, int stride) {
		return new StridedVector(data,offset,length,stride);
	}

	public static StridedVector wrap(double[] data, int offset, int length, int stride) {
		return wrapStrided(data,offset,length,stride);
	}
	
	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public double dotProduct(AVector v) {
		int length=checkLength(v.length());
		if (v instanceof ADenseArrayVector) {
			ADenseArrayVector av=(ADenseArrayVector) v;
			return dotProduct(av.getArray(),av.getArrayOffset());
		}
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i*stride]*v.unsafeGet(i);
		}
		return result;
	}
	
	@Override
	public double dotProduct(double[] ds, int off) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i*stride]*ds[i+off];
		}
		return result;
	}
	
	@Override
	public void set(AVector v) {
		int length=checkSameLength(v);
		v.copyTo(0, data, offset, length, stride);
	}
	
	@Override
	public void add(AVector v) {
		if (v instanceof AStridedVector) {
			add((AStridedVector)v);
			return;
		}
		super.add(v);
	}
	
	public void add(AStridedVector v) {
		int length=checkLength(v.length());
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		int vstride=v.getStride();
		for (int i=0; i<length; i++) {
			data[offset+i*stride]+=vdata[voffset+i*vstride];
		}
	}
	
	@Override
	public int getStride() {
		return stride;
	}
	
	@Override
	public int getArrayOffset() {
		return offset;
	}
	
	@Override
	public AVector subVector(int start, int length) {
		int len=checkRange(start,length);

		if (length==0) return Vector0.INSTANCE;
		if (length==len) return this;
		
		if (length==1) {
			return ArraySubVector.wrap(data, offset+start*stride, 1);
		} 
		return wrapStrided(data,offset+start*stride,length,stride);
	}
	
	@Override
	public double get(int i) {
		checkIndex(i);
		return data[offset+i*stride];
	}
	
	@Override
	public void set(int i, double value) {
		checkIndex(i);
		data[offset+i*stride]=value;
	}
	
	@Override
	public double unsafeGet(int i) {
		return data[offset+i*stride];
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		data[offset+i*stride]=value;
	}
	
	@Override
	public void addAt(int i, double value) {
		data[offset+i*stride]+=value;
	}
	
	@Override
	public void getElements(double[] dest, int destOffset) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]=data[offset+(i*stride)];
		}
	}
	
	@Override
	public StridedVector exactClone() {
		double[] data=this.data.clone();
		return wrapStrided(data,offset,length,stride);
	}

	@Override
	public void validate() {
		if (length>0) {
			if ((offset<0)||(offset>=data.length)) throw new VectorzException("offset out of bounds: "+offset);
			int lastIndex=offset+(stride*(length-1));
			if ((lastIndex<0)||(lastIndex>=data.length)) throw new VectorzException("lastIndex out of bounds: "+lastIndex);
		}
		
		super.validate();
	}

	@Override
	protected int index(int i) {
		return offset+i*stride;
	}
}
