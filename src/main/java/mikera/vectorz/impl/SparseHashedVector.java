package mikera.vectorz.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Hashed sparse vector. Mutable in all elements, but performance will be reduced if density is high.
 * 
 * In general, if density is more than about 10% then a dense Vector is likely to be better.
 * 
 * Index must be distinct and sorted.
 * 
 * @author Mike
 *
 */
public class SparseHashedVector extends ASparseVector {
	private static final long serialVersionUID = 750093598603613879L;

	private final int length;
	private final HashMap<Integer,Double> hash;
	
	private SparseHashedVector(int length) {
		this(length, new HashMap<Integer,Double>());
	}
	
	public SparseHashedVector(int length, HashMap<Integer, Double> hashMap) {
		hash=hashMap;
		this.length=length;
	}

	/**
	 * Creates a SparseIndexedVector with the specified index and data values.
	 * Performs no checking - Index must be distinct and sorted.
	 */
	public static SparseHashedVector create(AVector v) {
		int n=v.length();
		SparseHashedVector sv=new SparseHashedVector(n);
		for (int i=0; i<n; i++) {
			double val=v.unsafeGet(i);
			if (val!=0) sv.set(i,val);
		}
		return sv;
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
	public int length() {
		return length;
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
	public void unsafeSet(int i, double value) {
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
	
	@Override
	public SparseHashedVector exactClone() {
		return new SparseHashedVector(length,(HashMap<Integer, Double>) hash.clone());
	}


}
