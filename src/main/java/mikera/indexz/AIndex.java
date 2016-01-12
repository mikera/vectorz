package mikera.indexz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import mikera.indexz.impl.IndexIterator;
import mikera.vectorz.Tools;

/**
 * Abstract base class for a list of integer indexes
 * 
 * @author Mike
 */
@SuppressWarnings("serial")
public abstract class AIndex implements Serializable, Cloneable, Comparable<AIndex>, Iterable<Integer> {
	// ===================================
	// Abstract interface
	
	/**
	 * Gets the index value at position i
	 * 
	 * @param i
	 * @return
	 */
	public abstract int get(int i);

	/**
	 * Returns the length of this index list
	 * 
	 * @return
	 */
	public abstract int length();
	
	// ===================================
	// Common implementations
	
	public abstract void set(int i, int value);
	
	public boolean isFullyMutable() {
		return false;
	}
	
	public void copyTo(int[] array, int offset) {
		int len=length();
		for (int i=0; i<len; i++) {
			array[offset+i]=get(i);
		}
	}
	
	public int[] toArray() {
		int len=length();
		int[] arr=new int[len];
		copyTo(arr,0);
		return arr;
	}
	
	public Index toIndex() {
		int len=length();
		int[] arr=new int[len];
		copyTo(arr,0);
		return Index.wrap(arr);
	}
	
	public List<Integer> toList() {
		int len=length();
		ArrayList<Integer> al=new ArrayList<Integer>(len);
		for (int i=0; i<len; i++) {
			al.add(get(i));
		}
		return al;
	}
	
	public void swap(int i, int j) {
		int t=get(i);
		set(i,get(j));
		set(j,t);
	}
	
	public void reverse() {
		final int len=length();
		int m=len/2;
		for (int i=0; i<m; i++) {
			swap(i,(len-1)-i);
		}
	}
	
	public int minIndex() {
		int len=length();
		int min=get(0);
		for (int i=1; i<len; i++) {
			int x=get(i);
			if (x<min) min=x;
		}
		return min;
	}

	public int maxIndex() {
		int len=length();
		int max=get(0);
		for (int i=1; i<len; i++) {
			int x=get(i);
			if (x>max) max=x;
		}
		return max;
	}
	
	/**
	 * Returns true if this index is sorted (in increasing order)
	 * @return
	 */
	public boolean isSorted() {
		int len=length();
		for (int i=1; i<len; i++) {
			if (get(i-1)>get(i)) return false;
		}
		return true;
	}
	
	/**
	 * Returns true if this index is distinct and sorted (in strictly increasing order)
	 * @return
	 */
	public boolean isDistinctSorted() {
		int len=length();
		for (int i=1; i<len; i++) {
			if (get(i-1)>=get(i)) return false;
		}
		return true;
	}
	
	/**
	 * Sorts the index in place. May not be supported by some AIndex implementations
	 * e.g. if the index is immutable.
	 */
	public void sort() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns true if this Index contains only distinct integer indices
	 * @return
	 */
	public boolean isDistinct() {
		HashSet<Integer> hs=new HashSet<Integer>();
		int len=length();
		for (int i=0; i<len; i++) {
			Integer v=get(i);
			if (hs.contains(v)) return false;
			hs.add(v);
		}
		return true;
	}
	
	/**
	 * Returns true if this index represents a permutation of positions 0..length-1
	 * @return
	 */
	public boolean isPermutation() {
		if (!isWithinRange(0,length())) return false;
		if (!isDistinct()) return false;
		return true;
	}
	
	private boolean isWithinRange(int start, int length) {
		int len=length();
		for (int i=0; i<len; i++) {
			int v=get(i)-start;
			if ((v<0)||(v>=length)) return false;
		}
		return true;
	}

	/**
	 * Returns true if the index contains a specific value.
	 * Performs a full scan of the index (need not be in sorted order)
	 * @param index
	 * @return
	 */
	public boolean contains(int index) {
		int len=length();
		for (int i=0; i<len; i++) {
			if (get(i)==index) return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the index contains a specific value.
	 * Assumes the index is in sorted order.
	 * @param index
	 * @return
	 */
	public boolean containsSorted(int index) {
		return contains(index);
	}

	public boolean contains(Index inds) {
		int len=inds.length();
		for (int i=0; i<len; i++) {
			if (!contains(inds.get(i))) return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AIndex) return equals((AIndex)o);
		return false;
	}
	
	public boolean equals(AIndex o) {
		int len=length();
		if (len!=o.length()) return false;
		for (int i=0; i<len; i++) {
			if (get(i)!=o.get(i)) return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		int len=length();
		for (int i = 0; i < len; i++) {
			hashCode = 31 * hashCode + (Tools.hashCode(get(i)));
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		int length=length();
		sb.append('[');
		if (length>0) {
			sb.append(get(0));
			for (int i = 1; i < length; i++) {
				sb.append(',');
				sb.append(get(i));
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	@Override
	public AIndex clone() {
		try {
			return (AIndex) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
	
	@Override
	public int compareTo(AIndex a) {
		int len=length();
		int alen=a.length();
		if (len!=alen) return len-alen;
		
		for (int i=0; i<len; i++) {
			int d=get(i)-a.get(i);
			if (d!=0) return d;
		}
		return 0;
	}
	
	/**
	 * Gets an iterator over all elements of this index
	 */
	@Override
	public IndexIterator iterator() {
		return new IndexIterator(this);
	}
	
	/**
	 * Composes this index with a second index, returning a new index.
	 * 
	 * New index satisfies index.get(i) == a.get(this.get(i));
	 * 
	 * @param a
	 * @return
	 */
	public Index compose(AIndex a) {
		int len=this.length();
		Index r=new Index(len);
		for (int i=0; i<len; i++) {
			r.data[i]=a.get(this.get(i));
		}
		return r;
	}

	/**
	 * Looks up an index value in the index, returning its position or -1 if not found
	 * Index must be both sorted and distinct.
	 * @param x
	 * @return
	 */	
	public abstract int indexPosition(int x);
	
	/**
	 * Returns the position at which an index value exists or should be inserted at in an index
	 * Index must be both sorted and distinct.
	 * @param i
	 * @return The target position in the index, which will satisfy 0 <= position <= this.length()
	 */
	public int seekPosition(int i) {
		return toIndex().seekPosition(i);
	}

	public abstract AIndex exactClone();

	/**
	 * Gets the last index value in this index. 
	 * Throws an exception if the index is empty
	 * @return
	 */
	public abstract int last();



}
