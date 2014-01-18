package mikera.vectorz.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Hashed sparse vector, intended for large vectors with a few randomly positioned non-zero elements. 
 * 
 * Mutable in all elements, but performance will be reduced if density is high. In general, if density 
 * is more than about 10% then a dense Vector is likely to be better.
 * 
 * @author Mike
 *
 */
public class SparseHashedVector extends ASparseVector {
	private static final long serialVersionUID = 750093598603613879L;

	private HashMap<Integer,Double> hash;
	
	private SparseHashedVector(int length) {
		this(length, new HashMap<Integer,Double>());
	}
	
	private SparseHashedVector(int length, HashMap<Integer, Double> hashMap) {
		super(length);
		hash=hashMap;
	}

	/**
	 * Creates a SparseIndexedVector with the specified index and data values.
	 * Performs no checking - Index must be distinct and sorted.
	 */
	public static SparseHashedVector create(AVector v) {
		int n=v.length();
		if (n==0) throw new IllegalArgumentException(ErrorMessages.incompatibleShape(v));
		HashMap<Integer,Double> hm=new HashMap<Integer,Double>();
		for (int i=0; i<n; i++) {
			double val=v.unsafeGet(i);
			if (val!=0) hm.put(i,val);
		}
		return new SparseHashedVector(n,hm);
	}
	
	public static SparseHashedVector createLength(int length) {
		return new SparseHashedVector(length);
	}
	
	/** Creates a SparseIndexedVector from a row of an existing matrix */
	public static AVector createFromRow(AMatrix m, int row) {
		return create(m.getRow(row));
	}
	
	@Override
	public int nonSparseElementCount() {
		return hash.size();
	}
	
	@Override
	public boolean isZero() {
		return hash.size()==0;
	}
	
	@Override
	public boolean isElementConstrained() {
		return false;
	}

