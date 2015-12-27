package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;

/**
 * Mutable strided vector class
 * @author Mike
 *
 */
public final class StridedVector extends BaseStridedVector {
	private static final long serialVersionUID = 5807998427323932401L;
	
	private StridedVector(double[] data, int offset, int length, int stride) {
		super(length,data,offset,stride);
	}
	
	/**
	 * Wraps a StridedVector around a strided range of a double[] array.
	 * 
	 * Performs no bounds checking.
	 * @param data
	 * @param offset
	 * @param length
	 * @param stride
	 * @return
	 */
	public static StridedVector wrap(double[] data, int offset, int length, int stride) {
		return new StridedVector(data,offset,length,stride);
	}
	
	@Override
	public void set(int i, double value) {
		checkIndex(i);
		data[offset+i*stride]=value;
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
	public void applyOp(Op op) {
		op.applyTo(data, offset, stride, length);
	}
	
	@Override
	public void set(AVector v) {
		int length=checkSameLength(v);
		v.copyTo(0, data, offset, length, stride);
	}
		
	@Override
	public void setElements(double[] values, int offset) {
		double[] data=getArray();
		int stride=getStride();		
		int off=getArrayOffset();
		for (int i=0; i<length; i++) {
			data[off+i*stride]=values[offset+i];
		}
	}
	
	@Override
	public void setElements(int pos, double[] values, int offset, int length) {
		double[] data=getArray();
		int stride=getStride();		
		int off=getArrayOffset()+pos*stride;
		for (int i=0; i<length; i++) {
			data[off+i*stride]=values[offset+i];
		}
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
	public AVector subVector(int start, int length) {
		int len=checkRange(start,length);

		if (length==0) return Vector0.INSTANCE;
		if (length==len) return this;
		
		if (length==1) {
			return ArraySubVector.wrap(data, offset+start*stride, 1);
		} 
		return wrap(data,offset+start*stride,length,stride);
	}
	
	@Override
	public StridedVector exactClone() {
		double[] data=this.data.clone();
		return wrap(data,offset,length,stride);
	}
}
