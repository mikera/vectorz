package mikera.indexz;

import java.io.Serializable;
import java.util.HashSet;

import mikera.vectorz.Tools;

/**
 * Abstract base class for Index functionality
 * 
 * @author Mike
 */
@SuppressWarnings("serial")
public abstract class AIndex  implements Serializable, Cloneable {
	// ===================================
	// Abstract interface
	
	public abstract int get(int i);

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
	

	public boolean isSorted() {
		int len=length();
		for (int i=1; i<len; i++) {
			if (get(i-1)>get(i)) return false;
		}
		return true;
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
}
