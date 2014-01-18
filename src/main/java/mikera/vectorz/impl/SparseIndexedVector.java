package mikera.vectorz.impl;

import java.util.Arrays;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.AVectorMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Indexed sparse vector.
 * 
 * Efficient for mostly sparse vector.
 * 
 * WARNING: updates of non-indexed vectors are O(n) in the number of non-sparse elements. You should not normally
 * perform arbitrary mutation on a SparseIndexedVector if performance is a concern
 * 
 * Index must be distinct and sorted.
 * 
 * @author Mike
 *
 */
public class SparseIndexedVector extends ASparseVector {
	private static final long serialVersionUID = 750093598603613879L;

	private Index index;
	private double[] data;
	
	private SparseIndexedVector(int length, Index index) {
		this(length,index,new double[index.length()]);
	}
	
	private SparseIndexedVector(int length, Index index, double[] data) {
		super(length);
		this.index=index;
		this.data=data;
	}
	
	private SparseIndexedVector(int length, Index index, AVector data) {
		super(length);
		this.index=index;
		this.data=new double[index.length()];
		data.getElements(this.data, 0);
	}
	
	/**
	 * Creates a SparseIndexedVector with the specified index and data values.
	 * Performs no checking - Index must be distinct and sorted.
	 */
	public static SparseIndexedVector wrap(int length, Index index, double[] data) {
		assert(index.length()==data.length);
		assert(index.isDistinctSorted());
		return new SparseIndexedVector(length, index,data);
	}
	
	/**
	 * Creates a SparseIndexedVector using the given sorted Index to identify the indexes of non-zero values,
	 * and a double[] array to specify all the non-zero element values
	 */
	public static SparseIndexedVector create(int length, Index index, double[] data) {
		if (!index.isDistinctSorted()) {
			throw new VectorzException("Index must be sorted and distinct");
		}
		if (!(index.length()==data.length)) {
			throw new VectorzException("Length of index: mismatch woth data");			
		}
		return new SparseIndexedVector(length, index,data);
	}
	
	public static SparseIndexedVector createLength(int length) {
		return new SparseIndexedVector(length, Index.EMPTY,DoubleArrays.EMPTY);
	}
	
	/**
	 * Creates a SparseIndexedVector using the given sorted Index to identify the indexes of non-zero values,
	 * and a dense vector to specify all the non-zero element values
	 */
	public static SparseIndexedVector create(int length, Index index, AVector data) {
		SparseIndexedVector sv= create(length, index, new double[index.length()]);
		data.getElements(sv.data, 0);
		return sv;
	}
	
	/** 
	 * Creates a SparseIndexedVector from the given vector, ignoring the zeros in the source.
	 * 
	 */
	public static SparseIndexedVector create(AVector source) {
		int length = source.length();
		if (length==0) throw new IllegalArgumentException("Can't create a length 0 SparseIndexedVector");
		int len=0;
		for (int i=0; i<length; i++) {
			if (source.unsafeGet(i)!=0.0) len++;
		}
		int[] indexes=new int[len];
		double[] vals=new double[len];
		int pos=0;
		for (int i=0; i<length; i++) {
			double v=source.unsafeGet(i);
			if (v!=0.0) {
				indexes[pos]=i;
				vals[pos]=v;
				pos++;
			}
		}
		return wrap(length,Index.wrap(indexes),vals);
	}
	
	/** Creates a SparseIndexedVector from a row of an existing matrix */
	public static AVector createFromRow(AMatrix m, int row) {
		if (m instanceof AVectorMatrix) return create(m.getRow(row));
		return create(m.getRow(row));
	}
	
	@Override
	public int nonSparseElementCount() {
		return data.length;
	}
	
	@Override
	public void multiply (double d) {
		DoubleArrays.multiply(data, d);
	}
	
	@Override
	public void multiply (AVector v) {
		if (v instanceof AArrayVector) {
			multiply((AArrayVector)v);
			return;
		}
		for (int i=0; i<data.length; i++) {
			data[i]*=v.get(index.data[i]);
		}
	}
	
	public void multiply(AArrayVector v) {
		multiply(v.getArray(),v.getArrayOffset());
	}
	
