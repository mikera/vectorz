package mikera.vectorz.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Hashed sparse vector, intended for large vectors with very few randomly positioned non-zero elements. 
 * 
 * Maintains hash elements for non-zero values only. This is useful (and maybe better than SparseIndexedVector)
 * if random individual elements are likely to be changed, accessed or set back to zero on a frequent basis
 * 
 * Mutable in all elements, but performance will be reduced if density is high. In general, if density 
 * is more than about 1% then a dense Vector is likely to be better.
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
		if (length<=0) throw new IllegalArgumentException("Can't create SparseHashedVector of length "+length);
		hash=hashMap;
	}

	/**
	 * Creates a SparseHashedVector with the specified values
	 */
	public static SparseHashedVector create(AVector v) {
		Index ix=v.nonSparseIndex();
		int n=ix.length();
		HashMap<Integer,Double> hm=new HashMap<Integer,Double>(n);
		for (int i=0; i<n; i++) {
			int ii=ix.get(i);
			double val=v.unsafeGet(ii);
			if (val!=0.0) hm.put(ii,val);
		}
		return new SparseHashedVector(v.length(),hm);
	}
	
	/**
	 * Create a SparseHashedVector with specified non-zero indexes and values.
	 */
	public static SparseHashedVector create(int length, Index index, Vector values) {
		int n=index.length();
		if (values.length()!=n) throw new IllegalArgumentException("Mismatched values length: "+values.length());
		HashMap<Integer,Double> hm=new HashMap<Integer,Double>();
		for (int i=0; i<n; i++) {
			double v=values.get(i);
			if (v!=0.0) hm.put(index.get(i), v);
		}
		
		return new SparseHashedVector(length, hm);
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
		checkIndex(i);
		return unsafeGet(i);
	}
	
	@Override
	public double unsafeGet(int i) {
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
		return true;
	}
	
	@Override
	public long nonZeroCount() {
		return hash.size();
	}
	
	@Override
	public void multiply (double d) {
		if (d==1.0) return;
		if (d==0.0) {
			hash.clear();
			return;
		}
		for (Entry<Integer,Double> e:hash.entrySet()) {
			double r=e.getValue()*d;
			e.setValue(r);
		}
	}
	
	@Override
	public double dotProduct(AVector v) {
		v.checkLength(length);
		double result=0.0;
		for (int i: hash.keySet()) {
			result+=hash.get(i)*v.unsafeGet(i);
		}
		return result;
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (Integer i: hash.keySet()) {
			result+=hash.get(i)*data[offset+i];
		}
		return result;
	}
	
	@Override
	public double dotProduct(double[] data, int offset, int stride) {
		double result=0.0;
		for (Integer i: hash.keySet()) {
			result+=hash.get(i)*data[offset+i*stride];
		}
		return result;
	}
	
	@Override
	public void addMultipleToArray(double factor,int offset, double[] array, int arrayOffset, int length) {
		int aOffset=arrayOffset-offset;

		for (Integer i: hash.keySet()) {
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
	public void addToArray(double[] dest, int offset, int stride) {
		for (Entry<Integer,Double> e: hash.entrySet()) {
			int i=e.getKey();
			dest[offset+i*stride]+=e.getValue();
		}
	}
	
	@Override
	public void addProductToArray(double factor, int offset, AVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		int aOffset=arrayOffset-offset;
		int oOffset=otherOffset-offset;

		for (Entry<Integer,Double> e: hash.entrySet()) {
			Integer io=e.getKey();
			int i=io;
			if ((i<offset)||(i>=(offset+length))) continue;
			array[aOffset+i]+=factor*e.getValue()*other.get(i+oOffset);
		}
	}
	
	@Override
	public void addProductToArray(double factor, int offset, ADenseArrayVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		int aOffset=arrayOffset-offset;
		int oArrayOffset=other.getArrayOffset()+otherOffset-offset;
		double[] oArray=other.getArray();
		
		for (Entry<Integer,Double> e: hash.entrySet()) {
			Integer io=e.getKey();
			int i=io;
			if ((i<offset)||(i>=(offset+length))) continue;
			double ov=oArray[i+oArrayOffset];
			if (ov!=0.0) array[aOffset+i]+=factor*e.getValue()*ov;
		}
	}
	
	@Override public void getElements(double[] array, int offset) {
		Arrays.fill(array,offset,offset+length,0.0);
		copySparseValuesTo(array,offset);
	}
	
	public void copySparseValuesTo(double[] array, int offset) {
		for (Entry<Integer,Double> e: hash.entrySet()) {
			int i=e.getKey();
			array[offset+i]=e.getValue();
		}
	}
	
	@Override public void copyTo(AVector v, int offset) {
		if (v instanceof ADenseArrayVector) {
			ADenseArrayVector av=(ADenseArrayVector)v;
			getElements(av.getArray(),av.getArrayOffset()+offset);
		}
		v.fillRange(offset,length,0.0);
		for (Entry<Integer,Double> e: hash.entrySet()) {
			int i=e.getKey();
			v.unsafeSet(offset+i,e.getValue());
		}
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		if (value!=0.0) {	
			hash.put(i, value);
		} else {
			hash.remove(i);
		}
	}
	
	@Override
	public void set(AVector v) {
		if (v instanceof SparseHashedVector) {
			set((SparseHashedVector) v);
			return;
		}
		
		v.checkLength(length);
		hash=new HashMap<Integer, Double>();
		
		for (int i=0; i<length; i++) {
			double val=v.unsafeGet(i);
			if (val!=0) {
				hash.put(i, val);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void set(SparseHashedVector v) {
		v.checkLength(length);
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
	
	/**
	 * Special method to allow re-use of integer instances as keys
	 * 
	 * @param i
	 * @param value
	 */
	private void unsafeSetInteger(Integer i, double value) {
		if (value!=0.0) {	
			hash.put(i, value);
		} else {
			hash.remove(i);
		}
	}
	
	@Override
	public void addAt(int i, double value) {
		if (value==0.0) return;
		Integer ind=i;
		unsafeSetInteger(ind, value+unsafeGet(ind));
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
	public double elementMax() {
		double result=-Double.MAX_VALUE;
		for (Map.Entry<Integer,Double> e:hash.entrySet()) {
			double d=e.getValue();
			if (d>result) {
				result=d; 
			}
		}
		if ((result<0)&&(hash.size()<length)) {
			return 0.0;
		}
		return result;
	}
	
	
	@Override
	public double elementMin() {
		double result=Double.MAX_VALUE;
		for (Map.Entry<Integer,Double> e:hash.entrySet()) {
			double d=e.getValue();
			if (d<result) {
				result=d; 
			}
		}
		if ((result>0)&&(hash.size()<length)) {
			return 0.0;
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
	public double elementSum() {
		double result=0.0;
		for (Map.Entry<Integer,Double> e:hash.entrySet()) {
			double d=e.getValue();
			result+=d;
		}
		return result;
	}
	
	@Override
	public double elementSquaredSum() {
		double result=0.0;
		for (Map.Entry<Integer,Double> e:hash.entrySet()) {
			double d=e.getValue();
			result+=d*d;
		}
		return result;
	}

	@Override
	public Vector nonSparseValues() {
		int n=hash.size();
		double[] vs=new double[n];
		Index index=nonSparseIndex();
		for (int i=0; i<n; i++) {
			vs[i]=hash.get(index.get(i));
		}
		return Vector.wrap(vs);
	}
	
	@Override
	public int[] nonZeroIndices() {
		int n=hash.size();
		int[] ret=new int[n];
		int di=0;
		for (Entry<Integer,Double> e: hash.entrySet()) {
			ret[di++]=e.getKey();
		}
		Arrays.sort(ret);
		return ret;
	}
	
	@Override
	public Index nonSparseIndex() {
		int n=hash.size();
		int[] in=new int[n];
		int di=0;
		for (Map.Entry<Integer,Double> e:hash.entrySet()) {
			in[di++]=e.getKey();
		}
		Index result=Index.wrap(in);
		result.sort();
		return result;
	}

	@Override
	public boolean includesIndex(int i) {
		return hash.containsKey(i);
	}

	@Override
	public void add(ASparseVector v) {
		Index ind=v.nonSparseIndex();
		int n=ind.length();
		for (int i=0; i<n; i++) {
			int ii=ind.get(i);
			addAt(ii,v.unsafeGet(ii));
		}
	}

	@Override
	public boolean equalsArray(double[] data, int offset) {
		for (int i=0; i<length; i++) {
			double v=data[offset+i];
			if (v==0.0) {
				if (hash.containsKey(i)) return false;
			} else {
				Double d=hash.get(i);
				if ((d==null)||(d!=v)) return false;
			}
		}
		return true;
	}
	
	@Override
	public SparseIndexedVector clone() {
		return sparseClone();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SparseHashedVector exactClone() {
		return new SparseHashedVector(length,(HashMap<Integer, Double>) hash.clone());
	}
	
	@Override
	public SparseIndexedVector sparseClone() {
		// by default switch to SparsIndexedVector: will normally be faster
		return SparseIndexedVector.create(this);
	}
	
	@Override
	public void validate() {
		if (length<=0) throw new VectorzException("Illegal length: "+length);
		for (Entry<Integer, Double> e:hash.entrySet()) {
			int i=e.getKey();
			if ((i<0)||(i>=length)) throw new VectorzException(ErrorMessages.invalidIndex(this, i));
			if (e.getValue()==0.0) throw new VectorzException("Unexpected zero at index: "+i);
		}
		super.validate();
	}
}