	@Override
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this,i));
		return unsafeGet(i);
	}
	
	@Override
	public double unsafeGet(int i) {
		Double d= hash.get(i);
		if (d!=null) return d;
		return 0.0;
	}
	
	@Override
	public double unsafeGetInteger(Integer i) {
		Double d= hash.get(i);
		if (d!=null) return d;
		return 0.0;
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
	public long nonZeroCount() {
		return hash.size();
	}
	
	@Override
	public void multiply (double d) {
		if (d==1.0) return;
		if (d==0.0) {
			hash=new HashMap<Integer, Double>();
			return;
		}
		for (Integer i: hash.keySet()) {
			double r=hash.get(i)*d;
			unsafeSetInteger(i,r);
		}
	}
	
	@Override
	public double dotProduct(AVector v) {
		if (length!=v.length()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));
		double result=0.0;
		for (int i: hash.keySet()) {
			result+=hash.get(i)*v.unsafeGet(i);
		}
		return result;
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i: hash.keySet()) {
			result+=hash.get(i)*data[offset+i];
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

		for (int i: hash.keySet()) {
			if ((i<offset)||(i>=(offset+length))) continue;
			array[aOffset+i]+=factor*hash.get(i);
		}
	}
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		int aOffset=arrayOffset-offset;
		
		for (int i: hash.keySet()) {
			if ((i<offset)||(i>=(offset+length))) continue;
			array[aOffset+i]+=hash.get(i);
		}
	}
	
	@Override
	public void addProductToArray(double factor, int offset, AVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		int aOffset=arrayOffset-offset;

		for (int i: hash.keySet()) {
			if ((i<offset)||(i>=(offset+length))) continue;
			array[aOffset+i]+=factor*hash.get(i)*other.get(i+otherOffset);
		}
	}
	
	@Override
	public void addProductToArray(double factor, int offset, AArrayVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		int aOffset=arrayOffset-offset;
		int oOffset=otherOffset-offset;

		// TODO: faster array version
		for (int i: hash.keySet()) {
			if ((i<offset)||(i>=(offset+length))) continue;
			array[aOffset+i]+=factor*hash.get(i)*other.get(i+oOffset);
		}
	}
	
	@Override public void getElements(double[] array, int offset) {
		Arrays.fill(array,offset,offset+length,0.0);
		copySparseValuesTo(array,offset);
	}
	
	public void copySparseValuesTo(double[] array, int offset) {
		for (int i: hash.keySet()) {
			array[offset+i]=hash.get(i);
		}
	}
	
	@Override public void copyTo(AVector v, int offset) {
		if (v instanceof AArrayVector) {
			AArrayVector av=(AArrayVector)v;
			getElements(av.getArray(),av.getArrayOffset()+offset);
		}
		v.fillRange(offset,length,0.0);
		for (int i: hash.keySet()) {
			v.unsafeSet(offset+i,hash.get(i));
		}
	}

	@Override
	public void set(int i, double value) {
		if ((i<0)||(i>=length))  throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		if (value!=0.0) {	
			hash.put(i, value);
		} else {
			hash.remove(i);
		}
	}
	
	@Override
	public void set(AVector v) {
		if (v.length()!=length) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));
		
		if (v instanceof SparseHashedVector) {
			set((SparseHashedVector) v);
			return;
		}
		
		for (int i=0; i<length; i++) {
			double val=v.unsafeGet(i);
			if (val!=0) {
				hash.put(i, val);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void set(SparseHashedVector v) {
		hash=(HashMap<Integer, Double>) v.hash.clone();
	}

	
	@Override
	public void unsafeSet(int i, double value) {
		if (value!=0.0) {	
			hash.put(i, value);
		} else {
			hash.remove(i);
		}
	}
	
	@Override
	public void unsafeSetInteger(Integer i, double value) {
		if (value!=0.0) {	
			hash.put(i, value);
		} else {
			hash.remove(i);
		}
	}
	
	@Override
	public void addAt(int i, double value) {
		unsafeSet(i, value+unsafeGet(i));
	}
	
	@Override
	public double maxAbsElement() {
		double result=0.0;
		for (Map.Entry<Integer,Double> e:hash.entrySet()) {
			double d=Math.abs(e.getValue());
			if (d>result) {
				result=d; 
			}
		}
		return result;
	}
	
	@Override
	public int maxElementIndex(){
		if (hash.size()==0) return 0;
		int ind=0;
		double result=-Double.MAX_VALUE;
		for (Map.Entry<Integer,Double> e:hash.entrySet()) {
			double d=e.getValue();
			if (d>result) {
				result=d; 
				ind=e.getKey();
			}
		}
		if ((result<0)&&(hash.size()<length)) {
			return sparseElementIndex();
		}
		return ind;
	}
	
 
	@Override
	public int maxAbsElementIndex(){
		if (hash.size()==0) return 0;
		int ind=0;
		double result=unsafeGet(0);
		for (Map.Entry<Integer,Double> e:hash.entrySet()) {
			double d=Math.abs(e.getValue());
			if (d>result) {
				result=d; 
				ind=e.getKey();
			}
		}
		return ind;
	}
	
	@Override
	public int minElementIndex(){
		if (hash.size()==0) return 0;
		int ind=0;
		double result=Double.MAX_VALUE;
		for (Map.Entry<Integer,Double> e:hash.entrySet()) {
			double d=e.getValue();
			if (d<result) {
				result=d; 
				ind=e.getKey();
			}
		}
		if ((result>0)&&(hash.size()<length)) {
			return sparseElementIndex();
		}
		return ind;
	}
	
	/**
	 * Return this index of a sparse zero element, or -1 if not sparse
	 * @return
	 */
	private int sparseElementIndex() {
		if (hash.size()==length) {
			return -1;
		}
		for (int i=0; i<length; i++) {
			if (!hash.containsKey(i)) return i;
		}
		throw new VectorzException(ErrorMessages.impossible());
	}

	@Override
	public Vector nonSparseValues() {
		// TODO: much faster implementation needed!
		ArrayList<Double> al=new ArrayList<Double>(); 	
		for (int i=0; i<length; i++) {
			double d=unsafeGet(i);
			if (d!=0.0) al.add(d);
		}
		return Vector.create(al);
	}
	
	@Override
	public Index nonSparseIndexes() {
		return Index.createSorted(hash.keySet());
	}

	@Override
	public boolean includesIndex(int i) {
		return hash.containsKey(i);
	}
	
	@Override
	public Vector clone() {
		Vector v=Vector.createLength(length);
		this.copySparseValuesTo(v.data, 0);
		return v;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SparseHashedVector exactClone() {
		return new SparseHashedVector(length,(HashMap<Integer, Double>) hash.clone());
	}


}