	@Override
	public void multiply(double[] array, int offset) {
		for (int i=0; i<data.length; i++) {
			data[i]*=array[offset+index.data[i]];
		}
	}
	
	@Override
	public double magnitudeSquared() {
		double result=0.0;
		for (int i=0; i<data.length; i++) {
			double d=data[i];
			result+=d*d;
		}
		return result;
	}
	
	@Override
	public boolean isZero() {
		for (int i=0; i<data.length; i++) {
			if (data[i]!=0.0) return false;
		}
		return true;
	}
	
	@Override
	public double maxAbsElement() {
		double result=0.0;
		for (int i=0; i<data.length; i++) {
			double d=Math.abs(data[i]);
			if (d>result) result=d; 
		}
		return result;
	}
	
	@Override
	public int maxElementIndex(){
		if (data.length==0) return 0;
		double result=data[0];
		int di=0;
		for (int i=1; i<data.length; i++) {
			double d=data[i];
			if (d>result) {
				result=d; 
				di=i;
			}
		}
		if (result<0.0) { // need to find a sparse element
			int ind=sparseElementIndex();
			if (ind>0) return ind;
		}
		return index.get(di);
	}
	
 
	@Override
	public int maxAbsElementIndex(){
		if (data.length==0) return 0;
		double result=data[0];
		int di=0;
		for (int i=1; i<data.length; i++) {
			double d=Math.abs(data[i]);
			if (d>result) {
				result=d; 
				di=i;
			}
		}
		return index.get(di);
	}
	
	@Override
	public int minElementIndex(){
		if (data.length==0) return 0;
		double result=data[0];
		int di=0;
		for (int i=1; i<data.length; i++) {
			double d=data[i];
			if (d<result) {
				result=d; 
				di=i;
			}
		}
		if (result<0.0) { // need to find a sparse element
			int ind=sparseElementIndex();
			if (ind>0) return ind;
		}
		return index.get(di);
	}
	
	/**
	 * Return this index of a sparse zero element, or -1 if not sparse
	 * @return
	 */
	private int sparseElementIndex() {
		if (data.length==length) {
			return -1;
		}
		for (int i=0; i<length; i++) {
			if (!index.contains(i)) return i;
		}
		throw new VectorzException(ErrorMessages.impossible());
	}

	
	@Override
	public void negate() {
		for (int i=0; i<data.length; i++) {
			data[i]=-data[i]; 
		}
	}
	
	@Override
	public void applyOp(Op op) {
		int dlen=data.length;
		if ((dlen<length())&&(op.isStochastic()||(op.apply(0.0)!=0.0))) {
			super.applyOp(op);
		} else {
			op.applyTo(data);
		}
	}
	
	@Override
	public void abs() {
		for (int i=0; i<data.length; i++) {
			data[i]=Math.abs(data[i]); 
		}
	}

