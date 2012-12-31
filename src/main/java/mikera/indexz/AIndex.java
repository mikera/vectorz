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
public abstract class AIndex  implements Serializable, Cloneable, Comparable<AIndex>, Iterable<Integer> {
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
	
	public void set(int i, int value) {
		throw new UnsupportedOperationException();
	}
	
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
	
	public List<Integer> toList() {
		int len=length();
		ArrayList<Integer> al=new ArrayList<Integer>();
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
	 * Returns true if this index is sorted (in strictly increasing order)
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

	public boolean contains(int index) {
		int len=length();
		for (int i=0; i<len; i++) {
			if (get(i)==index) return true;
		}
		return false;
	}
	
	public boolean contains(Index inds) {
		int len=inds.length();
		for (int i=0; i<len; i++) {
			if (!contains(inds.get(i))) return false;
		}
		return true;
	}
	
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
	
	public IndexIterator iterator() {
		return new IndexIterator(this);
	}
	
	public Index compose(AIndex a) {
		int len=this.length();
		Index r=new Index(len);
		for (int i=0; i<len; i++) {
			r.data[i]=a.get(get(i));
		}
		return r;
	}
}
