package mikera.vectorz.impl;

import java.util.ArrayList;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.AVectorMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * Indexed sparse vector.
 * 
 * Efficient for mostly sparse vectors. Maintains a indexed array of elements which may be non-zero. 
 * 
 * WARNING: individual updates of non-indexed elements are O(n) in the number of non-sparse elements. You should not normally
 * perform element-wise mutation on a SparseIndexedVector if performance is a concern
 * 
 * Index must be distinct and sorted.
 * 
 * @author Mike
 *
 */
public class SparseIndexedVector extends ASparseIndexedVector {
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
	
	private SparseIndexedVector(int length, Index index, AVector source) {
		this(length,index,source.toDoubleArray());
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
	 * Creates a SparseIndexedVector with the specified index and data values.
	 * Performs no checking - Index must be distinct and sorted.
	 */
	public static SparseIndexedVector wrap(int length, int[] indices, double[] data) {
		Index index=Index.wrap(indices);
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
		return new SparseIndexedVector(length, index.clone(),DoubleArrays.copyOf(data));
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
		if (source instanceof ASparseVector) return create((ASparseVector) source);
		int srcLength = source.length();
		if (srcLength==0) throw new IllegalArgumentException("Can't create a length 0 SparseIndexedVector");
		int[] indexes=source.nonZeroIndices();
		int len=indexes.length;
		double[] vals=new double[len];
		for (int i=0; i<len; i++) {
			vals[i]=source.unsafeGet(indexes[i]);
		}
		return wrap(srcLength,Index.wrap(indexes),vals);
	}
	
	public static SparseIndexedVector create(ASparseVector source) {
		int length = source.length();
		if (length==0) throw new IllegalArgumentException("Can't create a length 0 SparseIndexedVector");
		Index ixs=source.nonSparseIndex();
		int n=ixs.length();
		double[] vals=new double[n];
		for (int i=0; i<n; i++) {
			vals[i]=source.unsafeGet(ixs.unsafeGet(i));
		}
		return wrap(length,ixs,vals);
	}
	
	public static SparseIndexedVector create(SparseHashedVector source) {
		int length = source.length();
		if (length==0) throw new IllegalArgumentException("Can't create a length 0 SparseIndexedVector");
		Index ixs=source.nonSparseIndex();
		int n=ixs.length();
		double[] vals=new double[n];
		for (int i=0; i<n; i++) {
			vals[i]=source.unsafeGet(ixs.unsafeGet(i));
		}
		return wrap(length,ixs,vals);
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
	public void add(AVector v) {
		if (v instanceof ASparseVector) {
			add((ASparseVector)v);
			return;
		}
		includeIndices(v);	
		for (int i=0; i<data.length; i++) {
			data[i]+=v.unsafeGet(index.get(i));
		}
	}
	
	@Override
	public void add(ASparseVector v) {
        if (v instanceof ZeroVector) {
            return;
        }
		includeIndices(v);	
		for (int i=0; i<data.length; i++) {
			data[i]+=v.unsafeGet(index.get(i));
		}
	}
	
	@Override
	public void multiply (double d) {
		if (d==0.0) {
			data=DoubleArrays.EMPTY;
			index=Index.EMPTY;
		} else {
			DoubleArrays.multiply(data, d);
		}
	}
	
	@Override
	public void multiply(AVector v) {
		if (v instanceof ADenseArrayVector) {
			multiply((ADenseArrayVector)v);
			return;
		}
		checkSameLength(v);
		double[] data=this.data;
		int[] ixs=index.data;
		for (int i=0; i<data.length; i++) {
			data[i]*=v.unsafeGet(ixs[i]);
		}
	}
	
	public void multiply(ADenseArrayVector v) {
		multiply(v.getArray(),v.getArrayOffset());
	}
	
	@Override
	public void multiply(double[] array, int offset) {
		double[] data=this.data;
		int[] ixs=index.data;
		for (int i=0; i<data.length; i++) {
			data[i]*=array[offset+ixs[i]];
		}
	}
	
	@Override
	public double maxAbsElement() {
		double[] data=this.data;
		double result=0.0;
		for (int i=0; i<data.length; i++) {
			double d=Math.abs(data[i]);
			if (d>result) result=d; 
		}
		return result;
	}
	
	@Override
	public int maxElementIndex(){
		double[] data=this.data;
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
		double[] data=this.data;
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
		double[] data=this.data;
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
		if (result>0.0) { // need to find a sparse element
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
		DoubleArrays.negate(data);
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
		DoubleArrays.abs(data);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
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
	public void setElements(double[] array, int offset) {
		int nz=DoubleArrays.nonZeroCount(array, offset, length);
		int[] ixs=new int[nz];
		double[] data=new double[nz];
		this.data=data;
		int di=0;
		for (int i=0; i<length; i++) {
			double x=array[offset+i];
			if (x==0.0) continue;
			ixs[di]=i;
			data[di]=x;
			di++;
		}
		index=Index.wrap(ixs);
	}
	
	@Override 
	public void setElements(int pos,double[] array, int offset, int length) {
		if (length>=this.length) {
			setElements(array,offset);
			return;
		}
		
		int nz=DoubleArrays.nonZeroCount(array, offset, length);
		int[] ixs=new int[nz];
		double[] data=new double[nz];
		this.data=data;
		int di=pos;
		for (int i=0; i<length; i++) {
			double x=array[offset+i];
			if (x==0.0) continue;
			ixs[di]=i;
			data[di]=x;
			di++;
		}
		index=Index.wrap(ixs);
	}
	
	@Override
	public void set(AVector v) {
		checkSameLength(v);
		
		if (v instanceof ADenseArrayVector) {
			set((ADenseArrayVector)v);
			return;
		} else if (v instanceof ASparseVector) {
            int[] nzi = v.nonZeroIndices();
            index=Index.wrap(nzi);
            if (nzi.length!=data.length) {
                data=new double[nzi.length];
            }
            for (int i=0; i<index.length(); i++) {
                double val=v.unsafeGet(index.get(i));
                data[i]=val;
            }
            return;
        } else {
            double[] data=this.data;
            int nz=(int) v.nonZeroCount();
            if (nz!=data.length) {
                data=new double[nz];
                this.data=data;
                index=Index.createLength(nz);
            }
            
            int di=0;
            for (int i=0; i<nz; i++) {
                double val=v.unsafeGet(i);
                if (val!=0) {
                    data[di]=val;
                    index.set(di, i);
                    di++;
                }
            }
        }
	}
	
	@Override
	public void set(ADenseArrayVector v) {
		checkSameLength(v);
		setElements(v.getArray(),v.getArrayOffset());
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		int ip=index.indexPosition(i);
		if (ip<0) {
			if (value==0.0) return;
			int npos=index.seekPosition(i);
			data=DoubleArrays.insert(data,npos,value);
			index=index.insert(npos,i);
		} else {
			data[ip]=value;
		}
	}
	
	@Override
	public void addAt(int i, double value) {
		// worth checking for zero when dealing with sparse vectors
		// can often avoid a relatively expensive index lookup
		if (value==0.0) return; 
		
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
	public Index nonSparseIndex() {
		return index;
	}

	@Override
	public boolean includesIndex(int i) {
		return index.indexPosition(i)>=0;
	}
	
	@Override
	public Vector toVector() {
		Vector v=Vector.createLength(length);
		double[] data=this.data;
		int[] ixs=index.data;
		for (int i=0; i<data.length; i++) {
			v.unsafeSet(ixs[i],data[i]);
		}	
		return v;
	}
	
	@Override
	public SparseIndexedVector clone() {
		return exactClone();
	}
	
	/**
	 * Include additional indices in the non-sparse index set of this vector.
	 * 
	 * Useful to improve performance if subsequent operations will access these indices.
	 * 
	 * @param ixs
	 */
	public void includeIndices(int[] ixs) {
		int[] nixs = IntArrays.mergeSorted(index.data,ixs);
		if (nixs.length==index.length()) return;
		int nl=nixs.length;
		double[] data=this.data;
		double[] ndata=new double[nl];
		int si=0;
        
		for (int i=0; i<nl; i++) {
            if (si>=data.length) break;
			int z=index.data[si];
			if (z==nixs[i]) {
				ndata[i]=data[si];
				si++; 
			}
		}
		this.data=ndata;
		index=Index.wrap(nixs);
	}
	
	/**
	 * Include additional indices in the non-sparse index set of this vector.
	 * 
	 * Useful to improve performance if subsequent operations will access these indices.
	 * 
	 * @param ixs
	 */
	public void includeIndices(Index ixs) {
		includeIndices(ixs.data);
	}
	
	/**
	 * Include additional indices in the non-sparse index set of this vector.
	 * 
	 * Useful to improve performance if subsequent operations will access these indices.
	 * 
	 * @param ixs
	 */
	public void includeIndices(AVector v) {
		if (v instanceof ASparseIndexedVector) {
			includeIndices((ASparseIndexedVector)v);
		} else {
			includeIndices(v.nonSparseIndex());
		}
	}
	
	public void includeIndices(ASparseIndexedVector v) {
		includeIndices(v.internalIndex());
	}
	
	@Override
	public SparseIndexedVector sparseClone() {
		return exactClone();
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
	
	@Override
	public AVector immutable() {
		return SparseImmutableVector.create(this);
	}

	@Override
	double[] internalData() {
		return data;
	}

	@Override
	Index internalIndex() {
		return index;
	}


}