	@Override
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this,i));
		int ip=index.indexPosition(i);
		if (ip<0) return 0.0;
		return data[ip];
	}
	
	@Override
	public double unsafeGet(int i) {
		int ip=index.indexPosition(i);
		if (ip<0) return 0.0;
		return data[ip];
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public boolean isMutable() {
		return length>0;
	}
	
	@Override
	public double elementSum() {
		return DoubleArrays.elementSum(data);
	}
	
	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(data);
	}
	
	@Override
	public double dotProduct(AVector v) {
		if (v instanceof AArrayVector) return dotProduct((AArrayVector)v);
		double result=0.0;
		for (int j=0; j<data.length; j++) {
			result+=data[j]*v.unsafeGet(index.data[j]);
		}
		return result;
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int j=0; j<this.data.length; j++) {
			result+=this.data[j]*data[offset+index.data[j]];
		}
		return result;
	}
	
	public double dotProduct(AArrayVector v) {
		double[] array=v.getArray();
		int offset=v.getArrayOffset();
		return dotProduct(array,offset);
	}
	
	@Override
	public void addMultipleToArray(double factor,int offset, double[] array, int arrayOffset, int length) {
		int aOffset=arrayOffset-offset;
		
		int start=index.seekPosition(offset);
		for (int i=start; i<data.length; i++) {
			int di=index.data[i];
			// if (di<offset) continue; not needed because of seekPosition!
			if (di>=(offset+length)) return;
			array[di+aOffset]+=factor*data[i];
		}
	}
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		assert((offset>=0)&&(offset+length<=this.length));
		
		
		int start=index.seekPosition(offset);
		for (int j=start; j<data.length; j++) {
			int di=index.data[j]-offset; // index relative to offset
			if (di>=length) return;
			array[arrayOffset+di]+=data[j];
		}
	}
	
	@Override
	public void addProductToArray(double factor, int offset, AVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		if (other instanceof AArrayVector) {
			addProductToArray(factor,offset,(AArrayVector)other,otherOffset,array,arrayOffset,length);
			return;
		}
		assert(offset>=0);
		assert(offset+length<=length());
		for (int j=index.seekPosition(offset); j<data.length; j++) {
			int i =index.data[j]-offset; // index relative to offset
			if (i>=length) return;
			array[i+arrayOffset]+=factor*data[j]*other.get(i+otherOffset);
		}		
	}
	
	@Override
	public void addProductToArray(double factor, int offset, AArrayVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		assert(offset>=0);
		assert(offset+length<=length());
		double[] otherArray=other.getArray();
		otherOffset+=other.getArrayOffset();
		
		for (int j=index.seekPosition(offset); j<data.length; j++) {
			int i =index.data[j]-offset; // index relative to offset
			if (i>=length) return;
			array[i+arrayOffset]+=factor*data[j]*otherArray[i+otherOffset];
		}		
	}
	
	@Override public void getElements(double[] array, int offset) {
		Arrays.fill(array,offset,offset+length,0.0);
		copySparseValuesTo(array,offset);
	}
	
	public void copySparseValuesTo(double[] array, int offset) {
		for (int i=0; i<data.length; i++) {
			int di=index.data[i];
			array[offset+di]=data[i];
		}	
	}
	
	@Override public void copyTo(AVector v, int offset) {
		if (v instanceof AArrayVector) {
			AArrayVector av=(AArrayVector)v;
			getElements(av.getArray(),av.getArrayOffset()+offset);
		}
		v.fillRange(offset,length,0.0);
		for (int i=0; i<data.length; i++) {
			v.unsafeSet(offset+index.data[i],data[i]);
		}	
	}
	
	@Override
	public void set(AVector v) {
		if (v.length()!=length) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));
		
		int nz=(int) v.nonZeroCount();
		data=new double[nz];
		index=Index.createLength(nz);
		int di=0;
		for (int i=0; i<length; i++) {
			double val=v.unsafeGet(i);
			if (val!=0) {
				data[di]=val;
				index.set(di, i);
				di++;
			}
		}
	}

	@Override
	public void set(int i, double value) {
		int ip=index.indexPosition(i);
		if (ip<0) {
			if (value==0.0) return;
			if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
			int npos=index.seekPosition(i);
			data=DoubleArrays.insert(data,npos,value);
			index=index.insert(npos,i);
		} else {
			data[ip]=value;
		}
	}
	
	@Override
	public void addAt(int i, double value) {
		int ip=index.indexPosition(i);
		if (ip<0) {
			unsafeSet(i,value);
		} else {
			data[ip]+=value;
		}
	}

	@Override
	public Vector nonSparseValues() {
		return Vector.wrap(data);
	}
	
	@Override
	public Index nonSparseIndexes() {
		return index;
	}

	@Override
	public boolean includesIndex(int i) {
		return index.indexPosition(i)>=0;
	}
	
	@Override
	public Vector clone() {
		Vector v=Vector.createLength(length);
		for (int i=0; i<data.length; i++) {
			v.unsafeSet(index.data[i],data[i]);
		}	
		return v;
	}
	
	@Override
	public SparseIndexedVector exactClone() {
		return new SparseIndexedVector(length,index.clone(),data.clone());
	}
	
	@Override
	public void validate() {
		if (index.length()!=data.length) throw new VectorzException("Inconsistent data and index!");
		if (!index.isDistinctSorted()) throw new VectorzException("Invalid index: "+index);
		super.validate();
	}

	


}
